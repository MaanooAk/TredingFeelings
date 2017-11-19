
package gr.auth.sam.tredingfeelings;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


/*
 * TODO doc
 */
public interface IStorage {

    void open();

    void close();

    void createCollection(String name);

    void insert(String collection, JSONObject object);

    ArrayList<JSONObject> getTweets(String collection);

    void drop();
    // TODO add methods to get the stored objects
}
