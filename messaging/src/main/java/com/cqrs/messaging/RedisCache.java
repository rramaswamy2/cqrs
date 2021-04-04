package com.cqrs.messaging;

import java.util.concurrent.TimeUnit;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RMapCache;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RedisCache implements AggregateCache {

    private Config config;
    
    // sync and async API
    private RedissonClient redisson;
    
    private int ttl; // in seconds for example 3600 seconds
       
    RMapCache<String,Object> mapCache;
   
    public RedisCache(String serverAddress, int ttl) {
        config = new Config();
        config.useSingleServer().setAddress(serverAddress);
        initMapCache(ttl);
    }

    private void initMapCache(int ttl) {
        redisson = Redisson.create(config);
        this.ttl = ttl;
        mapCache = redisson.getMapCache("anyMapCache");
    }
    
    public RedisCache(int ttl, boolean isCluster, String... serverAddresses) {
        config = new Config();
        
        if(isCluster) 
        config.useClusterServers().addNodeAddress(serverAddresses);
        else
        config.useSentinelServers().addSentinelAddress(serverAddresses);
        initMapCache(ttl);
            
    }
    
    // example master node on 127.0.0.1:6379 and slave node on 127.0.0.1:6389
    public RedisCache(int ttl, String master, String... slaves) {
        config = new Config();
        config.useMasterSlaveServers().setMasterAddress(master).addSlaveAddress(slaves);
        initMapCache(ttl);
    }
    
    @Override
    public Object put(String key, Object value) {
        RReadWriteLock rwLock = mapCache.getReadWriteLock(key);
        RLock wLock = rwLock.writeLock();
        wLock.lock();
        Object prevValue = null;
        try {
            prevValue = mapCache.put(key, value, ttl, TimeUnit.SECONDS);
            
        } finally {
            wLock.unlock();
        }
       return prevValue;
    }

    @Override
    public Object get(String key) {
        RReadWriteLock rwLock = mapCache.getReadWriteLock(key);
        RLock rLock = rwLock.readLock();
        rLock.lock(); 
        Object currValue = null;
        try {
            currValue = mapCache.get(key);
        } finally {
            rLock.unlock();
        }
        return currValue;
    }

    @Override
    public boolean containsKey(String key) {
        RLock rLock = mapCache.getReadWriteLock(key).readLock();
        rLock.lock();
        boolean exists = false;
        try { exists = mapCache.containsKey(key); 
        } finally {
        rLock.unlock();
        }
        return exists;
    }

    @Override
    public boolean expire(long expiry, TimeUnit unit) {
        return mapCache.expire(expiry, unit);
    }


    @Override
    public long remainingTTLForMapEntries() {
        return mapCache.remainTimeToLive(); // default ttl -1 for map entries 
    }
    
    @Override
    public void shutdown() {
        redisson.shutdown();
    }

}
