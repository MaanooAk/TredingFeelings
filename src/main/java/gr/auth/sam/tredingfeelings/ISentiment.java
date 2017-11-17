package gr.auth.sam.tredingfeelings;

import org.json.JSONObject;

import com.mashape.unirest.http.exceptions.UnirestException;

public interface ISentiment {

    JSONObject analyze(String text) throws UnirestException;

}
