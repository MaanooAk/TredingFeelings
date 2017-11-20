
package gr.auth.sam.tredingfeelings.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;


/**
 * A container for the Consumer Key and Consumer Secret which can be loaded from
 * a properties file, used by {@link Twitter}.
 * <p>
 * Example file:
 * <pre>
 * consumerKey=[consumer key here]
 * consumerSecret=[consumer secret here]
 * <pre>
 * 
 * @see Twitter
 * 
 */
public class TwitterConfig {

    private static final String DEFAULT_FILENAME = "twitter.config";
    private static final String KEY_CONSUMERKEY = "consumerKey";
    private static final String KEY_CONSUMERSECRET = "consumerSecret";

    public static TwitterConfig fromFile() throws IOException {

        return fromFile(Paths.get(DEFAULT_FILENAME));
    }

    public static TwitterConfig fromFile(Path path) throws IOException {

        Properties p = new Properties();

        try {
            p.load(Files.newInputStream(path));
        } catch (NoSuchFileException e) {
            Files.createFile(path);
        }

        if (!p.containsKey(KEY_CONSUMERKEY) || p.getProperty(KEY_CONSUMERKEY).isEmpty()) {
            throw new IOException("Missing " + KEY_CONSUMERKEY + " in " + path.toAbsolutePath());
        }
        if (!p.containsKey(KEY_CONSUMERSECRET) || p.getProperty(KEY_CONSUMERSECRET).isEmpty()) {
            throw new IOException("Missing " + KEY_CONSUMERSECRET + " in " + path.toAbsolutePath());
        }

        return new TwitterConfig(p.getProperty(KEY_CONSUMERKEY), p.getProperty(KEY_CONSUMERSECRET));
    }

    @Deprecated
    public static TwitterConfig fromStrings(String consumerKey, String consumerSecret) {

        return new TwitterConfig(consumerKey, consumerSecret);
    }

    //

    private final String consumerKey;
    private final String consumerSecret;

    public TwitterConfig(String consumerKey, String consumerSecret) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

}
