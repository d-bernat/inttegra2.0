/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.traveltainment.jee.common.utility;

import com.traveltainment.jee.common.messaging.message.Message;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;


/**
 *
 * @author bernat
 */
public final class LocalResponseQueueMap
{

    private static final Map<String, BlockingQueue<Message>> responseMap = new ConcurrentHashMap<>();
    private static LocalResponseQueueMap instance;
    
    private LocalResponseQueueMap()
    {
        
    }
    
    /**
     *
     * @return
     */
    public static LocalResponseQueueMap getInstance()
    {
        if(instance == null)
            instance = new LocalResponseQueueMap();

        return instance; 
    }

    /**
     *
     * @param key
     */
    public void add(final String key)
    {
        BlockingQueue<Message> queue = new ArrayBlockingQueue<>(0x1);
        responseMap.putIfAbsent(key, queue);
    }

    /**
     *
     * @param key
     * @return
     */
    public BlockingQueue get(final String key)
    {
        return responseMap.get(key);
    }

    /**
     *
     * @param key
     */
    public void remove(final String key)
    {
        responseMap.remove(key);

    }
}
