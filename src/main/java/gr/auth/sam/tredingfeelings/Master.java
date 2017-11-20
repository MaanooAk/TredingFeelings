
package gr.auth.sam.tredingfeelings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

import org.apache.http.auth.AuthenticationException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mashape.unirest.http.exceptions.UnirestException;


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
    private ArrayList<String> stopwords;
    private ArrayList<String> trends;

    public Master(ITwitter twitter, ISentiment sentiment, IStorage storage) {
        this.twitter = twitter;
        this.sentiment = sentiment;
        this.storage = storage;
    }

    public void start() {
        // TODO Implement

        try {
            storage.open();
            loadStopWords();
            storage.drop();

            twitter.authenticate();

            gatherData();

            storage.close();
        } catch (AuthenticationException e) {
            e.printStackTrace();
        } catch (UnirestException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private ArrayList<String> getTopTrends(JSONObject jsonObject) {

        ArrayList<String> ret = new ArrayList<>();

        JSONArray trends = jsonObject.getJSONArray("trends");

        for (int i = 0; i < 5; i++) {
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
        System.out.println(("Master: storing tweets from: " + name));
        storage.createCollection(name);

        JSONObject tweets = twitter.requestTweets(name);
        System.out.println(("Master:    fetched first 100 tweets from: " + name));
        processTweets(tweets, name);
        String max_id = getMax_id(tweets);

        for (int i = 0; i < 14 && !max_id.equals("not_exist"); i++) {
            tweets = twitter.requestTweets(name, max_id);
            System.out.println(("Master:    fetched first " +
                    String.valueOf(200 + i * 100) + " tweets from: " + name));

            processTweets(tweets, name);
            max_id = getMax_id(tweets);
        }

    }

    private ArrayList<JSONObject> processTweets(JSONObject response, String collection) {
        ArrayList<JSONObject> validTweets = new ArrayList<>();

        JSONArray statuses = response.getJSONArray("statuses");
        for (int i = 0; i < statuses.length(); i++) {
            JSONObject tweet = statuses.getJSONObject(i);
            if (validateTweet(tweet)) {
                storage.insert(collection, tweet);
            }
        }

        return validTweets;
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

    private String normalize(String text) {
        ArrayList<String> words = new ArrayList<>(Arrays.asList(text.split(" ")));

        for (int i = 0; i < words.size(); i++) {
            if (isValid(words.get(i))) {
                // TODO what to do with this..?
                String temp = words.get(i).toLowerCase();
            } else {
                words.remove(i);
                i--;
            }
        }

        return "";
    }

    private boolean isValid(String s) {
        if (!isWord(s))
            return false;
        return !isStopWord(s);
    }

    private boolean isStopWord(String s) {
        return stopwords.contains(s);
    }

    private boolean isWord(String word) {
        char[] chars = word.toCharArray();

        for (char c : chars) {
            if (!Character.isLetter(c)) {
                return false;
            }
        }

        return true;

    }

    private void loadStopWords() throws IOException {
        stopwords = new ArrayList<>();
        Stream<String> stream = Files.lines(Paths.get("stopwords_en.txt"));
        stream.forEach(item -> stopwords.add(item));
    }

}
