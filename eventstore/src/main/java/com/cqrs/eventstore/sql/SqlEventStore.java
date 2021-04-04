package com.cqrs.eventstore.sql;

import java.nio.charset.Charset;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.TransactionStatus;
import org.skife.jdbi.v2.VoidTransactionCallback;
import org.skife.jdbi.v2.exceptions.CallbackFailedException;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cqrs.eventstore.ConcurrencyException;
import com.cqrs.eventstore.EventStore;
import com.cqrs.eventstore.sql.dbi.EventStreams;
import com.cqrs.messaging.Deserializer;
import com.cqrs.messaging.Event;
import com.cqrs.messaging.JsonDeserializer;
import com.cqrs.messaging.JsonSerializer;
import com.cqrs.messaging.Serializer;

public class SqlEventStore implements EventStore {
    private static final Logger LOG = LoggerFactory.getLogger(SqlEventStore.class);
    private final DBI dbi;
    private final Serializer serializer;
    private final Deserializer deserializer;

    private SqlEventStore(DBI dbi, Serializer serializer, Deserializer deserializer) {
        this.dbi = dbi;
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    public static class Builder {

        private String databaseUrl;
        private String databaseUser;
        private String databasePassword;
        private Serializer serializer;
        private Deserializer deserializer;

        public Builder setDatabasePassword(String databasePassword) {
            this.databasePassword = databasePassword.trim();
            return this;
        }

        public Builder setDatabaseUser(String databaseUser) {
            this.databaseUser = databaseUser.trim();
            return this;
        }

        public Builder setDatabaseUrl(String databaseUrl) {
            this.databaseUrl = databaseUrl.trim();
            return this;
        }

        public Builder setSerializer(Serializer serializer) {
            this.serializer = serializer;
            return this;
        }

        public Builder setDeserializer(Deserializer deserializer) {
            this.deserializer = deserializer;
            return this;
        }

        public SqlEventStore build() {
            MysqlDataSource ds = new MysqlConnectionPoolDataSource();
            ds.setUrl(databaseUrl);
            if (databaseUser != null && !databaseUser.isEmpty()) {
                ds.setUser(databaseUser);
            }
            if (databasePassword != null && !databasePassword.isEmpty()) {
                ds.setPassword(databasePassword);
            }
            LOG.info("JDBC URL : " + databaseUrl);
            String dbName = extractDBNameFromJdbcURL(databaseUrl);
            LOG.info("DB name extracted from JDBC URL : " + dbName);
            updateDatabaseSchema(dbName);
            return new SqlEventStore(new DBI(ds), serializer != null ? serializer : new JsonSerializer(),
                    deserializer != null ? deserializer : new JsonDeserializer());
        }
        
        /*
        Parse ConnectorJ compatible urls
        jdbc:mysql://host:port/database
        example : "jdbc:mysql://localhost:3306/eventstore?useSSL=false&paramName=paramValue"
        if JDBC URL is malformed or not in expected format, then use eventstore as default DB name 
         */
        private String extractDBNameFromJdbcURL(String url) {
            if (!url.startsWith("jdbc:mysql://")) {
                return "eventstore";
            }
            url = url.substring(13);
            String[] tokens = url.split("/");
            String dbName=(tokens.length > 1)?tokens[1].split("\\?")[0]:"eventstore";
            return dbName;
        }
        

        private void updateDatabaseSchema(String dbName) {
            Properties props = System.getProperties();
            props.setProperty("database.name", dbName);
            LOG.info("system property for database.name :" + System.getProperty("database.name"));
            
            try {
                ResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor();
                Database db = DatabaseFactory.getInstance().openDatabase(databaseUrl, databaseUser, databasePassword,
                        null, resourceAccessor);
                try {
                    Liquibase liquibase = new Liquibase("db/changelog/db.eventstore.mysql.xml", resourceAccessor, db);
                    liquibase.update("");
                    LOG.info("updated liquibase DB schema..");

                } finally {
                    db.close();
                }
            } catch (Exception e) {
                throw new EventStoreMigrationException("Can not execute db migrations.", e);
            }
        }
    }

    @Override
    public void save(String streamName, String streamId, Iterable<? extends Event> events, int expectedVersion) {
        final Integer[] currentVersion = { 0 };
        try {
            dbi.inTransaction(new VoidTransactionCallback() {
                @Override
                protected void execute(Handle handle, TransactionStatus status) {
                    EventStreams streams = handle.attach(EventStreams.class);
                    currentVersion[0] = streams.getCurrentStreamVersion(streamId);
                    int version = expectedVersion;
                    if (expectedVersion == -1) {
                        LOG.debug("Aggregate has unknown expected version [{}]", expectedVersion);
                        version = currentVersion[0] == null ? 0 : currentVersion[0];
                    }
                    version = persistEventsInStream(streams, version, events, streamId);
                    if (currentVersion[0] == null) // this means a new stream
                    {
                        LOG.debug("Creating new Event Stream for aggregate [{}] with Id [{}] with total count [{}]",
                                streamName, streamId, version);
                        streams.createNewStream(streamName, streamId, version);

                    } else {
                        LOG.debug("Setting new current verison [{}] for aggregate [{}] with Id [{}]", version,
                                streamName, streamId);
                        streams.setCurrentStreamVersion(streamId, version);
                    }
                }
            });
        } catch (CallbackFailedException e) {
            if (e.getCause() instanceof UnableToExecuteStatementException
                    && e.getCause().getCause() instanceof SQLIntegrityConstraintViolationException) {
                throw new ConcurrencyException(streamId, currentVersion[0], expectedVersion);
            } else {
                throw e;
            }
        }
    }

    @Override
    public Iterable<? extends Event> getById(String streamId) {
        return dbi.withHandle(handle -> {
            EventStreams streams = handle.attach(EventStreams.class);
            Iterable<Map> iterable = streams.loadEvents(streamId);
            return StreamSupport.stream(iterable.spliterator(), false).map(entry -> {
                try {
                    Class cls = Class.forName(String.valueOf(entry.get("type")));
                    return (Event) deserializer.deserialize(String.valueOf(entry.get("data")), cls);
                } catch (ClassNotFoundException e) {
                    LOG.error(e.getMessage(), e);
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());
        });
    }

    private int persistEventsInStream(EventStreams streams, int version, Iterable<? extends Event> events,
            String streamId) {
        for (Event event : events) {
            version++;
            streams.appendNewEvent(streamId, event.getEventId().toString(),
                    serializer.serialize(event).getBytes(Charset.forName("UTF-8")), typeOf(event), event.getVersion());

            // TODO: refactor eventPublisher to aggregateRepo to keep SOLID
        }
        return version;
    }

    private static String typeOf(Event event) {
        return event.getClass().getCanonicalName();
    }
}
