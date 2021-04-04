package com.cqrs.eventstore.local;

import java.io.IOException;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;

import com.cqrs.eventstore.EventStore;
import com.cqrs.eventstore.EventStoreException;
import com.cqrs.messaging.Event;

/**
 * This implementation adds persistance using RecordManager to the InMemoryEventStore implementation by persisting the
 * data on disk at the provided storageLocation
 */
public class RecordManagerEventStore implements EventStore {
    private static final String EVENTSTORE_MAP = "eventstore";

    private final RecordManager recordManager;
    private final InMemoryEventStore inMemoryEventStore;

    public RecordManagerEventStore(String storageLocation) {
        try {
            String recordManagerName = storageLocation + RecordManagerEventStore.class.getSimpleName();
            recordManager = RecordManagerFactory.createRecordManager(recordManagerName);
            inMemoryEventStore = new InMemoryEventStore(recordManager.treeMap(EVENTSTORE_MAP));

            addShutdownHook();
        } catch (IOException e) {
            throw new EventStoreException(e);
        }
    }

    @Override
    public void save(String streamName, String streamId, Iterable<? extends Event> events, int expectedVersion) {
        inMemoryEventStore.save(streamName, streamId, events, expectedVersion);

        try {
            recordManager.commit();
        } catch (IOException e) {
            throw new EventStoreException(e);
        }
    }

    @Override
    public Iterable<? extends Event> getById(String streamId) {
        return inMemoryEventStore.getById(streamId);
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                recordManager.close();
            } catch (IOException ignored) {
                // hide exception when closing database connection
            }
        }));
    }
}
