
package gr.auth.sam.tredingfeelings;

import org.json.JSONObject;


/*
 * TODO doc
 */
public interface IStorage {

    void open();

    void close();

    void createCollection(String name);

    void insert(String collection, JSONObject object);

    // TODO add methods to get the stored objects
}
