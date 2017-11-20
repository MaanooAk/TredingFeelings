package gr.auth.sam.tredingfeelings.impl;

import java.io.IOException;
import java.util.List;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.internal.chartpart.Chart;

import gr.auth.sam.tredingfeelings.IPlotter;

public class XChartPlotter implements IPlotter {

    protected Chart<?, ?> chart;

    public XChartPlotter() {
        this.chart = null;
    }

    @Override
    public void createBarChart(String title, int width, int height, String xname, List<String> xvalues, String yname,
            List<Integer> yvalues) {
        // TODO Implement
        
    }

    @Override
    public void createZipfChart(String title, int width, int height, String xname, List<Integer> xvalues, String yname,
            List<Integer> yvalues) {
        // TODO Implement
        
    }

    @Override
    public void createCumulativeChart(String title, int width, int height, String xname, List<Integer> xvalues,
            String yname, List<Integer> yvalues) {
        // TODO Implement
        
    }

    @Override
    public void showChart() {
        if (chart == null) throw new NullPointerException("no chart has been created");

        new SwingWrapper<Chart<?, ?>>(chart).displayChart();
    }

    @Override
    public void exportChart(String path) {
        if (chart == null) throw new NullPointerException("no chart has been created");

        try {
            BitmapEncoder.saveBitmap(chart, path, BitmapFormat.PNG);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
    
}
