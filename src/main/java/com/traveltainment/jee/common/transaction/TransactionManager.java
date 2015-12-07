/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.traveltainment.jee.common.transaction;

/**
 *
 * @author bernat
 */
public interface TransactionManager
{

    /**
     *
     * @param timeout
     * @return
     */
    long beginTransaction(int timeout);

    /**
     *
     * @param id
     */
    void commitTransaction(final long id);

    /*public void close()
    {
    client.close();
    }*/
    /**
     *
     * @param id
     * @return
     */
    int getTransactionStatus(final long id);

    /**
     *
     * @param id
     */
    void removeTransaction(final long id);

    /**
     *
     * @param id
     */
    void rollbackTransaction(final long id);
    
}
