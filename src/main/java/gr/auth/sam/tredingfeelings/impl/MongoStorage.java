
package gr.auth.sam.tredingfeelings.impl;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONObject;

import gr.auth.sam.tredingfeelings.IStorage;


/*
 * TODO doc
 */
public class MongoStorage implements IStorage {
    private MongoDatabase database;
    private MongoClient mongoClient;
    private MongoCredential credential;

    public MongoStorage() {
    }

    @Override
    public void open() {
        try {
            mongoClient = new MongoClient("localhost", 27017);

            credential = MongoCredential.createCredential("admin", "twitter",
                    "twitter_pass".toCharArray());

            database = mongoClient.getDatabase("twitter");

            System.out.println("Connection established...");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void close() {
        mongoClient.close();
    }

    @Override
    public void createCollection(String name) {
        database.createCollection(name);
        System.out.println("MongoStorage: Collection " + name + " created...");
    }

    @Override
    public void insert(String collection, JSONObject object) {
        database.getCollection(collection).insertOne(Document.parse(object.toString()));
    }

    @Override
    public void drop() {
        mongoClient.dropDatabase("twitter");
    }
}
