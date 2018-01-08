
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

            System.out.println("Grapher: work " + collection);
            work(storage.getTweets(collection));
        }

    }

    private void work(Iterable<Document> tweets) {

        for (Document d : tweets) {

            System.out.println("@");
            System.out.println(d.get("_id"));
            System.out.println(d.toJson().contains("stemmed"));
            
        }
        
    }

}
