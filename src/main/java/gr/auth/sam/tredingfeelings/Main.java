
package gr.auth.sam.tredingfeelings;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.impl.client.HttpClients;

import com.mashape.unirest.http.Unirest;

import gr.auth.sam.tredingfeelings.ops.Analyzer;
import gr.auth.sam.tredingfeelings.ops.Gatherer;
import gr.auth.sam.tredingfeelings.ops.Grapher;
import gr.auth.sam.tredingfeelings.ops.Stemmer;
import gr.auth.sam.tredingfeelings.serv.IPlotter;
import gr.auth.sam.tredingfeelings.serv.ISentiment;
import gr.auth.sam.tredingfeelings.serv.IStorage;
import gr.auth.sam.tredingfeelings.serv.ITwitter;
import gr.auth.sam.tredingfeelings.serv.impl.MongoStorage;
import gr.auth.sam.tredingfeelings.serv.impl.Sentiment;
import gr.auth.sam.tredingfeelings.serv.impl.Twitter;
import gr.auth.sam.tredingfeelings.serv.impl.XChartPlotter;


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

            new Gatherer(params, storage, twitter).start();
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
