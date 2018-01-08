
package gr.auth.sam.tredingfeelings.serv.impl;

import java.io.IOException;
import java.util.function.UnaryOperator;

import org.apache.http.auth.AuthenticationException;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;

import gr.auth.sam.tredingfeelings.serv.ITwitter;


/*
 * TODO doc
 */
public class Twitter implements ITwitter {

    // https://developer.twitter.com/en/docs/basics/authentication/api-reference/token

    private static final String EP_OAUTH2_TOKEN = "https://api.twitter.com/oauth2/token";
    private static final String EP_OAUTH2_TOKEN_TYPE = "grant_type";
    private static final String EP_OAUTH2_TOKEN_TYPE_V = "client_credentials";

    // https://developer.twitter.com/en/docs/trends/trends-for-location/api-reference/get-trends-place

    private static final String EP_TRENDS = "https://api.twitter.com/1.1/trends/place.json";
    private static final String EP_TREND_ID = "id";

    // https://developer.twitter.com/en/docs/tweets/search/api-reference/get-search-tweets

    private static final String EP_TWEETS = "https://api.twitter.com/1.1/search/tweets.json";
    private static final String EP_TWEETS_Q = "q";
    private static final String EP_TWEETS_MAXID = "max_id";
    private static final String EP_TWEETS_LANG = "lang";
    private static final String EP_TWEETS_LANG_V = "en";
    private static final String EP_TWEETS_COUNT = "count";
    private static final String EP_TWEETS_COUNT_MAX = "100";

    //

    private final TwitterConfig config;

    private String bearerToken;

    public Twitter() throws IOException {
        this(TwitterConfig.fromFile());
    }
    
    public Twitter(TwitterConfig config) {
        this.config = config;

        bearerToken = null;
    }

    @Override
    public void authenticate() throws UnirestException, AuthenticationException {

        final HttpResponse<JsonNode> responce = Unirest.post(EP_OAUTH2_TOKEN)
                .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8.")
                .basicAuth(config.getConsumerKey(), config.getConsumerSecret())
                .field(EP_OAUTH2_TOKEN_TYPE, EP_OAUTH2_TOKEN_TYPE_V)
                .asJson();

        if (responce.getStatus() != 200) throw new AuthenticationException();

        bearerToken = responce.getBody().getObject().getString("access_token");

    }

    @Override
    public boolean isAuthenticated() {
        return bearerToken != null;
    }

    private JSONObject request(String endpoint, UnaryOperator<HttpRequest> f) throws UnirestException {

        if (bearerToken == null) throw new RuntimeException("authenticate() has not been called");

        final HttpRequest request = f.apply(Unirest.get(endpoint)
                .header("Authorization", "Bearer " + bearerToken));
        final HttpResponse<JsonNode> responce = request.asJson();

        if (responce.getStatus() != 200) throw new RuntimeException(responce.getStatusText());

        return responce.getBody().getArray().getJSONObject(0);
    }

    @Override
    public JSONObject requestTrends(int woeid) throws UnirestException {

        return request(EP_TRENDS, r -> r
                .queryString(EP_TREND_ID, woeid));
    }

    @Override
    public JSONObject requestTweets(String topic) throws UnirestException {

        return request(EP_TWEETS, r -> r
                .queryString(EP_TWEETS_Q, topic)
                .queryString(EP_TWEETS_LANG, EP_TWEETS_LANG_V)
                .queryString(EP_TWEETS_COUNT, EP_TWEETS_COUNT_MAX)
            );
    }

    @Override
    public JSONObject requestTweets(String topic, String maxId) throws UnirestException {
        if (maxId == null) return requestTweets(topic);

        return request(EP_TWEETS, r -> r
                .queryString(EP_TWEETS_Q, topic)
                .queryString(EP_TWEETS_MAXID, maxId)
                .queryString(EP_TWEETS_LANG, EP_TWEETS_LANG_V)
                .queryString(EP_TWEETS_COUNT, EP_TWEETS_COUNT_MAX)
            );
    }

}
