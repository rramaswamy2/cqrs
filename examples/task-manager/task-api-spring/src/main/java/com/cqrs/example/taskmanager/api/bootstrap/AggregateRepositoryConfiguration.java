package com.cqrs.example.taskmanager.api.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cqrs.domain.AggregateRepository;
import com.cqrs.domain.Repository;
import com.cqrs.eventbus.EventPublisher;
import com.cqrs.eventstore.EventStore;
import com.cqrs.example.taskmanager.domain.Task;
import com.cqrs.messaging.AggregateCache;

@Configuration
public class AggregateRepositoryConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(AggregateRepositoryConfiguration.class);
    private final EventStore eventStore;
    private final EventPublisher publisher;
    private final AggregateCache cache;
    private boolean aggregateCachingEnabled;

    public AggregateRepositoryConfiguration(EventStore eventStore, EventPublisher publisher, @Autowired(required=false) AggregateCache cache, @Value("${redis.aggregate.caching.enabled}") String cachingEnabled) {

        LOG.info("value of app property or docker environment variable for redis.aggregate.caching.enabled : " + cachingEnabled);
        this.eventStore = eventStore;
        this.publisher = publisher;
        this.cache = cache; 
        this.aggregateCachingEnabled = Boolean.parseBoolean(cachingEnabled);
    }

    @Bean
    public Repository<Task> createTaskRepository(){
        LOG.info("redis aggregate caching enabled property : " + this.aggregateCachingEnabled + " and is cache auto-wired :" + cache);
        if(this.aggregateCachingEnabled) {
        return new AggregateRepository<Task>(eventStore, publisher, cache) {}; 
        } else {
            return new AggregateRepository<Task>(eventStore,publisher) {};
        }
    }
}
