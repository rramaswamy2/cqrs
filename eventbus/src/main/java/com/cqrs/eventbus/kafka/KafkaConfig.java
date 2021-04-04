package com.cqrs.eventbus.kafka;

public class KafkaConfig {

    private final String bootstrapServers;
    private final String groupId;
    private final int concurrency;
    private int numPartitions;

    public KafkaConfig(String bootstrapServers, String groupId, int concurrency) {
        this.bootstrapServers = bootstrapServers;
        this.groupId = groupId;
        this.concurrency = concurrency;
        this.numPartitions = 1;
    }
    
    public KafkaConfig(String bootstrapServers, String groupId, int concurrency, int numPartitions) {
        this.bootstrapServers = bootstrapServers;
        this.groupId = groupId;
        this.concurrency = concurrency;
        this.numPartitions = numPartitions;
        
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public String getGroupId() {
        return groupId;
    }

    public int getConcurrency() {
        return concurrency;
    }

    public int getNumPartitions() {
        return numPartitions;
    }
    
    
}
