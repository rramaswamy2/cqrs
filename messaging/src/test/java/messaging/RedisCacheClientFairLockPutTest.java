package messaging;



import org.redisson.Redisson;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisCacheClientFairLockPutTest {
    private static final Logger LOG = LoggerFactory.getLogger(RedisCacheClientFairLockPutTest.class);
    private Config config;
    
    // sync and async API
    private RedissonClient redisson;
    
    private String[] nodeAddresses; 
    
    public RedisCacheClientFairLockPutTest(String[] nodeAddresses, int ttl) {
        
        config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
   
        redisson = Redisson.create(config);
      
        RLocalCachedMap<String, Object> map = redisson.getLocalCachedMap("anyMap", LocalCachedMapOptions.defaults());
        
        String key1 = "aggregateID1";
        String value1 = "serialized string version of task aggregate updated using fair lock again";
       
        RLock keyLock = map.getFairLock(key1);
     
        long startTime = System.nanoTime();
        LOG.debug("waiting to get lock from : " + startTime);
        keyLock.lock();
        
        long lockAcquiredTime = System.nanoTime();
        LOG.debug("lock acquired : " + keyLock.isLocked() + " is held by current thread : " + keyLock.isHeldByCurrentThread() + " and current thread ID : " + Thread.currentThread().getId() +" and name : " +Thread.currentThread().getName());
        
        long elapsedTime = lockAcquiredTime - startTime;
        
        double elapsedTimeInSecs = (double) elapsedTime / 1000000000.0;
        LOG.debug("lock acquired at : " + lockAcquiredTime);
        LOG.debug("elapsed time to acquire lock in nanosecs : " + elapsedTime);
        LOG.debug("time to acquire lock in seconds : " + elapsedTimeInSecs);
        
        try {
        
       Object prevValue = map.put(key1, value1);
      
       LOG.debug("set key : " + key1 + " with value : " + value1 + " pair to the map cache , previous value was : " + prevValue);
       LOG.debug("does map cache contain key " + key1 +" : "  +map.containsKey(key1) + " and value :" + map.get(key1) + " (before TTL expiry on key)");
               
       LOG.debug("get on non-existent key : " + map.get("non-existent-key"));
        } finally {
            
            LOG.debug("remaining lock ttl or lease time : " + keyLock.remainTimeToLive());
            keyLock.unlock(); 
        }
        
        
        
        redisson.shutdown();
        
    }
    
    public static void main(String[] args) {
        
        new RedisCacheClientFairLockPutTest(null, -1);
    }

}
