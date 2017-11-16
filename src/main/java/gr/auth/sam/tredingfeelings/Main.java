
package gr.auth.sam.tredingfeelings;

import org.apache.http.impl.client.HttpClients;

import com.mashape.unirest.http.Unirest;

import gr.auth.sam.tredingfeelings.impl.MongoStorage;
import gr.auth.sam.tredingfeelings.impl.Twitter;


/*
 * TODO doc
 */
public class Main {

    public static void main(String[] args) throws Exception {

        // twitter sends some weird cookies, ignore them
        Unirest.setHttpClient(HttpClients.custom().disableCookieManagement().build());

        final ITwitter twitter = new Twitter();
        final IStorage storage = new MongoStorage();

        Master m = new Master(twitter, storage);
        m.start();

    }

}
