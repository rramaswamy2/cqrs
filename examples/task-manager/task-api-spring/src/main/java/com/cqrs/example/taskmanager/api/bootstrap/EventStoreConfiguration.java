package com.cqrs.example.taskmanager.api.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cqrs.eventstore.EventStore;
import com.cqrs.eventstore.local.InMemoryEventStore;
import com.cqrs.eventstore.sql.SqlEventStore;
import com.cqrs.messaging.JsonSerializer;

@Configuration
public class EventStoreConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(EventStoreConfiguration.class);
    private final String databaseUrl;
    private final String databaseUser;
    private final String databasePassword;

    private final boolean mysqlEventStoreEnabled;

    public EventStoreConfiguration(@Value("${eventstore.datasource.url}") String databaseUrl,
            @Value("${eventstore.datasource.username}") String databaseUser,
            @Value("${eventstore.datasource.password}") String databasePassword,
            @Value("${eventstore.mysql.enabled:true}") boolean mysqlEventStoreEnabled) {
        this.databaseUrl = databaseUrl;
        this.databaseUser = databaseUser;
        this.databasePassword = databasePassword;
        this.mysqlEventStoreEnabled = mysqlEventStoreEnabled;
    }

    /*
     * create the event store.
     */
    @Bean
    public EventStore eventStore() {

        if (this.mysqlEventStoreEnabled) {
            LOG.info("plugging mySQL as event store..");
            return new SqlEventStore.Builder().setDatabaseUrl(databaseUrl).setDatabaseUser(databaseUser)
                    .setDatabasePassword(databasePassword).setSerializer(new JsonSerializer()).build();
        } else {

            LOG.info("plugging in-memory event store..");
            return new InMemoryEventStore();
        }
    }

}
