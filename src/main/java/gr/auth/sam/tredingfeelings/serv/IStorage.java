
package gr.auth.sam.tredingfeelings.serv;

import org.bson.Document;
import org.json.JSONObject;


/*
 * TODO doc
 */
public interface IStorage {

    void open();

    void close();

    void createCollection(String name);

    void dropCollection(String name);

    Iterable<String> getCollections();

    void insert(String collection, JSONObject object);

    void update(String collection, JSONObject oldObject, JSONObject newObject);
    
    Iterable<Document> getTweets(String collection);

    void drop();
    // TODO add methods to get the stored objects
}
