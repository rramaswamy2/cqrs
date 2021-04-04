package com.cqrs.domain;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cqrs.messaging.Event;
import com.cqrs.messaging.ID;

public abstract class AggregateRoot implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AggregateRoot.class);

    private static final String APPLY_METHOD_NAME = "handle";
    private final List<Event> changes = new ArrayList<>();

    private ID id;
    private int version;

    protected AggregateRoot() {
        this(null);
    }

    protected AggregateRoot(ID id) {
        this.id = id;
    }

    public ID getId() {
        return id;
    }

    protected void setId(ID id) {
        this.id = id;
    }
   
    public int getVersion() {
        return version;
    }
    
    

    public void setVersion(int version) {
        this.version = version;
    }

    public void markChangesAsCommitted() {
        changes.clear();
    }

    public final Iterable<? extends Event> getUncommittedChanges() {
        return new ArrayList<>(changes);
    }

    public final void loadFromHistory(Iterable<? extends Event> history) {
        for (Event e : history) {
            if (version < e.getVersion()) {
                version = e.getVersion();
            }

            applyChange(e, false);
        }
    }

    protected void applyChange(Event event) {
        applyChange(event, true);
    }

    private void applyChange(Event event, boolean isNew) {
        invokeApplyIfEntitySupports(event);
        if (isNew) {
            changes.add(event);
        }
    }

    private void invokeApplyIfEntitySupports(Event event) {
        Class<?> eventType = nonAnonymous(event.getClass());
        try {
            Method method = this.getClass().getDeclaredMethod(APPLY_METHOD_NAME, eventType);
            method.setAccessible(true);
            method.invoke(this, event);
        } catch (SecurityException | IllegalAccessException | InvocationTargetException e) {
            throw new DomainException(e);
        } catch (NoSuchMethodException ex) {
            LOGGER.warn("Event {} not applicable to {}!", event, this);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> nonAnonymous(Class<T> clazz) {
        return clazz.isAnonymousClass() ? (Class<T>) clazz.getSuperclass() : clazz;
    }
}
