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

public class RedisCacheClientReadWriteLockGetTest {
    
    private static final Logger LOG = LoggerFactory.getLogger(RedisCacheClientReadWriteLockGetTest.class);
    private Config config;
    
    
    // sync and async API
    private RedissonClient redisson;
    
    private String[] nodeAddresses; 
    
    public RedisCacheClientReadWriteLockGetTest(String[] nodeAddresses, int ttl) {
        
        config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
   
        redisson = Redisson.create(config);
        
        RLocalCachedMap<String, Object> map = redisson.getLocalCachedMap("anyMap", LocalCachedMapOptions.defaults());
        
        String key1 = "aggregateID1";
        
        RReadWriteLock rwLock = map.getReadWriteLock(key1);
        RLock readLockOnKey = rwLock.readLock();   
        
        long startTime = System.nanoTime();
        LOG.debug("waiting to get read lock from : " + startTime);
        readLockOnKey.lock();
       
        long lockAcquiredTime = System.nanoTime();
        LOG.debug("lock acquired : " + readLockOnKey.isLocked() + " is held by current thread : " + readLockOnKey.isHeldByCurrentThread() + " and current thread ID : " + Thread.currentThread().getId() +" and name : " +Thread.currentThread().getName());
        
        long elapsedTime = lockAcquiredTime - startTime;
        
        double elapsedTimeInSecs = (double) elapsedTime / 1000000000.0;
        LOG.debug("read lock acquired at : " + lockAcquiredTime);
        LOG.debug("elapsed time to acquire read lock in nanosecs : " + elapsedTime);
        LOG.debug("time to acquire lock in read lock in seconds : " + elapsedTimeInSecs);
        
        try {
        
         LOG.debug("does map cache contain key " + key1 +" : "  +map.containsKey(key1) + " and value :" + map.get(key1) + " (before TTL expiry on key)");
         } finally {
            
            LOG.debug("remaining lock ttl or lease time : " + readLockOnKey.remainTimeToLive());
            readLockOnKey.unlock(); 
        }
       
        redisson.shutdown();
        
    }
    
    public static void main(String[] args) {
        
        new RedisCacheClientReadWriteLockGetTest(null, -1);
    }
    
    
}
