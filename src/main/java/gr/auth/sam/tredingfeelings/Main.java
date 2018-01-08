
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

        boolean clear = true;
        boolean gather = true;
        boolean proc = true;
        boolean graph = true;

        //

        setup();

        final IStorage storage = new MongoStorage();
        storage.open();

        if (clear) {
            storage.drop();
        }

        if (gather || proc) {
            final ITwitter twitter = new Twitter();
            final ISentiment sentiment = new Sentiment();

            Master m = new Master(twitter, sentiment, storage);

            if (gather && clear) {
                m.startGather();
            }

            if (proc) {
                m.startProc();
            }
        }

        if (graph) {
            final IPlotter plotter = new XChartPlotter();

            new Grapher(storage, plotter).start();
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
