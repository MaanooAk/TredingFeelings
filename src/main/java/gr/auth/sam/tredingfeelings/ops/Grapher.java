
package gr.auth.sam.tredingfeelings.ops;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bson.Document;

import gr.auth.sam.tredingfeelings.Operator;
import gr.auth.sam.tredingfeelings.Params;
import gr.auth.sam.tredingfeelings.serv.IPlotter;
import gr.auth.sam.tredingfeelings.serv.IStorage;
import gr.auth.sam.tredingfeelings.util.ProgressBar;


public class Grapher extends Operator {

    private static final int TOP_WORDS = 50;

    private static final int CHART_W = 800;
    private static final int CHART_H = 600;

    private static final String OUTPUT_FOLDER = "output";

    private final IStorage storage;
    private final IPlotter plotter;

    public Grapher(Params params, IStorage storage, IPlotter plotter) {
        super(params);
        this.storage = storage;
        this.plotter = plotter;
    }

    public void start() {

        clearOutput();

        progress = ProgressBar.create("Grapher", params.topicsCount * 3);

        for (String collection : storage.getCollections()) {
            progress.setMessage("Trend: " + collection);

            doWordPlots(collection);

            doUserPlots(collection);
        }

        progress.close();

    }

    private void clearOutput() {

        try {
            File folder = Paths.get(OUTPUT_FOLDER).toFile();

            if (folder.exists()) {
                for (File i : folder.listFiles())
                    i.delete();
            } else {
                folder.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to create output folder");
            System.exit(1);
        }

    }

    private void doWordPlots(String collection) {

        progress.incAndShow();
        createWordPlots(collection, "text");

        progress.incAndShow();
        createWordPlots(collection, "stemmed");

    }

    private void createWordPlots(String collection, String field) {

        Iterable<Document> tweets = storage.getTweets(collection);

        HashMap<String, Integer> counts = new HashMap<>();
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
        plotter.exportChart(OUTPUT_FOLDER + "/" + collection + "." + field + ".freq.png");

        plotter.createZipfChart(collection + " | " + field, CHART_W, CHART_H, "rank", rs, "frequency", fs);
        plotter.exportChart(OUTPUT_FOLDER + "/" + collection + "." + field + ".zipf.png");

    }

    private void doUserPlots(String collection) {

        progress.incAndShow();

        Iterable<Document> tweets = storage.getTweets(collection);

        HashMap<String, Integer> sent = new HashMap<>();
        HashMap<String, Integer> counts = new HashMap<>();
        ArrayList<Float> ratios = new ArrayList<>();
        ArrayList<Integer> xs = new ArrayList<>();
        ArrayList<String> labelx = new ArrayList<>(3);
        ArrayList<Integer> labely = new ArrayList<>(3);
        labelx.add("negative");
        labelx.add("neutral");
        labelx.add("positive");
        labely.add(0);
        labely.add(0);
        labely.add(0);

        for (Document t : tweets) {
            String id = t.get("user", Document.class).get("id") + "";
            int label = labelToInt(t.getString("label"));

            if (sent.containsKey(id)) {
                sent.put(id, sent.get(id) + label);
                counts.put(id, counts.get(id) + 1);
            } else {
                sent.put(id, label);
                counts.put(id, 1);

                int followers = t.get("user", Document.class).getInteger("followers_count");
                int following = t.get("user", Document.class).getInteger("friends_count");

                if (following == 0) following = 1;
                if (followers == 0) followers = 1;

//                float ratio = followers * 1f / following;
                float ratio = following * 1f / followers;
                ratios.add(ratio);
                xs.add(xs.size() + 1);
            }
        }

        for (String id : sent.keySet()) {
            float average = sent.get(id) * 1f / counts.get(id);

            int index = labelToIndex(average);
            labely.set(index, labely.get(index) + 1);
        }

        plotter.createSimpleChart(collection + " | Sentiment", CHART_W, CHART_H, "", labelx, "", labely);
        plotter.exportChart(OUTPUT_FOLDER + "/" + collection + ".sentiment.png");

        plotter.createCumulativeChart(collection + " | followers / friends", CHART_W, CHART_H, "", xs, "ratio", ratios);
        plotter.exportChart(OUTPUT_FOLDER + "/" + collection + ".ratios.png");
    }

    private static int labelToInt(String label) {
        if (label.startsWith("p")) return 1;
        if (label.startsWith("neg")) return -1;
        return 0;
    }

    private static int labelToIndex(float value) {
        return value < -0.33f ? 0 : value > 0.33f ? 2 : 1;
    }

}
