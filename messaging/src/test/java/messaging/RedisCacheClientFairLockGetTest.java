package messaging;



import org.redisson.Redisson;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisCacheClientFairLockGetTest {
    
    private static final Logger LOG = LoggerFactory.getLogger(RedisCacheClientFairLockGetTest.class);
    private Config config;
    
    // sync and async API
    private RedissonClient redisson;
    
    private String[] nodeAddresses; 
    
    public RedisCacheClientFairLockGetTest(String[] nodeAddresses, int ttl) {
        
        config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        
     
        redisson = Redisson.create(config);
        
        RLocalCachedMap<String, Object> map = redisson.getLocalCachedMap("anyMap", LocalCachedMapOptions.defaults());
        
        String key1 = "aggregateID1";
       
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
        
     
        LOG.debug("does map cache contain key " + key1 +" : "  +map.containsKey(key1) + " and value :" + map.get(key1) + " (before TTL expiry on key)");
      
        } finally {
            
            LOG.debug("remaining lock ttl or lease time : " + keyLock.remainTimeToLive());
            keyLock.unlock(); 
        }
        
        
        
        redisson.shutdown();
        
    }
    
    public static void main(String[] args) {
        
        new RedisCacheClientFairLockGetTest(null, -1);
    }
    
    
    
    

}
