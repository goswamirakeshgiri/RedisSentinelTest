package com.jedistest;

import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.exceptions.JedisException;

public class JedisTestSentinelEndpoint {
    private static final String MASTER_NAME = "redis-cluster";
    public static final String PASSWORD = null;
    private static final Set sentinels;
    static {
        sentinels = new HashSet();
        sentinels.add("35.184.130.95:16379");
        sentinels.add("35.184.130.95:16380");
        sentinels.add("35.184.130.95:16381");
    }
    
    public static void main(String args[])
    {
    	 try {
			runTest();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public JedisTestSentinelEndpoint() {
    }

    private static void runTest() throws InterruptedException {
        boolean writeNext = true;
        JedisSentinelPool pool = new JedisSentinelPool(MASTER_NAME, sentinels);
        System.out.println(pool.getCurrentHostMaster());
        Jedis jedis = null;
        while (true) {
            try {
                System.out.println("Fetching connection from pool");
                jedis = pool.getResource();
            //    System.out.println("Authenticating...");
            //    jedis.auth(PASSWORD);
                System.out.println("auth complete...");
                Socket socket = jedis.getClient().getSocket();
                System.out.println("Connected to " + socket.getRemoteSocketAddress());
                while (true) {
                    if (writeNext) {
                        System.out.println("Writing...");
                        jedis.set("java-key-999", "java-value-999");
                        writeNext = false;
                    } else {
                        System.out.println("Reading...");
                        jedis.get("java-key-999");
                        writeNext = true;
                    }
                    Thread.sleep(2 * 1000);
                }
            } catch (JedisException e) {
                System.out.println("Connection error of some sort!");
                e.printStackTrace();
              //  System.out.println(e.getStackTrace());
                Thread.sleep(2 * 1000);
                break;
                
            } finally {
                if (jedis != null) {
                    jedis.close();
                    
                }
            }
        }
    }
}
