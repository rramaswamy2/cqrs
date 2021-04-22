package com.cqrs.domain;

import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cqrs.eventbus.EventPublisher;
import com.cqrs.eventstore.EventStore;
import com.cqrs.messaging.AggregateCache;
import com.cqrs.messaging.Event;
import com.cqrs.messaging.ID;
import com.google.common.collect.Iterables;
import com.google.common.reflect.TypeToken;

public abstract class AggregateRepository<T extends AggregateRoot> implements Repository<T> {
    private static final Logger LOG = LoggerFactory.getLogger(AggregateRepository.class);
    private EventStore store;
    private AggregateCache cache;
    private EventPublisher publisher;
    
    protected AggregateRepository(EventStore store, EventPublisher publisher) {
        this.store = store;
        this.publisher = publisher;
        
    }

    protected AggregateRepository(EventStore store, EventPublisher publisher, AggregateCache cache) {
        this.store = store;
        this.cache = cache;
        this.publisher = publisher;
    }

    @Override
    public void save(T aggregate, int version) {
        String streamName = aggregate.getClass().getSimpleName();
        Iterable<? extends Event> uncommittedChanges = aggregate.getUncommittedChanges();
        int newEventCount = Iterables.size(uncommittedChanges);
        LOG.info("new aggregate state change event count : " + newEventCount);
        store.save(streamName, aggregate.getId().toString(), uncommittedChanges, version);
        aggregate.markChangesAsCommitted();
        if(cache!= null) { 
        // cache reference not null indicates caching is enabled and a cache client bean is autowired or injected into the CQRS service    
        LOG.info("preparing to cache aggregate with ID : "+ aggregate.getId().toString()+" and current version " + aggregate.getVersion() +" to redis cache");
        int aggregateNewVersion =  aggregate.getVersion() + newEventCount;
        aggregate.setVersion(aggregateNewVersion);
        LOG.info("new aggregate version to be cached : "+ aggregate.getVersion());
        cache.put(aggregate.getId().toString(), aggregate);
        LOG.info("current aggregate state cached after ES save/commit : " + aggregate.toString());
        }
        publisher.publish(streamName, uncommittedChanges);
    }
    
    @Override
    public void replay(ID id) {
    	String aggregateId = id.toString();
    	Iterable<? extends Event> events = store.getById(aggregateId);
    	
    	int eventSize = Iterables.size(events);
    	LOG.info("number of events to replay : " + eventSize);
    	if(events == null || Iterables.isEmpty(events)) {
    		LOG.info("no events found for specific aggregate instance ");
  		
    	} else {
    		  Class<? extends AggregateRoot> parameterizedClass = getParameterizedClass(getClass());
    	      T aggregate = (T) instantiate(parameterizedClass);   //create empty aggregate object 
    	      String topicName = aggregate.getClass().getSimpleName();
    		  publisher.publish(topicName, events);
    	}
    	
    }
    
    @Override
    public List<String> replayAll() {
    	List<String> aggrIds = store.getAllAggregateIds();
    	for(String aggrId : aggrIds) {
    		LOG.info("attempting replay on aggregate : " + aggrId);
    		ID aggregateId = ID.fromObject(aggrId);
    		replay(aggregateId);
    	}
    	return aggrIds;
    	
    }

    @Override
    public T getById(ID id) {
        String aggregateIdKey = id.toString();
        if(cache != null && cache.containsKey(aggregateIdKey)) {
          T aggregate  = (T) cache.get(aggregateIdKey);
          LOG.info("directly fetching aggregate with key : " + aggregateIdKey + " from cache.");
          LOG.info("aggregate state fetched from cache : " + aggregate.toString());
          return aggregate;
        } 
        else {
        LOG.info("fallback to event dehydration method as aggregate not found in cache or aggregate caching is not enabled");
        Class<? extends AggregateRoot> parameterizedClass = getParameterizedClass(getClass());
        T aggregate = (T) instantiate(parameterizedClass);
        Iterable<? extends Event> events = store.getById(id.toString());
        if (events == null || Iterables.isEmpty(events)) {
            throw new AggregateNotFoundException(parameterizedClass.getName(), id);
        } else {
            aggregate.loadFromHistory(events);
            return aggregate;
        }
        } 
    }

    private Class<? extends AggregateRoot> getParameterizedClass(Class clazz) {
        TypeToken<T> type = new TypeToken<T>(clazz) {};
        return (Class<? extends AggregateRoot>)type.getRawType();
    }

    private static <T> T instantiate(final Class<T> clazz) {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<T>) () -> {
                Constructor<T> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                return constructor.newInstance();
            });
        } catch (PrivilegedActionException e) {
            if (e.getCause() instanceof NoSuchMethodException) {
                throw new DomainException(clazz + " must have a default constructor");
            }
            throw new DomainException(e);
        }
    }
}
