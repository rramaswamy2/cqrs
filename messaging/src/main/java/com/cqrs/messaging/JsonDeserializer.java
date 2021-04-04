package com.cqrs.messaging;

import java.io.IOException;
import java.util.Map;

import com.cqrs.messaging.serializer.IDSerializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

public class JsonDeserializer implements Deserializer {
	private final ObjectMapper objectMapper;

	public JsonDeserializer() {
		SimpleModule module = new SimpleModule();
		module.addSerializer(ID.class, new IDSerializer());

		objectMapper = new ObjectMapper().registerModule(module)
				.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES))
				.registerModule(new JavaTimeModule());

		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> deserializeJsonToMap(String json) {
		try {
			return objectMapper.readValue(json, Map.class);
		} catch (IOException e) {
			throw new SerializationException(e);
		}
	}

	@Override
	public <T> T deserialize(String json, Class<T> clazz) {
		try {
			return objectMapper.readValue(json, clazz);
		} catch (IOException e) {
			throw new SerializationException(e);
		}
	}

}
