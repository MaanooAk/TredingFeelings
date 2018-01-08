
package gr.auth.sam.tredingfeelings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bson.Document;


public class Grapher {

    private static final int TOP_WORDS = 50;

    private final IStorage storage;
    private final IPlotter plotter;

    private final HashMap<String, Integer> map;

    public Grapher(IStorage storage, IPlotter plotter) {
        this.storage = storage;
        this.plotter = plotter;

        map = new HashMap<>();
    }

    public void start() {

        for (String collection : storage.getCollections()) {

            System.out.println("Grapher: work " + collection);
            work(collection);
        }

    }

    private void work(String collection) {

        workWordFreq(collection, "text");
        workWordFreq(collection, "stemmed");

    }

    private void workWordFreq(String collection, String field) {

        Iterable<Document> tweets = storage.getTweets(collection);

        HashMap<String, Integer> counts = map;
        counts.clear();

        for (Document t : tweets) {
            for (String word : t.getString(field).split(" ")) {

                int count = counts.getOrDefault(word, 0) + 1;
                counts.put(word, count);
            }
        }

        List<Entry<String, Integer>> list = counts.entrySet().stream()
                .sorted((Map.Entry<String, Integer> i1, Map.Entry<String, Integer> i2) -> {
                    return Integer.compare(i1.getValue(), i2.getValue());
                }).limit(TOP_WORDS).collect(Collectors.toList());

        System.out.println(collection + " " + field);
        list.forEach(i -> System.out.println(" - " + i.getKey() + " " + i.getValue()));
    }

}
