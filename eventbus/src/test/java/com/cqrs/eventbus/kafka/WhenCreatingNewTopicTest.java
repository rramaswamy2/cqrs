package com.cqrs.eventbus.kafka;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.cqrs.eventbus.kafka.KafkaConfig;
import com.cqrs.eventbus.kafka.KafkaEventPublisher;
import com.cqrs.messaging.Deserializer;
import com.cqrs.messaging.JsonDeserializer;
import com.cqrs.messaging.JsonSerializer;
import com.cqrs.messaging.Serializer;


@TestInstance(Lifecycle.PER_CLASS)
public class WhenCreatingNewTopicTest {
   
    KafkaEventPublisher publisher;
    public WhenCreatingNewTopicTest() {
        KafkaConfig config = new KafkaConfig("localhost:9092", "cqrs-kafka-eventbus-test", 1);
        Deserializer deserializer = new JsonDeserializer();
        Serializer serializer = new JsonSerializer();
        publisher = new KafkaEventPublisher(config, serializer, deserializer);
        publisher.createTopicWithMultiplePartitions("kafkaTopic2", 3, 1);
        await().atLeast(4, SECONDS).pollDelay(4, SECONDS).until(() -> true);
      
    }

    @Test
    public void testItShouldCreateTopic() throws InterruptedException, ExecutionException {
        
         Set<String> topicNames = publisher.listTopics();
         
         System.out.println("newly created kafka topics : " + topicNames.toString());
        
    }

  
}

