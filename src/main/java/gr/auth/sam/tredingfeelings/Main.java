
package gr.auth.sam.tredingfeelings;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.impl.client.HttpClients;

import com.mashape.unirest.http.Unirest;

import gr.auth.sam.tredingfeelings.impl.MongoStorage;
import gr.auth.sam.tredingfeelings.impl.Sentiment;
import gr.auth.sam.tredingfeelings.impl.Twitter;
import gr.auth.sam.tredingfeelings.impl.XChartPlotter;


/*
 * TODO doc
 */
public class Main {

    public static void main(String[] args) throws Exception {

        boolean clear = false;
        boolean gather = false;
        boolean proc = false;
        boolean graph = true;

        Params params = new Params();

        //

        setup();

        Stemmer stemmer = new Stemmer();

        final IStorage storage = new MongoStorage();
        storage.open();

        if (clear) {
            storage.drop();
        }

        if (gather && clear) {
            final ITwitter twitter = new Twitter();

            new Master(params, storage, twitter).start();
        }

        if (proc) {
            final ISentiment sentiment = new Sentiment();

            new Analyzer(params, storage, sentiment, stemmer).start();
        }

        if (graph) {
            final IPlotter plotter = new XChartPlotter();

            new Grapher(params, storage, plotter).start();
        }

        storage.close();
    }

    private static void setup() {

        // twitter sends some weird cookies, ignore them
        Unirest.setHttpClient(HttpClients.custom().disableCookieManagement().build());

        // suppress mongodb's logger
        Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);

    }

}
