
package gr.auth.sam.tredingfeelings.ops;

import org.bson.Document;
import org.json.JSONObject;

import com.mashape.unirest.http.exceptions.UnirestException;

import gr.auth.sam.tredingfeelings.Operator;
import gr.auth.sam.tredingfeelings.Params;
import gr.auth.sam.tredingfeelings.serv.ISentiment;
import gr.auth.sam.tredingfeelings.serv.IStorage;
import gr.auth.sam.tredingfeelings.util.ProgressBar;


public class Analyzer extends Operator {

    private final IStorage storage;
    private final ISentiment sentiment;
    private final Stemmer stemmer;

    public Analyzer(Params params, IStorage storage, ISentiment sentiment, Stemmer stemmer) {
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

        ProgressBar progress = ProgressBar.create(this.progress, "Sentiment sub", params.tweetsCount);
        progress.setMessage(collection);

        for (Document i : storage.getTweets(collection)) {
            progress.incAndShow();

            JSONObject tweet = new JSONObject(i.toJson());
            if (tweet.has("stemmed")) continue; // has been analyzed

            JSONObject etweet = analizeTweet(tweet);

            if (etweet == null) {
                System.out.println("Stopped");
                System.exit(0);
            }

            storage.update(collection, tweet, etweet);
        }

        progress.close();
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

        // TODO DEBUG
//        System.out.println("original: " + text);
//        System.out.println("stemmed:  " + stemmed);
//        System.out.println("sent:     " + sent);
//        System.out.println(etweet);
//        System.out.println("-------------------------------------------");

        return etweet;
    }

}
