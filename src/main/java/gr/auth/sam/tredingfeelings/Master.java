
package gr.auth.sam.tredingfeelings;

import java.util.ArrayList;

import org.apache.http.auth.AuthenticationException;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mashape.unirest.http.exceptions.UnirestException;

import gr.auth.sam.tredingfeelings.util.ProgressBar;


/*
 * TODO doc
 */
public class Master {

    // TODO extend

    public static final int woeid = 23424977; // United States
    public static final int topicsCount = 2; // the top 5 trends
    public static final int tweetsCount = 200; // 1500 tweets for each topic

    //

    private final ITwitter twitter;
    private final ISentiment sentiment;
    private final IStorage storage;

    private final Stemmer stemmer;
    private final ArrayList<String> trends;
    
    private ProgressBar progress;

    public Master(ITwitter twitter, ISentiment sentiment, IStorage storage) {
        this.twitter = twitter;
        this.sentiment = sentiment;
        this.storage = storage;

        stemmer = new Stemmer();
        trends = new ArrayList<>();
    }

    public void start() {

        try {

            storage.drop(); // TODO handle

            twitter.authenticate();

            gatherData();
            metrics();

            storage.getTweets(trends.get(0));

        } catch (AuthenticationException | UnirestException e) {
            e.printStackTrace();
        }

    }

    private void fillTopTrends(JSONObject jsonObject) {

        JSONArray trends = jsonObject.getJSONArray("trends");

        for (int i = 0; i < topicsCount; i++) {
            JSONObject object = trends.getJSONObject(i);
            this.trends.add(object.getString("name"));
        }

    }

    private void gatherData() throws UnirestException {
        fillTopTrends(twitter.requestTrends(woeid));

        progress = ProgressBar.create("Grapher", Master.topicsCount);
        
        for (String topic : trends) {
            progress.incAndShow(topic);
            
            storeTweets(topic);
        }
        
        progress.close();
    }

    // TODO split
    private void storeTweets(String name) throws UnirestException {
        System.out.println(("Master:    storing tweets from: " + name));
        storage.createCollection(name);

        String max_id;
        int count = 0;

        ProgressBar progress = ProgressBar.create(this.progress, "Gather", tweetsCount);
        progress.setMessage(name);
        
        do {
            JSONObject tweets = twitter.requestTweets(name); // TODO fix
            max_id = getMax_id(tweets);

            JSONArray statuses = tweets.getJSONArray("statuses");
            for (int i = 0; i < statuses.length() && count < tweetsCount; i++) {
                JSONObject tweet = statuses.getJSONObject(i);
                if (validateTweet(tweet)) {
                    count++;
                    storage.insert(name, tweet);
                }
            }
            
            progress.setAndShow(count);

            System.out.println(("Master:    tweets gathered from " + name + ": " + count));
        } while (!max_id.equals("not_exist") && count < tweetsCount);
        
        progress.close();
    }

    private boolean validateTweet(JSONObject tweet) {
        // TODO move to twitter class
        return tweet.getString("text").toLowerCase().indexOf("rt") != 0;
    }

    // TODO fix
    private String getMax_id(JSONObject object) {
        String max_id = "";
        JSONObject search_metadata = object.getJSONObject("search_metadata");

        // check if the results contains max_id, if not there are no more results
        try {
            String next_results = search_metadata.getString("next_results");
            if (next_results.contains("max_id")) {
                String[] firstSplit = next_results.split("max_id=");
                String s = firstSplit[1];
                String[] secondSplit = s.split("&");
                max_id = secondSplit[0];
            }
        } catch (JSONException e) {
            max_id = "not_exist"; // TODO fix
        }

        return max_id;
    }

    public void metrics() {

        progress = ProgressBar.create("Sentiment", Master.topicsCount);

        for (String trent : trends) {
            progress.incAndShow(trent);
            
            analizeTrent(trent);
        }
        
        progress.close();
    }

    private void analizeTrent(String collection) {

        ProgressBar progress = ProgressBar.create(this.progress, "Sentiment sub", tweetsCount);
        progress.setMessage(collection);
        
        for (Document i : storage.getTweets(collection)) {

            JSONObject tweet = new JSONObject(i.toJson());
            JSONObject etweet = analizeTweet(tweet);

            if (etweet == null) {
                System.out.println("Stopped");
                System.exit(0);
            }

            storage.update(collection, tweet, etweet);
            
            progress.incAndShow();
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
            return null;
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
