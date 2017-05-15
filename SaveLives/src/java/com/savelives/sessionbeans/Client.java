/*
 * Created by Cheick Berthe on 2017.04.13  * 
 * Copyright © 2017 Cheick Berthe. All rights reserved. * 
 */
package com.savelives.sessionbeans;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;

/**
 *
 * @author berth
 */
class Client {

    private final String DB_PASSWORD = "CSD@s17";
    private final String DB_USER = "CS3984";
    private final static String HOST = "localhost";
    private final static int PORT = 27017;
    public final static String DATABASE = "SaveLives";
    private final String COLLECTION;
    private final MongoClient _client;
    private final MongoCredential credentials;

    /**
     * Create a Client with the given collection name
     *
     * @param collectionName collection name
     */
    public Client(String collectionName) {
        List<ServerAddress> seeds = new ArrayList<>();
        List<MongoCredential> credentialList = new ArrayList<>();

        ServerAddress serverAddress = new ServerAddress(HOST, PORT);
        seeds.add(serverAddress);

        credentials = MongoCredential.createCredential(DB_USER, DATABASE, DB_PASSWORD.toCharArray());
        credentialList.add(credentials);

        MongoClientOptions.Builder optionBuilder = new MongoClientOptions.Builder();
        optionBuilder.connectionsPerHost(100);          // Set the maximum number of connections that can be established with the database
        optionBuilder.connectTimeout(1000 * 60 * 5);    // Set the timeout of connections
        optionBuilder.maxWaitTime(100 * 60 * 5);        // Set the Maximum waiting time for a connection threads 
        optionBuilder.threadsAllowedToBlockForConnectionMultiplier(10);
        optionBuilder.maxConnectionIdleTime(0);
        optionBuilder.maxConnectionLifeTime(0);
        optionBuilder.socketTimeout(0);
        optionBuilder.socketKeepAlive(true);
        MongoClientOptions clientOptions = optionBuilder.build();
        
        COLLECTION = collectionName;
        _client = new MongoClient(seeds, credentialList, clientOptions);
    }

    /**
     * Get the collection
     *
     * @return the
     */
    MongoCollection<Document> getCollection() {
        return _client.getDatabase(DATABASE).getCollection(COLLECTION);
    }
}
