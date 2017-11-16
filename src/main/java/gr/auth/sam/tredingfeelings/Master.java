
package gr.auth.sam.tredingfeelings;


/*
 * TODO doc
 */
public class Master {

    private final ITwitter twitter;
    private final IStorage storage;

    public Master(ITwitter twitter, IStorage storage) {
        this.twitter = twitter;
        this.storage = storage;
    }

}
