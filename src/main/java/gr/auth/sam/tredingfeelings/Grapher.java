
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

    private static final int CHART_W = 800;
    private static final int CHART_H = 600;

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

        progress = ProgressBar.create("Grapher", Master.topicsCount * 2);

        for (String collection : storage.getCollections()) {
//            progress.incAndShow("Trend: " + collection);
            progress.setMessage("Trend: " + collection);

            work(collection);
        }

        progress.close();
    }

    private void work(String collection) {

//        ProgressBar progress = ProgressBar.create(this.progress, "Grapher sub",2);

        workWordFreq(collection, "text");
        progress.incAndShow();

        workWordFreq(collection, "stemmed");
        progress.incAndShow();

//        progress.close();
    }

    private void workWordFreq(String collection, String field) {

        Iterable<Document> tweets = storage.getTweets(collection);

        HashMap<String, Integer> counts = map;
        counts.clear();

        for (Document t : tweets) {
            for (String word : t.getString(field).split(" ")) {

                // skip non words
                if (word.length() < 2) continue;

                // skip words related to the trend
                if (word.length() < 2 || collection.toLowerCase().contains(word.toLowerCase())) continue;

                int count = counts.getOrDefault(word, 0) + 1;
                counts.put(word, count);
            }
        }

        List<Entry<String, Integer>> list = counts.entrySet().stream()
                .sorted((Map.Entry<String, Integer> i1, Map.Entry<String, Integer> i2) -> {
                    return -Integer.compare(i1.getValue(), i2.getValue());
                }).limit(TOP_WORDS).collect(Collectors.toList());

        float sum = list.stream().mapToInt(i -> i.getValue()).sum();

        ArrayList<String> xs = new ArrayList<>();
        ArrayList<Integer> ys = new ArrayList<>();
        ArrayList<Float> rs = new ArrayList<>();
        ArrayList<Float> fs = new ArrayList<>();

        list.forEach(i -> xs.add(i.getKey()));
        list.forEach(i -> ys.add(i.getValue()));
        list.forEach(i -> rs.add(rs.size() + 1f));
        list.forEach(i -> fs.add(i.getValue() / sum));

        plotter.createBarChart(collection + " | " + field, CHART_W, CHART_H, "word", xs, "", ys);
        plotter.exportChart("output/" + collection + "." + field + ".freq.png");

        plotter.createZipfChart(collection + " | " + field, CHART_W, CHART_H, "rank", rs, "frequency", fs);
        plotter.exportChart("output/" + collection + "." + field + ".zipf.png");

    }

}
