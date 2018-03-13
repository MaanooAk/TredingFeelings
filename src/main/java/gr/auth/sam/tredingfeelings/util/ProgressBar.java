
package gr.auth.sam.tredingfeelings.util;

public abstract class ProgressBar {

    public static ProgressBar create(String title, int max) {
        return create(null, title, max);
    }

    public static ProgressBar create(ProgressBar parent, String title, int max) {

        try {
            return new GraphicalProgressBar(parent, title, max);
        } catch (Exception e) {}

        return new ConsoleProgressBar(parent, title, max);
    }

    protected ProgressBar parent;

    protected String title;
    protected int max;

    protected int current;
    protected String message;

    public ProgressBar(ProgressBar parent, String title, int max) {
        this.parent = parent;
        this.title = title;
        this.max = max;

        current = 0;
        message = "";
    }

    public final void setAndShow(int current) {
        this.current = current;
        show();
    }

    public final void setAndShow(int current, String message) {
        setMessage(message);
        setAndShow(current);
    }

    public final void incAndShow() {
        current += 1;
        show();
    }

    public final void incAndShow(String message) {
        setMessage(message);
        incAndShow();
    }

    public final void setMessage(String message) {
        this.message = message;
    }

    /**
     * Show the progress based on the current, max message fields
     */
    public abstract void show();

    public abstract void nextBar();

    public abstract void close();

}
