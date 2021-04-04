package com.cqrs.eventstore.pulsar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Reader;
import org.apache.pulsar.client.api.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cqrs.eventstore.EventStore;
import com.cqrs.messaging.Event;
import com.cqrs.messaging.JsonDeserializer;

public class PulsarEventStore implements EventStore {

	private PulsarClient client;
	
	//private String streamId;
	
	private String topic;
	
	private static final JsonDeserializer deser = new JsonDeserializer();
	
	public PulsarEventStore(String serviceUrl, String topic) throws PulsarClientException {
		
		client = PulsarClient.builder().serviceUrl(serviceUrl).build();
		//this.streamId = streamId;
		this.topic = topic; // this has to be a topic per aggregate instance identified by event streamID
		
	}
	private static final Logger LOG = LoggerFactory.getLogger(PulsarEventStore.class);
	
	@Override
	public void save(String streamName, String streamId, Iterable<? extends Event> events, int expectedVersion) {
		LOG.info("messages are saved and retained in pulsar event bus or message broker.. check the retention policy for unlimited time and size retention..");
		
	}

	@Override
	public Iterable<? extends Event> getById(String streamId) {
		try {
			System.out.println("dynamic topic contains streamId : " + topic.contains(streamId));
			
			Reader<String> reader = client.newReader(Schema.STRING).topic(topic).startMessageId(MessageId.earliest).create();
			List eventList = new ArrayList();
		    while(reader.hasMessageAvailable()) {
		    	
		    	Message<String> msg = reader.readNext();
		    	
		    	String msgValue = msg.getValue();
		    	System.out.println("JSON event string : " + msgValue);
		    	Map<String, String> eventMap = deser.deserializeJsonToMap(msgValue);
		    	String eventClassName = eventMap.get("eventClassName");
		    	Class clazz = Class.forName(eventClassName);
		    	 
                Event event = (Event) deser.deserialize(msgValue, clazz);
                eventList.add(event);
		    	
		    }
		return eventList;
		
		
		} catch (PulsarClientException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	
	}
	
	public void close() {
		
		try {
			client.close();
		} catch (PulsarClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
