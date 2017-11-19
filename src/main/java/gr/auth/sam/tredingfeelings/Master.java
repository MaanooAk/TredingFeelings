
package gr.auth.sam.tredingfeelings;

import org.apache.http.auth.AuthenticationException;

import com.mashape.unirest.http.exceptions.UnirestException;
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
    public static final int tweetsCount = 400; // 1500 tweets for each topic

    //

    private final ITwitter twitter;
    private final ISentiment sentiment;
    private final IStorage storage;

    public Master(ITwitter twitter, ISentiment sentiment, IStorage storage) {
        this.twitter = twitter;
        this.sentiment = sentiment;
        this.storage = storage;
    }

    public void start() {
        // TODO Implement
        // test
        try {
            storage.open();
            storage.drop();

            twitter.authenticate();

            gatherData();
            test();

            storage.close();
        } catch (AuthenticationException e) {
            e.printStackTrace();
        } catch (UnirestException e) {
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

    ArrayList<String> trends;

    private void gatherData() throws UnirestException {
        trends = getTopTrends(twitter.requestTrends(woeid));

        for (String topic : trends) {
            storeTweets(topic);
        }

    }

    private void storeTweets(String name) throws UnirestException {
        System.out.println(("Master: storing tweets from: " + name));

        storage.createCollection(name);

        JSONObject tweets = twitter.requestTweets(name);
        System.out.println(("Master:    fetched first 100 tweets from: " + name));
        tweets = removeRetweets(tweets);
        storage.insert(name, tweets);
        String max_id = getMax_id(tweets);

        for (int i = 0; i < 14 && !max_id.equals("not_exist"); i++) {
            tweets = twitter.requestTweets(name, max_id);
            System.out.println(("Master:    fetched first " +
                    String.valueOf(200 + i * 100) + " tweets from: " + name));
            tweets = removeRetweets(tweets);

            storage.insert(name, tweets);
            max_id = getMax_id(tweets);
        }

    }

    private JSONObject removeRetweets(JSONObject tweets) {
        JSONArray statuses = tweets.getJSONArray("statuses");
        ArrayList<Integer> toRemove = new ArrayList<>();

        // make remove list
        for (int i = 0; i < statuses.length(); i++) {
            JSONObject t = statuses.getJSONObject(i);
            if (t.getString("text").indexOf("RT") == 0) {
                toRemove.add(i);
            }
        }

        // remove selected
        int offset = 0;
        for (Integer i : toRemove) {
            statuses.remove(i - offset);
            offset++;
        }

        tweets.remove("statuses");
        tweets.put("statuses", statuses);
        return tweets;
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

    private void test() {
        ArrayList<JSONObject> t = storage.getTweets("#SundayMorning");
        for (JSONObject object : t) {
            JSONArray array = object.getJSONArray("statuses");
            for (int i = 0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);
                System.out.println("----------------------------------------------------------------------");
                System.out.println(o.getString("text"));
            }
        }
    }
}
