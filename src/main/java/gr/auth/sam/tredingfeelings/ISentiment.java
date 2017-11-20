package gr.auth.sam.tredingfeelings;

import org.json.JSONObject;

import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * Analyzes text in order to predict the sentiment of the writer.
 * <p>
 * The returned result depends on the implementation.
 * 
 */
public interface ISentiment {

    JSONObject analyze(String text) throws UnirestException;

}
