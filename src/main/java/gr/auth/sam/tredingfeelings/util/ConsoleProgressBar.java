
package gr.auth.sam.tredingfeelings.util;

public class ConsoleProgressBar extends ProgressBar {

    public ConsoleProgressBar(String title, int max) {
        super(title, max);
    }

    @Override
    public void show() {

        // TODO implement

        System.out.println(current + " / " + max); // placeholder
    }

}
