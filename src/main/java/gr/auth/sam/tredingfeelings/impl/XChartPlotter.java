package gr.auth.sam.tredingfeelings.impl;

import java.io.IOException;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.internal.chartpart.Chart;

public class XChartPlotter {

    protected Chart<?, ?> chart;

    public XChartPlotter() {
        this.chart = null;
    }
    
    public void showChart() {
        if (chart == null) throw new NullPointerException("no chart has been created");

        new SwingWrapper<Chart<?, ?>>(chart).displayChart();
    }

    public void exportChart(String path) {
        if (chart == null) throw new NullPointerException("no chart has been created");

        try {
            BitmapEncoder.saveBitmap(chart, path, BitmapFormat.PNG);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
    
}
