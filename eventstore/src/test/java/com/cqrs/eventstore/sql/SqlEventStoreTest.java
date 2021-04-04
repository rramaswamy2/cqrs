package com.cqrs.eventstore.sql;

import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.junit.jupiter.api.AfterAll;

import com.cqrs.eventstore.EventStore;
import com.cqrs.eventstore.sql.SqlEventStore;
import com.cqrs.messaging.Event;
import com.cqrs.messaging.JsonSerializer;
import com.cqrs.messaging.Serializer;

public abstract class SqlEventStoreTest {
    protected EventStore store;
    protected static MysqlDataSource dataSource = null;
    protected JsonSerializer serializer = new JsonSerializer();

    public SqlEventStoreTest() {
        dataSource = new MysqlConnectionPoolDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/eventstore");
        dataSource.setUser("event");
        dataSource.setPassword("store");
        try {
            dataSource.setUseSSL(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Serializer serializer = new JsonSerializer();
        store = new SqlEventStore.Builder()
                .setDatabaseUrl("jdbc:mysql://localhost:3306/eventstore?useSSL=false")
                .setDatabaseUser("event")
                .setDatabasePassword("store")
                .setSerializer(serializer)
                .build();
    }

    protected void storeEvents(String streamId, List<Event> events, String type) {
        events.forEach(event -> executeInsertStatementForEvents(event, streamId));

        try {
            executeInsertStatementForStream(streamId, type, events);
        } catch (SQLException e) {
            //
        }
    }

    private void executeInsertStatementForEvents(Event event, String streamId) {

        try {
            PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                    "insert into events (event_id, data, type, version, stream_id) values (?, ?, ?, ?, ?)");
            preparedStatement.setString(1, event.getEventId().toString());
            preparedStatement.setBytes(2, serializer.serialize(event).getBytes(Charset.forName("UTF-8")));
            preparedStatement.setString(3, event.getClass().getCanonicalName());
            preparedStatement.setInt(4, event.getVersion());
            preparedStatement.setString(5, streamId);
            preparedStatement.execute();
        } catch (SQLException e) {

        }
    }

    private void executeInsertStatementForStream(String streamId, String type, List<Event> events) throws SQLException {
        int version = events.stream().mapToInt(Event::getVersion).max().getAsInt();
        PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "insert into streams (id, type, version) values (?, ?, ?)");
        preparedStatement.setString(1, streamId);
        preparedStatement.setString(2, type);
        preparedStatement.setInt(3, version);
        preparedStatement.execute();
    }

    @AfterAll
    public static void teardown() throws SQLException {

        dataSource.getConnection().createStatement().execute("truncate table events");
        dataSource.getConnection().createStatement().execute("truncate table streams");
        dataSource.getConnection().createStatement().execute("truncate table DATABASECHANGELOGLOCK");

    }

    protected static <T> List<T> toList(Iterable<T> iterable) {
        if (iterable instanceof List) {
            return (List<T>) iterable;
        }
        return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
    }
}
