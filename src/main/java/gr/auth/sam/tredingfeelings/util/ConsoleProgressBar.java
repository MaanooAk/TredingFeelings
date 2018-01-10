
package gr.auth.sam.tredingfeelings.util;

import java.util.Locale;

/**
 * This is a classical unix progress bar. The length of the bar will be 20 chars
 * between the [, ]
 * <p>
 * This object inherits the needed properties from the parent object.
 */
public class ConsoleProgressBar extends ProgressBar {

    public ConsoleProgressBar(ProgressBar parent, String title, int max) {
        super(parent, title, max);
    }

    @Override
    public void show() {

        float progress = this.current / (float) this.max;

        StringBuilder bar = new StringBuilder();

        if (this.message != null) {

            bar.append(this.message);
            bar.append("\n");
        }

        bar.append("[");
        for (int i = 0; i < Math.floor(progress * 20); i++) {
            bar.append("=");
        }
        for (int i = (int) Math.floor(progress * 20); i < 20; i++) {
            bar.append(" ");
        }
        bar.append("]\t");

        String temp = String.format(Locale.ENGLISH, "%.2f", progress * 100);
        bar.append(temp);

        bar.append("%\r");

        System.out.println(bar.toString());
    }

    @Override
    public void close() {
    }

}
