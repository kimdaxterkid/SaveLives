/*
 * Created by Pingxin Shang on 2017.03.13  * 
 * Copyright © 2017 Pingxin Shang. All rights reserved. * 
 */
package com.savelives.sessionbeans;

import com.savelives.entityclasses.User;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import org.bson.BsonDocument;
import org.bson.BsonRegularExpression;
import org.bson.Document;
import org.bson.types.ObjectId;

@SessionScoped
/*
 * @author ping
 */
public class UserFacade implements Serializable{

    private static final Logger LOGGER = Logger.getLogger(UserFacade.class.getName());
    private final Client userClient;
    private final String COLLECTION_NAME = "Users";

    /**
     * Default Constructor
     */
    public UserFacade() {
        userClient = new Client(COLLECTION_NAME);
    }

    /**
     * Create a new user and add it to the collection
     *
     * @param u
     */
    public void create(User u) {
        //Get the user collection from the client
        MongoCollection<Document> collection = userClient.getCollection();
        //check that user object exists then insert it in the collection
        if (u != null) {
            collection.insertOne(u.toDocument());
        }
    }

    public void edit(User u) {

        MongoCollection<Document> collection = userClient.getCollection();

        if (u != null) {
            collection.updateOne(eq("_id", new ObjectId(u.getId())), new Document("$set", u.toDocument()));

        }
    }

    public void delete(User u) {

        MongoCollection<Document> collection = userClient.getCollection();
        collection.deleteOne(new Document("_id", u.getId()));
    }

    public List<User> find(String filter) {
        List<User> list = new ArrayList<>();
        MongoCollection<Document> collection = userClient.getCollection();
        FindIterable<Document> iter;
        if (filter == null || filter.trim().length() == 0) {
            iter = collection.find();
        } else {
            BsonRegularExpression bsonRegex = new BsonRegularExpression(filter);
            BsonDocument bsonDoc = new BsonDocument();
            bsonDoc.put("username", bsonRegex);
            iter = collection.find(bsonDoc);
        }
        iter.forEach(new Block<Document>() {
            @Override
            public void apply(Document doc) {
                list.add(new Gson().fromJson(doc.toJson(), User.class));
            }
        });
        return list;
    }

    public User findByUsername(String username) {
        Document d;
        User u;

        MongoCollection<Document> collection = userClient.getCollection();

        if (username == null) {
            return null;
        } else {
            d = collection.find(eq("username", username)).first();

        }
        if (d == null) {
            return null;
        } else {
            u = new User(d);
        }
        return u;
    }

    /**
     * Get user using given user id
     *
     * @param userId user objectId in database
     * @return user if found or null
     */
    public User findById(String userId) {
        MongoCollection<Document> collection = userClient.getCollection();
        if (userId == null) {
            return null;
        }
        BasicDBObject query = new BasicDBObject("_id", new ObjectId(userId));
        Document doc = collection.find(query).first();
        if (doc == null) {
            return null;
        }
        return new User(doc);
    }

    //TODO: implement this
    public void deleteUser(int user_id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
