package com.cqrs.messaging.serializer;

import java.io.IOException;

import com.cqrs.messaging.ID;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class IDSerializer extends StdSerializer<ID> {

    private static final long serialVersionUID = 1L;

    public IDSerializer() {
        this(null);
    }

    public IDSerializer(Class<ID> t) {
        super(t);
    }

    @Override
    public void serialize(ID value, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
        jgen.writeString(value.toString());
    }
}
