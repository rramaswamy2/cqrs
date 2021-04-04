package com.cqrs.messaging;

import java.io.Serializable;
import java.util.UUID;

public final class ID implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String value;

    private ID(String value) {
        this.value = value;
    }

    public static ID fromObject(Object value) {
        ID identifier;
        if (value instanceof String) {
            identifier = new ID((String) value);
        } else if (value instanceof UUID) {
            identifier = new ID(value.toString());
        } else {
            throw new IllegalArgumentException("The id should be of either String or UUID type");
        }

        return identifier;
    }

    public boolean equals(Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        return this.value.equals(((ID)obj).value);
    }

    public int hashCode() {
        return this.value.hashCode();
    }

    public String toString() {
        return value;
    }
}
