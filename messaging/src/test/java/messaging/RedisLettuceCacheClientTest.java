package messaging;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.sync.RedisCommands;

class RedisLettuceCacheClientTest {
    private static final Logger LOG = LoggerFactory.getLogger(RedisLettuceCacheClientTest.class);
    private static RedisClient client;
    private static RedisCommands<String,String> syncCommands;
    
    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        
   client = RedisClient.create(RedisURI.create("localhost", 6379));
   syncCommands = client.connect().sync();
        
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
        client.shutdown();
    }

    @Test
    void test() {
        String key1 = "aggregate1";   
        String value1 = "value1";
        String setOK = syncCommands.set(key1, value1);
        LOG.debug("set " + key1 + " with value : " + value1 + " was " + setOK);
       String actualValue = syncCommands.get(key1);
       
       LOG.debug("actual value retrieved for key "+key1 + " is " + actualValue);
        
       Assertions.assertEquals(value1, actualValue, "expected and actual value got for key don't match");
           
        Long keyExistsVal = syncCommands.exists(key1);
        
        LOG.debug("key " + key1 + " exists in cache : " + keyExistsVal);
        
        Assertions.assertEquals(1, keyExistsVal);
          
        String non_existent_key = "non-existent-key";
        Long keyNonExistsVal = syncCommands.exists(non_existent_key);
        
        LOG.debug("key " + non_existent_key + " exists in cache : " + keyNonExistsVal);
        
        Assertions.assertEquals(0, keyNonExistsVal);
        
        String nonExistentKeyVal = syncCommands.get(non_existent_key);
        
        LOG.debug("non existent key : " + non_existent_key + " value in cache : " + nonExistentKeyVal);
        Assertions.assertNull(nonExistentKeyVal);
        
        Long defaultTtl = syncCommands.ttl(key1);
        
        LOG.debug("key "+ key1 + " exists in cache : "+ keyExistsVal + " and default TTL :"+ defaultTtl);
        
        Assertions.assertEquals(-1, defaultTtl);
       
        
       
        // set a 5 second timeout or TTL on the key
        syncCommands.expire(key1, 5);
        
        LOG.debug("getting " + key1 + " before TTL expiry : " + syncCommands.get(key1));
        LOG.debug("current TTL on " + key1 + " is : "+ syncCommands.ttl(key1));
        
        await().atLeast(5, SECONDS).pollDelay(5, SECONDS).until(() -> true);
        
        LOG.debug("getting an expired key : "+ key1 + " after ttl value : "+ syncCommands.get(key1));
        
        Assertions.assertNull(syncCommands.get(key1));
    }

}
