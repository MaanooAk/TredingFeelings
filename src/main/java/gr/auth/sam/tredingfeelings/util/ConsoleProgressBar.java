
package gr.auth.sam.tredingfeelings.util;

public class ConsoleProgressBar extends ProgressBar {

    public ConsoleProgressBar(ProgressBar parent, String title, int max) {
        super(parent, title, max);
    }

    @Override
    public void show() {

        // TODO implement

        System.out.println(current + " / " + max); // placeholder
    }

    @Override
    public void close() {}

}
