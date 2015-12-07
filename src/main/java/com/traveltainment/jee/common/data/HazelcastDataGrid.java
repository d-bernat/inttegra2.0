/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.traveltainment.jee.common.data;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

/**
 *
 * @author bernat
 */
public final class HazelcastDataGrid
{
    private static final HazelcastInstance hazelcastInstance;
    
    static
    {
        final Config cfg = new Config();
        hazelcastInstance = Hazelcast.newHazelcastInstance(cfg);
        
    }
    
    /**
     *
     * @return
     */
    public static HazelcastInstance getHazelcastInstance()
   {
       return hazelcastInstance;
   }
}
