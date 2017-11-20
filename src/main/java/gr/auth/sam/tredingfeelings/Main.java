
package gr.auth.sam.tredingfeelings;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.impl.client.HttpClients;

import com.mashape.unirest.http.Unirest;

import gr.auth.sam.tredingfeelings.impl.MongoStorage;
import gr.auth.sam.tredingfeelings.impl.Sentiment;
import gr.auth.sam.tredingfeelings.impl.Twitter;


/*
 * TODO doc
 */
public class Main {

    public static void main(String[] args) throws Exception {

        // twitter sends some weird cookies, ignore them
        Unirest.setHttpClient(HttpClients.custom().disableCookieManagement().build());

        // suppress mongodb's logger
        Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);

        final ITwitter twitter = new Twitter();
        final ISentiment sentiment = new Sentiment();
        final IStorage storage = new MongoStorage();

        Master m = new Master(twitter, sentiment, storage);
        m.start();

    }

}
