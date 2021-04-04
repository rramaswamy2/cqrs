package messaging;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cqrs.messaging.RedisCache;

public class RedisCacheTest {

    private static final Logger LOG = LoggerFactory.getLogger(RedisCacheClientWithSimpleMapPutAndGetWithLockingTest.class);
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        
        int ttl = 5; //5 seconds ttl
        RedisCache cache = new RedisCache("localhost:6379", ttl);
        
        String key1 = "Mobile";
        String value1 = "Samsung";
        String key2 = "Electronics";
        String value2 = "HP Computer";
        cache.put(key1, value1);
        
        LOG.debug("added " +key1 + " with value : " + value1 + " to cache");
        
        LOG.debug("default TTL for map entries : " + cache.remainingTTLForMapEntries());
        LOG.debug("configured TTL for map entries : " + ttl);
        await().atLeast(3, SECONDS).pollDelay(3, SECONDS).until(() -> true);
        
        cache.put(key2, value2);
        
        LOG.debug("added " +key1 + " with value : " + value1 + " to cache");
        
        LOG.debug("does key : "+ key1 + " exists in cache : " + cache.containsKey(key1) + " and value : " + cache.get(key1) + " before TTL expiry on key :" + key1);
        
        await().atLeast(3, SECONDS).pollDelay(3, SECONDS).until(() -> true);
        
        LOG.debug("does key : "+ key1 + " exists in cache : " + cache.containsKey(key1) + " and value : " + cache.get(key1) + " after TTL expiry on key :"+key1);
        
        
        LOG.debug("does key : "+ key2 + " exists in cache : " + cache.containsKey(key2) + " and value : " + cache.get(key2) + " after TTL expiry on key "+ key1);
        
        await().atLeast(2, SECONDS).pollDelay(2, SECONDS).until(() -> true);
        
        
        LOG.debug("does key : "+ key2 + " exists in cache : " + cache.containsKey(key2) + " and value : " + cache.get(key2) + " after TTL expiry on key "+ key2);
        
        cache.shutdown();
        

        
    }

}
