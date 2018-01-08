
package gr.auth.sam.tredingfeelings.ops;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.bson.Document;
import org.json.JSONObject;

import com.mashape.unirest.http.exceptions.UnirestException;

import gr.auth.sam.tredingfeelings.Operator;
import gr.auth.sam.tredingfeelings.Params;
import gr.auth.sam.tredingfeelings.serv.ISentiment;
import gr.auth.sam.tredingfeelings.serv.IStorage;
import gr.auth.sam.tredingfeelings.util.ProgressBar;


public class AnalyzerMulti extends Operator {

    private final IStorage storage;
    private final ISentiment sentiment;
    private final Stemmer stemmer;

    private ProgressBar sprogress;

    public AnalyzerMulti(Params params, IStorage storage, ISentiment sentiment, Stemmer stemmer) {
        super(params);
        this.storage = storage;
        this.sentiment = sentiment;
        this.stemmer = stemmer;
    }

    public void start() {

        progress = ProgressBar.create("Sentiment", params.topicsCount);

        for (String trent : storage.getCollections()) {
            progress.incAndShow(trent);

            analizeTrent(trent);
        }

        progress.close();
    }

    private void analizeTrent(String collection) {

        final ThreadPoolExecutor tpool = new ThreadPoolExecutor(20, 20, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(40));
        tpool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        sprogress = ProgressBar.create(this.progress, "Sentiment sub", params.tweetsCount);
        sprogress.setMessage(collection);

        Iterator<Document> iterator = storage.getTweets(collection).iterator();

        while (true) {
            Document i;

            synchronized (storage) {
                if (!iterator.hasNext()) break;
                i = iterator.next();
            }

            JSONObject tweet = new JSONObject(i.toJson());
            if (tweet.has("stemmed")) {
                synchronized (sprogress) {
                    sprogress.incAndShow();
                }
                continue; // has been analyzed
            }

            tpool.submit(new AnalizeTweetCall(collection, tweet));

        }

        System.out.println("Waitting responses");

        tpool.shutdown();

        try {
            tpool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {}

        sprogress.close();
    }

    private final class AnalizeTweetCall implements Runnable {

        private String collection;
        private final JSONObject tweet;

        public AnalizeTweetCall(String collection, JSONObject tweet) {
            this.collection = collection;
            this.tweet = tweet;
        }

        @Override
        public void run() {

            JSONObject etweet = analizeTweet(tweet);

            if (etweet == null) {
                System.out.println("Stopped");
                System.exit(0);
            }

            synchronized (storage) {
                storage.update(collection, tweet, etweet);
            }

            synchronized (sprogress) {
                sprogress.incAndShow();
            }
        }

    }

    private JSONObject analizeTweet(JSONObject tweet) {

        String text = tweet.getString("text");

        String stemmed = stemmer.stem(text);
        // TODO also remove trend related words

        JSONObject sent = null;
        try {
            sent = sentiment.analyze(stemmed);
        } catch (UnirestException e) {
            e.printStackTrace();
            System.out.println("Sentiment failed");
            System.exit(1);
        }

        String label = sent.getString("label");
        double pos = sent.getJSONObject("probability").getDouble("pos");
        double neg = sent.getJSONObject("probability").getDouble("neg");
        double neutral = sent.getJSONObject("probability").getDouble("neutral");

        // construct the extended tweet json object

        JSONObject etweet = new JSONObject(tweet, JSONObject.getNames(tweet));
        etweet.put("stemmed", stemmed);
        etweet.put("label", label);
        etweet.put("pos_prob", pos);
        etweet.put("neg_prob", neg);
        etweet.put("neutral_prob", neutral);

        return etweet;
    }

}
