
package gr.auth.sam.tredingfeelings.util;

import java.util.Locale;

/**
 * This is a classical unix progress bar. The length of the bar will be 20 chars
 * between the [, ]
 * <p>
 * This object inherits the needed properties from the parent object.
 */
public class ConsoleProgressBar extends ProgressBar {
    private final int length = 30;
    private int prevStringMaxLen;

    public ConsoleProgressBar(ProgressBar parent, String title, int max) {
        super(parent, title, max);
        prevStringMaxLen = 0;
    }

    @Override
    public void show() {

        float progress = this.current / (float) this.max;

        StringBuilder bar = new StringBuilder();

        bar.append("[");
        for (int i = 0; i < Math.floor(progress * length); i++) {
            bar.append("=");
        }
        for (int i = (int) Math.floor(progress * length); i < length; i++) {
            bar.append(" ");
        }
        bar.append("] ");

        if (this.message != null) {
            bar.append(this.message);
            bar.append(" ");
        }

        String temp = String.format(Locale.ENGLISH, "%.2f", progress * 100) + "%";
        bar.append(temp);



        // insert rewrite chars
        for(int i = 0; i < prevStringMaxLen - bar.length(); i++)
            bar.append(" ");

        bar.append("\r");

        prevStringMaxLen = (bar.length()>prevStringMaxLen)?bar.length():prevStringMaxLen;

        System.out.print(bar.toString());
    }

    @Override
    public void close() {
    }

    @Override
    public void nextBar() {
        prevStringMaxLen = 0;
        System.out.println();
    }
}
