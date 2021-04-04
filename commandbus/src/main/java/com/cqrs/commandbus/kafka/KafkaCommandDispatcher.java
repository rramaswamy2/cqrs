package com.cqrs.commandbus.kafka;

import static com.cqrs.commandbus.kafka.Constants.COMMAND_TOPIC_PREFIX;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cqrs.commandbus.CommandDispatcher;
import com.cqrs.messaging.Command;
import com.cqrs.messaging.Serializer;

public class KafkaCommandDispatcher implements CommandDispatcher {
	private static final Logger LOG = LoggerFactory.getLogger(KafkaCommandDispatcher.class);
    private final KafkaProducer producer;
   // private final ObjectMapper objectMapper;
    private final Serializer serializer;
    
    public KafkaCommandDispatcher(String zookeeper, Serializer serializer) {
        Properties props = new Properties();
        props.put("bootstrap.servers", zookeeper);
        props.put("key.serializer", StringSerializer.class);
        props.put("value.serializer", StringSerializer.class);

        this.producer = new KafkaProducer<>(props);
        this.serializer = serializer;
       // this.objectMapper = objectMapper;
    }

    @Override
    public <T extends Command> void dispatch(T command) {
        String topic = COMMAND_TOPIC_PREFIX + command.getClass().getSimpleName();
        //String value = serializeCommand(command);
        String value = serializer.serialize(command);
        if (LOG.isDebugEnabled()) {
               
        LOG.debug("kafka pushing " + value +" to topic : " + topic);
        }
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, value);
        producer.send(record);
    }

  /*  private <T extends Command> String serializeCommand(T command) {
        String json;
        try {
            json = objectMapper.writeValueAsString(command);
        } catch (JsonProcessingException e) {
            throw new MessagingException(e);
        }
        return json;
    } */
}
