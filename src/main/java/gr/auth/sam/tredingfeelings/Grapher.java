
package gr.auth.sam.tredingfeelings;

import org.bson.Document;

public class Grapher {

    private IStorage storage;
    private IPlotter plotter;

    public Grapher(IStorage storage, IPlotter plotter) {
        this.storage = storage;
        this.plotter = plotter;

    }

    public void start() {

        for (String collection : storage.getCollections()) {
            if (!collection.endsWith("_")) continue;
            
            work(storage.getTweets(collection));    
        }
        
    }

    private void work(Iterable<Document> tweets) {
        
        
        
    }

}
