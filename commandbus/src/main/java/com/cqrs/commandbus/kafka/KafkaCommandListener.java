package com.cqrs.commandbus.kafka;

import static com.cqrs.commandbus.kafka.Constants.COMMAND_TOPIC_PREFIX;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cqrs.commandbus.CommandListener;
import com.cqrs.messaging.Action;
import com.cqrs.messaging.ActionHandler;
import com.cqrs.messaging.ActionHandlerResolver;
import com.cqrs.messaging.Deserializer;

public class KafkaCommandListener implements CommandListener {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaCommandListener.class);
    private static final int CONSUMER_POLL_TIMEOUT = 10000;

    private final KafkaConsumer consumer;
    private final ActionHandlerResolver resolver;
    private final AtomicBoolean closed = new AtomicBoolean(false);
    //private final ObjectMapper objectMapper;
    private final Deserializer deserializer;
    
    public KafkaCommandListener(String zookeeper, String groupId, Deserializer deserializer, ActionHandlerResolver resolver) {
        this.resolver = resolver;

        Properties props = new Properties();
        props.put("bootstrap.servers", zookeeper);
        props.put("group.id", groupId);
        props.put("key.deserializer", StringDeserializer.class);
        props.put("value.deserializer", StringDeserializer.class);
        props.put("enable.auto.commit", "false");
        props.put("auto.offset.reset", "earliest");
        this.deserializer = deserializer;
        this.consumer = new KafkaConsumer(props);
        //this.objectMapper = objectMapper;
    }

    
    public void start() throws Exception {
        new Thread(this::consume).start();
    }

    
    public void stop() throws Exception {
        closed.set(true);
        consumer.wakeup();
    }

    private void consume() {
        try {
            List<String> actionTopics = resolver.getSupportedActions().stream()
                    .map(s -> COMMAND_TOPIC_PREFIX + s)
                    .collect(Collectors.toList());

            consumer.subscribe(actionTopics);
            if (LOG.isDebugEnabled()) {
            	
            LOG.debug("subscribed for topics : " + actionTopics);
            }
            LOG.info("Subscribed for [{}]", actionTopics);

            while (!closed.get()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(CONSUMER_POLL_TIMEOUT));
                for (ConsumerRecord<String, String> record : records) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Received record [{}] from [{}]", record, record.topic());
                    }
                    String action = record.topic().replace(COMMAND_TOPIC_PREFIX, "");
                    tryHandleAction(action, record);
                }
            }
        } catch (WakeupException e) {
            if (!closed.get()) {
                throw e;
            }
        } finally {
            consumer.close();
        }
    }

    private void tryHandleAction(String action, ConsumerRecord<String, String> record) {
        try {
            LOG.debug("Handling action [{}]", action);
            handleAction(action, record.value());

            consumer.commitSync();
        } catch (Exception ex) {
            handleException(action, ex);
        }
    }

    private void handleAction(String action, String value) {
        List<ActionHandler> handlers = resolver.findHandlersFor(action);
        Class<?> clazz = resolver.getHandledActionType(handlers.get(0).getClass());
        Action actionClazz;

      /*  try {
            actionClazz = (Action) objectMapper.readValue(value, clazz);
        } catch (IOException e) {
            throw new MessagingException(e);
        } */
        
        actionClazz = (Action) deserializer.deserialize(value, clazz);
       
        handlers.forEach(handler -> handler.handle(actionClazz));
    }

    private void handleException(String action, Exception ex) {
        //TODO: implement eventPublisher part once eventpublisher is implemented
        LOG.debug("Application error while handling the action: [{}]", action, ex);
    }
}
