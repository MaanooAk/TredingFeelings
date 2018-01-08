
package gr.auth.sam.tredingfeelings.ops;

import java.util.ArrayList;

import org.apache.http.auth.AuthenticationException;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.exceptions.UnirestException;

import gr.auth.sam.tredingfeelings.Operator;
import gr.auth.sam.tredingfeelings.Params;
import gr.auth.sam.tredingfeelings.serv.IStorage;
import gr.auth.sam.tredingfeelings.serv.ITwitter;
import gr.auth.sam.tredingfeelings.util.ProgressBar;


/*
 * TODO doc
 */
public class Gatherer extends Operator {

    private final IStorage storage;
    private final ITwitter twitter;

    private final ArrayList<String> trends;

    public Gatherer(Params params, IStorage storage, ITwitter twitter) {
        super(params);
        this.storage = storage;
        this.twitter = twitter;

        trends = new ArrayList<>(params.topicsCount);
    }

    @Override
    public void start() {

        try {

            twitter.authenticate();
            gatherData();

        } catch (AuthenticationException | UnirestException e) {
            e.printStackTrace();
            System.exit(0);
        }

    }

    private void fillTopTrends(JSONObject jsonObject) {

        JSONArray trends = jsonObject.getJSONArray("trends");

        for (int i = 0; i < params.topicsCount; i++) {
            JSONObject object = trends.getJSONObject(i);
            this.trends.add(object.getString("name"));
        }

    }

    private void gatherData() throws UnirestException {
        fillTopTrends(twitter.requestTrends(params.woeid));

        progress = ProgressBar.create("Grapher", params.topicsCount);

        for (String topic : trends) {
            progress.incAndShow(topic);

            storeTweets(topic);
        }

        progress.close();
    }

    // TODO split
    private void storeTweets(String name) throws UnirestException {

        ProgressBar progress = ProgressBar.create(this.progress, "Gather", params.tweetsCount);
        progress.setMessage(name);

        storage.createCollection(name);

        int count = 0;

        JSONObject tweets = twitter.requestTweets(name);

        while (count < params.tweetsCount) {

            JSONArray statuses = tweets.getJSONArray("statuses");
            for (int i = 0; i < statuses.length() && count < params.tweetsCount; i++) {
                JSONObject tweet = statuses.getJSONObject(i);
                if (validateTweet(tweet)) {
                    count++;
                    storage.insert(name, tweet);
                }
            }

            progress.setAndShow(count, params.tweetsCount - count + " left");

            String max_id = getMaxId(tweets);
            if (max_id == null) break;

            tweets = twitter.requestTweets(name, max_id);
        }

        progress.close();
    }

    private boolean validateTweet(JSONObject tweet) {
        // TODO move to twitter class
        return tweet.getString("text").toLowerCase().indexOf("rt") != 0;
    }

    // TODO fix
    private String getMaxId(JSONObject tweets) {

        if (!tweets.has("search_metadata") || !tweets.getJSONObject("search_metadata").has("next_results")) {
            return null;
        }

        final String line = tweets.getJSONObject("search_metadata").getString("next_results");

        final String key = "max_id=";
        final int start = line.indexOf(key) + key.length();
        final int end = line.indexOf("&", start);

        return line.substring(start, end);
    }

}
