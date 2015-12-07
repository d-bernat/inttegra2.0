/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.traveltainment.jee.common.aspect.router;

import javax.interceptor.InvocationContext;

/**
 *
 * @author bernat
 */
public interface Router
{
    /**
     *
     * @param ctx
     * @return
     * @throws Exception
     */
    Object process(InvocationContext ctx) throws Exception;
    
}
