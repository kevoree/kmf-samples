package org.bcom;

import cloud.factory.CloudTransaction;
import cloud.factory.CloudTransactionManager;
import org.kevoree.modeling.api.Transaction;
import org.kevoree.modeling.api.persistence.MemoryDataStore;
import powercloud.DistributedCloud;

import java.io.Console;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )

    {



        CloudTransactionManager manager = new CloudTransactionManager(new MemoryDataStore());
        CloudTransaction transaction = manager.createTransaction();
        DistributedCloud model  = transaction.time(0).createDistributedCloud();
        transaction.time(0).root(model);



        System.out.println(model.select("nodes[id=*]"));
        System.out.println(model.select("/nodes[id=*]"));



    }
}
