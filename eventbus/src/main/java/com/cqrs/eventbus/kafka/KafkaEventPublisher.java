package com.cqrs.eventbus.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cqrs.eventbus.EventPublisher;
import com.cqrs.eventbus.serialize.EventEnvelope;
import com.cqrs.messaging.Deserializer;
import com.cqrs.messaging.Event;
import com.cqrs.messaging.Serializer;

public class KafkaEventPublisher implements EventPublisher {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaEventPublisher.class);
    private final KafkaProducer producer;
    private final Serializer serializer;
    private final Deserializer deserializer;
    private final AdminClient adminClient;

    public KafkaEventPublisher(KafkaConfig config, Serializer serializer, Deserializer deserializer) {
        Properties props = new Properties();
        props.put("bootstrap.servers", config.getBootstrapServers());
        props.put("group.id", config.getGroupId());
        props.put("key.serializer", StringSerializer.class);
        props.put("value.serializer", StringSerializer.class);
        this.producer = new KafkaProducer<>(props);
        this.serializer = serializer;
        this.deserializer = deserializer;
        this.adminClient = AdminClient.create(props);
    }
    
    public void createTopicWithMultiplePartitions(String topicName, int numPartitions, int replicationFactor) {
        NewTopic newTopic = new NewTopic(topicName, numPartitions, (short)replicationFactor);
        List<NewTopic> newTopics = new ArrayList<NewTopic>();
        newTopics.add(newTopic);
        adminClient.createTopics(newTopics);
        
    }
       
    public Set<String> listTopics() throws InterruptedException, ExecutionException {
        Set<String> topicNames = adminClient.listTopics().names().get();
        
        DescribeTopicsResult describeTopics = adminClient.describeTopics(topicNames);
        LOG.info("list of pre-created topics in kafka " + topicNames.toString());
        
        LOG.info("description of the topics : " + describeTopics.all().get().toString());
        return topicNames;
    }

    // more flexible option to specify topic partition number while publishing event, can be used when needed  
    public <T extends Event> void publish(String streamName, int partition, T event) {
        String eventJsonStr = serializeEvent(event);
        LOG.info("publishing event [{} to partition [{}] for topic [{}]", event.getClass().getSimpleName(), partition, streamName);
        try {
            ProducerRecord<String,String> record = new ProducerRecord<>(streamName, partition, event.getEventId().toString(), eventJsonStr);
            producer.send(record);
        } catch(Exception e) {
            LOG.error("failed to publish event : %s", e);
        }
    }

    @Override
    public <T extends Event> void publish(String streamName, T event) {
        String eventType = event.getClass().getSimpleName();
        LOG.debug("Publishing event type [{}] to stream/topic [{}]", eventType, streamName);
        // -- the key of the record is an aggregate Id to ensure the order of the events for the same aggregate
        String eventEnvelopeJson = serializeEvent(event);
        try {
            ProducerRecord<String, String> record = new ProducerRecord<>(streamName, event.getEventId().toString(), eventEnvelopeJson);
            producer.send(record);
     
        } catch (Exception e) {
            LOG.error("Failed to publish event: %s", e);
        }
    }

    private <T extends Event> String serializeEvent(T event) {
        EventEnvelope envelope = EventEnvelope.fromEvent(event, this.serializer, this.deserializer);
        String eventEnvelopeJson = serializer.serialize(envelope);
        return eventEnvelopeJson;
    }

    @Override
    public <T extends Event> void publish(String streamName, Iterable<T> events) {
        events.forEach(event -> publish(streamName, event));
    }

}
