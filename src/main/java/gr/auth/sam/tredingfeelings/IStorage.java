
package gr.auth.sam.tredingfeelings;

import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.json.JSONObject;


/*
 * TODO doc
 */
public interface IStorage {

    void open();

    void close();

    void createCollection(String name);

    void insert(String collection, JSONObject object);

    MongoCursor<Document> getTweets(String collection);

    void drop();
    // TODO add methods to get the stored objects
}
