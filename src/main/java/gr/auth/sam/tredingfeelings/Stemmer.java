package gr.auth.sam.tredingfeelings;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;


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
    public String stem(String text) {
        text = text.toLowerCase();
        text = removeLinks(text);
        text = clean(text);
        
        StringBuilder sb = new StringBuilder();
        
        for (String word : text.split(" ")) {
            if (!isStopWord(word)) sb.append(word).append(" ");
        }
        
        return sb.toString();
    }
    
    public ArrayList<String> normalize(String text) {

        // TODO remove non character

        ArrayList<String> words = new ArrayList<>(Arrays.asList(text.split(" ")));
        ArrayList<String> finalWords = new ArrayList<>();

        for (int i = 0; i < words.size(); i++) {
            String temp = words.get(i).toLowerCase();
            if (isValid(temp)) {
                char[] chars = temp.toCharArray();
                if (chars.length > 1) {
                    if (!Character.isLetter(chars[chars.length - 1]))
                        finalWords.add(removeLastChar(temp));
                    else
                        finalWords.add(temp);
                }
            } else {
                words.remove(i);
                i--;
            }
        }

        return finalWords;
    }

    private boolean isValid(String s) {
        if (!isWord(s)) return false;
        return !isStopWord(s);
    }

    private boolean isStopWord(String s) {
        return stopwords.contains(s);
    }

    // TODO revisit
    private boolean isWord(String word) {
        char[] chars = word.toCharArray();

        if (chars.length == 1)
            return false;

        for (int i = 0; i < chars.length - 1; i++) {
            if (!Character.isLetter(chars[i])) return false;
        }

        return true;
    }

    private String removeLastChar(String word) {
        char[] chars = word.toCharArray();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < chars.length - 1; i++)
            builder.append(chars[i]);

        return builder.toString();
    }
}
