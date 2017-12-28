
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

    public ArrayList<String> normalize(String text) {
        ArrayList<String> words = new ArrayList<>(Arrays.asList(text.split(" ")));
        ArrayList<String> finalWords = new ArrayList<>();

        for (int i = 0; i < words.size(); i++) {
            String temp = words.get(i).toLowerCase();
            if (isValid(temp)) {
                finalWords.add(temp);
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

    private boolean isWord(String word) {
        char[] chars = word.toCharArray();

        for (char c : chars) {
            if (!Character.isLetter(c)) {
                return false;
            }
        }

        return true;
    }
}
