
package gr.auth.sam.tredingfeelings;

import org.apache.http.auth.AuthenticationException;
import org.json.JSONObject;

import com.mashape.unirest.http.exceptions.UnirestException;


/*
 * TODO doc
 */
public interface ITwitter {

    void authenticate() throws UnirestException, AuthenticationException;

    boolean isAuthenticated();

    JSONObject requestTrends(int woeid) throws UnirestException;

    JSONObject requestTweets(String topic) throws UnirestException;

    JSONObject requestTweets(String topic, String maxId) throws UnirestException;

}
