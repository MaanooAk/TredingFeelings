
package gr.auth.sam.tredingfeelings.impl;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

import gr.auth.sam.tredingfeelings.IStorage;


/*
 * TODO doc
 */
public class MongoStorage implements IStorage {

    private static final String DB_HOST = "localhost";
    private static final int DB_HOST_PORT = 27017;
    private static final String DB_NAME = "twitter";

    private MongoDatabase database;
    private MongoClient mongoClient;

    public MongoStorage() {

    }

    @Override
    public void open() {

        try {
            mongoClient = new MongoClient(DB_HOST, DB_HOST_PORT);

            database = mongoClient.getDatabase(DB_NAME);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("MongoStorage: Connection established");
    }

    @Override
    public void close() {
        mongoClient.close();
    }

    @Override
    public void createCollection(String name) {
        database.createCollection(name);

        System.out.println("MongoStorage: Collection " + name + " created");
    }

    @Override
    public void dropCollection(String name) {
        database.getCollection(name).drop();
    }

    @Override
    public MongoIterable<String> getCollections() {
        return database.listCollectionNames();
    }

    @Override
    public void insert(String collection, JSONObject object) {
        database.getCollection(collection).insertOne(Document.parse(object.toString()));
    }

    @Override
    public void update(String collection, JSONObject oldObject, JSONObject newObject) {
        MongoCollection<Document> c = database.getCollection(collection);

        Document filter = new Document("_id", new ObjectId(oldObject.getJSONObject("_id").getString("$oid")));

        newObject.remove("_id");
        c.replaceOne(filter, Document.parse(newObject.toString()));
    }

    @Override
    public FindIterable<Document> getTweets(String collection) {
        MongoCollection<Document> tweets = database.getCollection(collection);
        return tweets.find();
    }

    @Override
    public void drop() {
        mongoClient.dropDatabase(DB_NAME);
    }
}
