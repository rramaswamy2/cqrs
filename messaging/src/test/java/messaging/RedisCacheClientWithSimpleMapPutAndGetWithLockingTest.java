package messaging;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

import java.util.concurrent.TimeUnit;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisCacheClientWithSimpleMapPutAndGetWithLockingTest {
    private static final Logger LOG = LoggerFactory.getLogger(RedisCacheClientWithSimpleMapPutAndGetWithLockingTest.class);
    private Config config;
    
    // sync and async API
    private RedissonClient redisson;
    
    private String[] nodeAddresses; 
    
    public RedisCacheClientWithSimpleMapPutAndGetWithLockingTest(String[] nodeAddresses, int ttl) {
        
        config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        redisson = Redisson.create(config);
     
        RMap<Object, Object> map = redisson.getMap("anyMap");
        
        RMapCache<Object, Object> mapCache = redisson.getMapCache("anyMapCache");
        
        
        String key1 = "aggregateID1";
        String value1 = "value1";
        
        String key2 = "aggregateID2";
        String value2 = "value2";
        
        String key3 = "aggregateID3";
        String value3 = "value3";
        
        RLock keyLock = map.getLock(key1);
     
        long startTime = System.nanoTime();
        LOG.debug("waiting to get lock from : " + startTime);
       keyLock.lock();
       
       
        LOG.debug("default Rlock TTL : " + keyLock.remainTimeToLive() + " and default TTL for map entries : " + map.remainTimeToLive());
     
        long lockAcquiredTime = System.nanoTime();
         LOG.debug("lock acquired : " + keyLock.isLocked() + " is held by current thread : " + keyLock.isHeldByCurrentThread() + " and current thread ID : " + Thread.currentThread().getId() +" and name : " +Thread.currentThread().getName());
        
        long elapsedTime = lockAcquiredTime - startTime;
        
        double elapsedTimeInSecs = (double) elapsedTime / 1000000000.0;
        LOG.debug("lock acquired at : " + lockAcquiredTime);
        LOG.debug("elapsed time to acquire lock in nanosecs : " + elapsedTime);
        LOG.debug("time to acquire lock in seconds for a simple map entry : " + elapsedTimeInSecs);
        
        try {
        
       Object prevValue = map.put(key1, value1);
       LOG.debug("default TTL for lock  : " + keyLock.remainTimeToLive() + " default TTL for map entries : "+ map.remainTimeToLive());
       map.expire(5, TimeUnit.SECONDS);
       await().atLeast(3, SECONDS).pollDelay(3, SECONDS).until(() -> true);
       LOG.debug("map contents : " + map.toString());
       map.put(key3, value3);
        
       LOG.debug("map contents : " + map.toString());
       LOG.debug("added key : " + key1 + " with value : " + value1 + "  previous value was : " + prevValue);
       LOG.debug("does map cache contain key " + key1 +" : "  +map.containsKey(key1) + " and value :" + map.get(key1) + " (before TTL expiry on key)");
       LOG.debug("does map cache contain key " + key3 +" : "  +map.containsKey(key3) + " and value :" + map.get(key3) + " (before TTL expiry on key)");
       
        
       LOG.debug("new TTL for map entries : " + map.remainTimeToLive());
        
       await().atLeast(3, SECONDS).pollDelay(3, SECONDS).until(() -> true);
        
        LOG.debug("does map cache contain key " + key1 +" : "  +map.containsKey(key1) + " and value :" + map.get(key1) + " (after TTL expiry of 5 secs)");
        LOG.debug("does map cache contain key " + key3 +" : "  +map.containsKey(key3) + " and value :" + map.get(key3) + " (after TTL expiry of 5 secs) ");
        LOG.debug("get on non-existent key : " + map.get("non-existent-key"));
        } finally {
            
            LOG.debug("remaining lock ttl or lease time : " + keyLock.remainTimeToLive());
            LOG.debug("new TTL for map entries : " + map.remainTimeToLive());
            keyLock.unlock(); 
        }
        
        
        // try same operations with map cache 
        LOG.debug("trying cache operations with RMapCache API..");
        RLock lock1 = mapCache.getLock(key1);
        long startTime1 = System.nanoTime();
        lock1.lock();
        long endTime1 = System.nanoTime();
        
       long elapsedTime1 = endTime1 - startTime1;
        
        double elapsedTimeInSecs1 = (double) elapsedTime1 / 1000000000.0;
        
        LOG.debug("elapsed time to acquire lock in nanosecs : " + elapsedTime1);
        LOG.debug("time to acquire lock in seconds for a simple map cache entry : " + elapsedTimeInSecs1);
        
        
        Object prevValue = mapCache.put(key1, value1, 5, TimeUnit.SECONDS);
        LOG.debug("added: " + key1 + " value : " + value1 + " to map cache");
        LOG.debug("does map contain key " + key1 +" :" + mapCache.containsKey(key1) +"  value for key : " + key1 + " from map cache " + mapCache.get(key1) + " before TTL expiry" );
        RLock lock2 = mapCache.getLock(key2);
        
        lock2.lock();
        mapCache.put(key2, value2); // default TTL , no expiry
        LOG.debug("added " + key2 + " with " + value2 + " to map cache with default TTL");
        await().atLeast(5, SECONDS).pollDelay(5, SECONDS).until(() -> true);
        LOG.debug("does map contain key " + key1 +" :" + mapCache.containsKey(key1) +"  value for key : " + key1 + " from map cache " + mapCache.get(key1) + " after TTL expiry" );
        
        lock1.unlock();
        
        LOG.debug(" does map contain key " + key2 + " : "+ mapCache.containsKey(key2) +" value for key : " + key2 + " from map cache " + mapCache.get(key2) + " after TTL expiry for key " + key1);
        
        lock2.unlock();
        
        redisson.shutdown();
        
    }
    
    public static void main(String[] args) {
        
        new RedisCacheClientWithSimpleMapPutAndGetWithLockingTest(null, -1);
    }
    
    
    
    

}
