
package gr.auth.sam.tredingfeelings;

import org.bson.Document;
import org.json.JSONObject;

import com.mongodb.client.MongoCursor;


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

    MongoCursor<Document> getTweets(String collection);

    void drop();
    // TODO add methods to get the stored objects
}
