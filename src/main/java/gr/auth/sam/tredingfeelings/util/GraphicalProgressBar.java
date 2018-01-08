
package gr.auth.sam.tredingfeelings.util;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;


public class GraphicalProgressBar extends ProgressBar {

    private final JDialog dlg;
    private final JProgressBar dpb;
    private final JLabel labelProgress;
    private final JLabel labelMessage;

    public GraphicalProgressBar(String title, int max) {
        super(title, max);

        dpb = new JProgressBar(0, max);
        labelProgress = new JLabel("0%", SwingConstants.CENTER);
        labelMessage = new JLabel(" ");

        dlg = new JDialog();
        dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dlg.setTitle(title);
        dlg.setModal(true);
        dlg.add(BorderLayout.CENTER, dpb);
        dlg.add(BorderLayout.NORTH, labelProgress);
        dlg.add(BorderLayout.SOUTH, labelMessage);

        dlg.setSize(300, 75);
        dlg.setLocationRelativeTo(null); // center on screen

        new Thread(new Runnable() {

            public void run() {
                dlg.setVisible(true);
            }
        }).start();
    }

    @Override
    public void show() {

        dpb.setValue(current);
        labelProgress.setText(((current * 100) / max) + "%");
        labelMessage.setText(message + " ");

    }

    @Override
    public void close() {

        dlg.setVisible(false);
        dlg.dispose();
    }

}
