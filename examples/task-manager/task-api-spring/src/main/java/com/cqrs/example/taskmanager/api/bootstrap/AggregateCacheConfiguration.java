package com.cqrs.example.taskmanager.api.bootstrap;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.cqrs.messaging.AggregateCache;
import com.cqrs.messaging.RedisCache;

@Configuration
@ConditionalOnProperty(
value ="redis.aggregate.caching.enabled",
havingValue = "true",
matchIfMissing = false)
public class AggregateCacheConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(AggregateCacheConfiguration.class);

    private final int ttl;
    private final String redisServerAddress; 
    private final String nodeConfig;
    public AggregateCacheConfiguration(Environment environment) {
       ttl  = environment.getProperty("redis.map.entry.ttl", int.class, -1);
       redisServerAddress = environment.getProperty("redis.server.address", "redis://localhost:6379");
       nodeConfig = environment.getProperty("redis.server.node.config", "SINGLE");
       LOG.info("configured environment variable for redis.map.entry.ttl : " + ttl + " and for redis.server.address :" + redisServerAddress +" and for redis.server.node.config : " +nodeConfig);
    }

    /*
     * Create the aggregate cache client.
     */
    @Bean
    public AggregateCache aggregateCache() {
        if (nodeConfig.equalsIgnoreCase("SINGLE")) {
            return new RedisCache(redisServerAddress, ttl);
        } else {
            String[] nodeAddr = redisServerAddress.split(",");
            if (nodeConfig.equalsIgnoreCase("MASTER_SLAVE")) {
                String master = nodeAddr[0];
                String[] slaves = Arrays.copyOfRange(nodeAddr, 1, nodeAddr.length);
                return new RedisCache(ttl, master, slaves);
            } else {
            // alternative option is cluster node config, we can add support for SENTINEL node config also
                return new RedisCache(ttl, true, nodeAddr);
            }
        }
    }
}