
package gr.auth.sam.tredingfeelings.impl;

import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import gr.auth.sam.tredingfeelings.ISentiment;


/**
 * TODO doc
 */
public class Sentiment implements ISentiment {

    private static final String EP = "http://text-processing.com/api/sentiment/";
    private static final String EP_TEXT = "text";

    public Sentiment() {

    }

    @Override
    public JSONObject analyze(String text) throws UnirestException {

        final HttpResponse<JsonNode> responce = Unirest.post(EP)
                .field(EP_TEXT, text)
                .asJson();

        if (responce.getStatus() != 200) throw new RuntimeException(responce.getStatusText());

        return responce.getBody().getObject();
    }

}
