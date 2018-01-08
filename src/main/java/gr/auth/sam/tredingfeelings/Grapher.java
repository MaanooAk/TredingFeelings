
package gr.auth.sam.tredingfeelings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bson.Document;

import gr.auth.sam.tredingfeelings.util.ProgressBar;


public class Grapher {

    private static final int TOP_WORDS = 50;

    private final IStorage storage;
    private final IPlotter plotter;

    private final HashMap<String, Integer> map;

    private ProgressBar progress;

    public Grapher(IStorage storage, IPlotter plotter) {
        this.storage = storage;
        this.plotter = plotter;

        map = new HashMap<>();
    }

    public void start() {

        progress = ProgressBar.create("Grapher", Master.topicsCount);

        for (String collection : storage.getCollections()) {
            progress.incAndShow("Trend: " + collection);

            System.out.println("Grapher: work " + collection);
            work(collection);
        }

        progress.close();
    }

    private void work(String collection) {

        ProgressBar progress = ProgressBar.create(this.progress, "Grapher sub",2);
        
        workWordFreq(collection, "text");
        progress.incAndShow();
        
        workWordFreq(collection, "stemmed");
        progress.incAndShow();

        progress.close();
    }

    private void workWordFreq(String collection, String field) {

        Iterable<Document> tweets = storage.getTweets(collection);

        HashMap<String, Integer> counts = map;
        counts.clear();

        for (Document t : tweets) {
            for (String word : t.getString(field).split(" ")) {

                if (word.length() < 2) continue;
                
                int count = counts.getOrDefault(word, 0) + 1;
                counts.put(word, count);
            }
        }

        List<Entry<String, Integer>> list = counts.entrySet().stream()
                .sorted((Map.Entry<String, Integer> i1, Map.Entry<String, Integer> i2) -> {
                    return -Integer.compare(i1.getValue(), i2.getValue());
                }).limit(TOP_WORDS).collect(Collectors.toList());

        System.out.println(collection + " " + field);
        list.forEach(i -> System.out.println(" - " + i.getKey() + " " + i.getValue()));
        
        ArrayList<String> xs = new ArrayList<>();
        ArrayList<Integer> ys = new ArrayList<>();

        list.forEach(i -> xs.add(i.getKey()));
        list.forEach(i -> ys.add(i.getValue()));
        
        plotter.createBarChart(collection + " | " + field, 1000, 1000, "word", xs, "", ys);
        plotter.showChart();
    }

}
