package com.kodcu.main;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.script.ScriptService.ScriptType;
import org.elasticsearch.search.SearchHit;


/**
 *
 * @author 
 */

public class JavaAPIMain {
    
    public static void main(String args[]) throws IOException{


    	Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", "elasticsearch").build();
		 Client client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress("192.168.0.9", 9300));
   
        
        client.prepareIndex("ui", "article", "1")
              .setSource(putJsonDocument("ElasticSearch: Java",
                                         "ElasticSeach provides Java API, thus it executes all operations " +
                                          "asynchronously by using client object..",
                                         new Date(),
                                         new String[]{"elasticsearch"},
                                         "Huseyin Akdogan")).execute().actionGet();
        
        client.prepareIndex("ui", "article", "2")
              .setSource(putJsonDocument("Java Web Application and ElasticSearch (Video)",
                                         "Today, here I am for exemplifying the usage of ElasticSearch which is an open source, distributed " +
                                         "and scalable full text search engine and a data analysis tool in a Java web application.",
                                         new Date(),
                                         new String[]{"elasticsearch"},
                                         "Huseyin Akdogan")).execute().actionGet();
        
        getDocument(client, "ui", "article", "1");

        updateDocument(client, "ui", "article", "1", "title", "ElasticSearch: Java API");
        updateDocument(client, "ui", "article", "1", "tags", new String[]{"bigdata"});

        getDocument(client, "ui", "article", "1");

        searchDocument(client, "ui", "article", "title", "ElasticSearch");
        
        deleteDocument(client, "ui", "article", "1");
        
    }
    
    public static Map<String, Object> putJsonDocument(String title, String content, Date postDate, 
                                                      String[] tags, String author){
        
        Map<String, Object> jsonDocument = new HashMap<String, Object>();
        
        jsonDocument.put("title", title);
        jsonDocument.put("content", content);
        jsonDocument.put("postDate", postDate);
        jsonDocument.put("tags", tags);
        jsonDocument.put("author", author);
        
        return jsonDocument;
    }
    
    public static void getDocument(Client client, String index, String type, String id){
        
        GetResponse getResponse = client.prepareGet(index, type, id)
                                        .execute()
                                        .actionGet();
        Map<String, Object> source = getResponse.getSource();
        
        System.out.println("------------------------------");
        System.out.println("Index: " + getResponse.getIndex());
        System.out.println("Type: " + getResponse.getType());
        System.out.println("Id: " + getResponse.getId());
        System.out.println("Version: " + getResponse.getVersion());
        System.out.println(source);
        System.out.println("------------------------------");
        
    }
    
    public static void updateDocument(Client client, String index, String type, 
                                      String id, String field, String newValue){
        
        Map<String, Object> updateObject = new HashMap<String, Object>();
        updateObject.put(field, newValue);
        
        client.prepareUpdate(index, type, id)
              .setScript("ctx._source." + field + "=" + field, ScriptType.INDEXED)
              .setScriptParams(updateObject).execute().actionGet();
    }

    public static void updateDocument(Client client, String index, String type,
                                      String id, String field, String[] newValue){

        String tags = "";
        for(String tag :newValue)
            tags += tag + ", ";

        tags = tags.substring(0, tags.length() - 2);

        Map<String, Object> updateObject = new HashMap<String, Object>();
        updateObject.put(field, tags);

        client.prepareUpdate(index, type, id)
                .setScript("ctx._source." + field + "+=" + field, ScriptType.INDEXED)
                .setScriptParams(updateObject).execute().actionGet();
    }

    public static void searchDocument(Client client, String index, String type,
                                      String field, String value){
    	Map<String, String> fieldquery = new HashMap<String, String>();
    	fieldquery.put(field, value);
    	
        SearchResponse response = client.prepareSearch(index)
                                        .setTypes(type)
                                        .setSearchType(SearchType.QUERY_AND_FETCH)
                                       // .setQuery(QueryBuilders.termQuery(field, value))
                                        .setQuery(fieldquery)
                                        .setFrom(0).setSize(60).setExplain(true)
                                        .execute()
                                        .actionGet();
        
        SearchHit[] results = response.getHits().getHits();
        
        System.out.println("Current results: " + results.length);
        for (SearchHit hit : results) {
            System.out.println("------------------------------");
            Map<String,Object> result = hit.getSource();   
            System.out.println(result);
        }
    }
    
    public static void deleteDocument(Client client, String index, String type, String id){
        
        DeleteResponse response = client.prepareDelete(index, type, id).execute().actionGet();
        System.out.println("Information on the deleted document:");
        System.out.println("Index: " + response.getIndex());
        System.out.println("Type: " + response.getType());
        System.out.println("Id: " + response.getId());
        System.out.println("Version: " + response.getVersion());
    }
}
