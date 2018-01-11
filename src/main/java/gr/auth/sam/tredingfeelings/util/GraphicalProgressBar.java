
package gr.auth.sam.tredingfeelings.util;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;


public class GraphicalProgressBar extends ProgressBar {

    private final JDialog dlg;
    private final JProgressBar dpb;
    private final JLabel labelProgress;
    private final JLabel labelMessage;

    public GraphicalProgressBar(ProgressBar parent, String title, int max) {
        super(parent, title, max);

        dpb = new JProgressBar(0, max);

        labelProgress = new JLabel("0%", SwingConstants.CENTER);
        labelProgress.setBorder(new EmptyBorder(0, 0, 5, 0));
        labelMessage = new JLabel(" ");
        labelMessage.setBorder(new EmptyBorder(5, 0, 0, 0));

        dlg = new JDialog();
        dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dlg.setTitle(title);
        dlg.setModal(true);

        dlg.getRootPane().setBorder(new EmptyBorder(5, 10, 5, 10));
        dlg.add(BorderLayout.CENTER, dpb);
        dlg.add(BorderLayout.NORTH, labelProgress);
        dlg.add(BorderLayout.SOUTH, labelMessage);

        dlg.setSize(300, 95);
        dlg.setLocationRelativeTo(null); // center on screen

        if (parent instanceof GraphicalProgressBar) {
            GraphicalProgressBar gparent = (GraphicalProgressBar) parent;

            dlg.setLocation(dlg.getX(), gparent.dlg.getY() + 110);
        }

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

    @Override
    public void nextBar() {
        // do nothing
    }
}
