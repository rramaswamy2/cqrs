package messaging;



import org.redisson.Redisson;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisCacheClientReadWriteLockPutTest {
    private static final Logger LOG = LoggerFactory.getLogger(RedisCacheClientReadWriteLockPutTest.class);
    private Config config;
    
    // sync and async API
    private RedissonClient redisson;
    
    private String[] nodeAddresses; 
    
    public RedisCacheClientReadWriteLockPutTest(String[] nodeAddresses, int ttl) {
        
        config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        
    
        redisson = Redisson.create(config);
        
        RLocalCachedMap<String, Object> map = redisson.getLocalCachedMap("anyMap", LocalCachedMapOptions.defaults());
        
        
        String key1 = "aggregateID1";
        String value1 = "serialized string version of task aggregate updated again using RW lock";
        
       
        RReadWriteLock rwLock = map.getReadWriteLock(key1);
        
        RLock writeLockOnKey = rwLock.writeLock();
        
        
        
        long startTime = System.nanoTime();
        LOG.debug("waiting to get write lock from : " + startTime);
        writeLockOnKey.lock();
        // keyLock.lock(20, TimeUnit.SECONDS);
        long lockAcquiredTime = System.nanoTime();
        LOG.debug("lock acquired : " + writeLockOnKey.isLocked() + " is held by current thread : " + writeLockOnKey.isHeldByCurrentThread() + " and current thread ID : " + Thread.currentThread().getId() +" and name : " +Thread.currentThread().getName());
        
        long elapsedTime = lockAcquiredTime - startTime;
        
        double elapsedTimeInSecs = (double) elapsedTime / 1000000000.0;
        LOG.debug("write lock acquired at : " + lockAcquiredTime);
        LOG.debug("elapsed time to acquire write lock in nanosecs : " + elapsedTime);
        LOG.debug("time to acquire write lock in seconds : " + elapsedTimeInSecs);
        
        try {
        
       Object prevValue = map.put(key1, value1);
            
       LOG.debug("set key : " + key1 + " with value : " + value1 + " pair to the map cache , previous value was : " + prevValue);
       LOG.debug("does map cache contain key " + key1 +" : "  +map.containsKey(key1) + " and value :" + map.get(key1) + " (before TTL expiry on key)");
         } finally {
            
            LOG.debug("remaining lock ttl or lease time : " + writeLockOnKey.remainTimeToLive());
            writeLockOnKey.unlock(); 
        }
        
        
        
        redisson.shutdown();
        
    }
    
    public static void main(String[] args) {
        
        new RedisCacheClientReadWriteLockPutTest(null, -1);
    }
    
    
}

