package gr.auth.sam.tredingfeelings.impl;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoDatabase;
import gr.auth.sam.tredingfeelings.IStorage;
import org.json.JSONObject;

public class Storage implements IStorage {
    private MongoDatabase database;
    private MongoClient mongoClient;
    private MongoCredential credential;

    @Override
    public void open() {
        try {
            mongoClient = new MongoClient("localhost", 27017);

            credential = MongoCredential.createCredential("admin", "twitter",
                    "twitter_pass".toCharArray());

            database = mongoClient.getDatabase("twitter");

            System.out.println("Connection established");

            database.createCollection("twitterCollection");

            System.out.println("Collection created established");
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

    }

    @Override
    public void insert(String collection, JSONObject object) {

    }
}
