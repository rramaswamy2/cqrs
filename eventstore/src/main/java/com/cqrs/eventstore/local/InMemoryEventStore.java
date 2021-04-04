package com.cqrs.eventstore.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cqrs.eventstore.ConcurrencyException;
import com.cqrs.eventstore.EventStore;
import com.cqrs.messaging.Event;

/**
 * This eventstore implementation is only ment for unittesting and is probably not usefull for any production use cases
 */
public class InMemoryEventStore implements EventStore {
    private static final Logger LOG = LoggerFactory.getLogger(InMemoryEventStore.class);

    private final Map<String, List<EventDescriptor>> storage;

    public InMemoryEventStore() {
        this(new HashMap<>());
    }

    InMemoryEventStore(Map<String, List<EventDescriptor>> storage) {
        this.storage = storage;
    }

    @Override
    public void save(String streamName, String streamId, Iterable<? extends Event> events, int expectedVersion) {
        LOG.debug("Saving events for [{}] with Id [{}]", streamName, streamId);

        List<EventDescriptor> eventDescriptors;

        if (!storage.containsKey(streamId)) {
            eventDescriptors = new ArrayList<>();
        } else {
            eventDescriptors = storage.get(streamId);

            int actualVersion = eventDescriptors.get(eventDescriptors.size() - 1).version;
            if (actualVersion != expectedVersion && expectedVersion != -1) {
                throw new ConcurrencyException(streamId, actualVersion, expectedVersion);
            }
        }

        int version = expectedVersion;

        for (Event event : events) {
            version++;
            event.setVersion(version);

            eventDescriptors.add(new EventDescriptor(streamId, event, version));
        }

        storage.put(streamId, eventDescriptors);
    }

    @Override
    public Iterable<? extends Event> getById(String streamId) {
        if (storage.containsKey(streamId)) {
            return storage.get(streamId).stream().map(eventDescriptor -> eventDescriptor.event).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
