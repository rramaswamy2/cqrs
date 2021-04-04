package com.cqrs.messaging;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public class RedisLettuceCacheClient implements AggregateLettuceCache {

    private String redisServer;
    private int redisPort;
    private long ttl;
    
    private RedisClient client;
    private StatefulRedisConnection<String,String> connection;
    private RedisCommands<String,String> syncCommands;
    
    public RedisLettuceCacheClient(String server, int port, long ttl) {
        this.redisServer = server;
        this.redisPort = port; //default 6379
        this.ttl = ttl;
        
        client = RedisClient.create(RedisURI.create(this.redisServer, this.redisPort));
        
    }
    
    public void connect() {
      connection = client.connect();
      syncCommands = connection.sync();
          
    }
    
    
    @Override
    public String set(String key, String value) {
              
        return syncCommands.set(key, value);
              
    }

    @Override
    public String get(String key) {
       
        return syncCommands.get(key);
    }

    @Override
    public Long ttl(String key) {
        
        return syncCommands.ttl(key);
    }

    @Override
    public Long exists(String key) {
        
        return syncCommands.exists(key);
    }

    @Override
    public Boolean expire(String key, long timeout) {
        
        return syncCommands.expire(key, timeout); 
    }

}
