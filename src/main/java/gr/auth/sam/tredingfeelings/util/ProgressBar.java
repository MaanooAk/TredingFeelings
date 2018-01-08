
package gr.auth.sam.tredingfeelings.util;

public abstract class ProgressBar {

    public static ProgressBar create(String title, int max) {

        return new ConsoleProgressBar(title, max);
    }

    protected String title;
    protected int max;

    protected int current;

    public ProgressBar(String title, int max) {
        this.title = title;
        this.max = max;
    }

    public final void setAndShow(int current) {
        this.current = current;
        show();
    }

    public final void incAndShow() {
        current += 1;
        show();
    }

    /**
     * Show the progress based on the current and max fields
     */
    public abstract void show();

}
