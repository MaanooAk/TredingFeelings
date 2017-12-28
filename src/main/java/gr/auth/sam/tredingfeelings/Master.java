package gr.auth.sam.tredingfeelings;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.mongodb.client.MongoCursor;
import org.apache.http.auth.AuthenticationException;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/*
 * TODO doc
 */
public class Master {

    // TODO extend

    public static final int woeid = 23424977; // United States
    public static final int topicsCount = 5; // the top 5 trends
    public static final int tweetsCount = 1500; // 1500 tweets for each topic

    //

    private final ITwitter twitter;
    private final ISentiment sentiment;
    private final IStorage storage;

    private final Stemmer stemmer;

    private ArrayList<String> trends;

    public Master(ITwitter twitter, ISentiment sentiment, IStorage storage) {
        this.twitter = twitter;
        this.sentiment = sentiment;
        this.storage = storage;

        stemmer = new Stemmer();
    }

    public void start() {
        // TODO Implement

        try {
            storage.open();

            storage.drop();

            twitter.authenticate();

            gatherData();
            metrics();

            storage.getTweets(trends.get(0));
            storage.close();
        } catch (AuthenticationException | UnirestException e) {
            e.printStackTrace();
        }

    }

    private ArrayList<String> getTopTrends(JSONObject jsonObject) {

        ArrayList<String> ret = new ArrayList<>();

        JSONArray trends = jsonObject.getJSONArray("trends");

        for (int i = 0; i < topicsCount; i++) {
            JSONObject object = trends.getJSONObject(i);
            ret.add(object.getString("name"));
        }

        return ret;
    }


    private void gatherData() throws UnirestException {
        trends = getTopTrends(twitter.requestTrends(woeid));

        for (String topic : trends) {
            storeTweets(topic);
        }
    }

    private void storeTweets(String name) throws UnirestException {
        System.out.println(("Master:    storing tweets from: " + name));
        storage.createCollection(name);

        String max_id;
        int count = 0;

        do {
            JSONObject tweets = twitter.requestTweets(name);
            max_id = getMax_id(tweets);

            JSONArray statuses = tweets.getJSONArray("statuses");
            for (int i = 0; i < statuses.length() && count < tweetsCount; i++) {
                JSONObject tweet = statuses.getJSONObject(i);
                if (validateTweet(tweet)) {
                    count++;
                    storage.insert(name, tweet);
                }
            }

            System.out.println(("Master:    tweets gathered from " + name + ": " + count));
        } while (!max_id.equals("not_exist") && count < tweetsCount);
    }

    private boolean validateTweet(JSONObject tweet) {
        return tweet.getString("text").toLowerCase().indexOf("rt") != 0;
    }

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
            max_id = "not_exist";
        }

        return max_id;
    }

    public void metrics() {
        for (String trent : trends) {
            analizeTrent(trent);
        }
    }

    private void analizeTrent(String collection) {

        for (MongoCursor<Document> it = storage.getTweets(collection); it.hasNext(); ) {
            analizeTweet(new JSONObject(it.next().toJson()));
        }
    }

    private void analizeTweet(JSONObject tweet) {
        String text = tweet.getString("text");
        ArrayList<String> words = stemmer.normalize(text);

        System.out.println("analizeTweet: " + text);
        System.out.println("analizedTweet: " + words.toString());
        // TODO impl
        System.out.println("-------------------------------------------");
    }
}
