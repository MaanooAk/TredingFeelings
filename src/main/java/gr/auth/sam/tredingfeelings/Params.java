
package gr.auth.sam.tredingfeelings;

public class Params {

    public int woeid = 23424977; // United States
    public int topicsCount = 5; // the top 5 trends
    public int tweetsCount = 1500; // 1500 tweets for each topic

    public boolean multithreaded = false;
    
    //

    public boolean clear = true;
    public boolean gather = true;
    public boolean proc = true;
    public boolean graph = true;

    //

    public Params(String[] args) {
        // TODO implement dynamic handling
    }

}
