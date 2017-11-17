
package gr.auth.sam.tredingfeelings;

import org.apache.http.auth.AuthenticationException;

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

    public Master(ITwitter twitter, ISentiment sentiment, IStorage storage) {
        this.twitter = twitter;
        this.sentiment = sentiment;
        this.storage = storage;
    }

    public void start() {
        // TODO Implement

        // test
        try {

            twitter.authenticate();
            System.out.println(twitter.requestTrends(woeid));

        } catch (AuthenticationException e) {
            e.printStackTrace();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

    }

}
