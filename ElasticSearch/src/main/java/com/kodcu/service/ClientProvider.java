/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kodcu.service;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;

import com.kodcu.util.Constants;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

/**
 *
 * @author 
 */
public class ClientProvider {
    
    private static ClientProvider instance = null;
    private static Object lock      = new Object();
    
    private Client client;
    private Node node;

    public static ClientProvider instance(){
        
        if(instance == null) { 
            synchronized (lock) {
                if(null == instance){
                    instance = new ClientProvider();
                    instance.prepareClient();
                }
            }
        }
        return instance;
    }

    public void prepareClient(){
    	Settings settings = ImmutableSettings.settingsBuilder().put(Constants.key_cluster, "elasticsearch").build();
		 client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress("192.168.0.9", 9300));
    }

    public void closeClient(){
        
        	client.close();

    }
    
    public Client getClient(){
        return client;
    }
    
    
    public void printThis() {
        System.out.println(this);
    }
    
}
