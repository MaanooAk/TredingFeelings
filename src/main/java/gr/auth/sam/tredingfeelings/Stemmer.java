
package gr.auth.sam.tredingfeelings;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;


public class Stemmer {

    private static final String PATH_STOPWORDS = "stopwords_en.txt";

    private final ArrayList<String> stopwords;

    public Stemmer() {
        stopwords = new ArrayList<>();

        loadStopwords();
    }

    private void loadStopwords() {
        try {

            Path path = Paths.get(ClassLoader.getSystemResource(PATH_STOPWORDS).toURI());

            Files.lines(path).forEach(stopwords::add);

        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Stemmer: Failed to load stopwords");
        }

        System.out.println("Stemmer: Loaded " + stopwords.size() + " stopwords");
    }

    /**
     * TODO doc
     */
    public String stem(String text) {
        text = text.toLowerCase();
        text = removeLinks(text);
        text = clean(text);

        StringBuilder sb = new StringBuilder();

        for (String word : text.split(" ")) {
            if (!isStopWord(word) && word.length() > 1) sb.append(word).append(" ");
        }

        return sb.toString();
    }
    
    /**
     * Remove all no character characters and convert to lower case
     */
    public String clean(String text) {
        return text.replaceAll("[^a-z]+", " ").replaceAll("[ ]+", " ");
    }

    /**
     * TODO doc
     */
    public String removeLinks(String text) {
        return text.replaceAll("http[^ ]*", " ");
    }

    /**
     * TODO doc
     */
    private boolean isStopWord(String s) {
        return stopwords.contains(s);
    }
}
