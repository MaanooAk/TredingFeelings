package gr.auth.sam.tredingfeelings.serv.impl;

import java.awt.Font;
import java.io.IOException;
import java.util.List;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.internal.chartpart.Chart;

import gr.auth.sam.tredingfeelings.serv.IPlotter;

/**
 * Implementation of IPlotter using the XChart library.
 * <p>
 * Usage:
 * <pre>
 * IPlotter p = new XChartPlotter();
 * p.createBarChart(...);
 * p.show();
 * </pre>
 * 
 * @see IPlotter
 * 
 */
public class XChartPlotter implements IPlotter {

    protected Chart<?, ?> chart;

    public XChartPlotter() {
        this.chart = null;
    }

    @Override
    public void createSimpleChart(String title, int width, int height, String xname, List<String> xvalues, String yname,
            List<Integer> yvalues) {

        final CategoryChart chart = new CategoryChartBuilder()
                .width(width)
                .height(height)
                .title(title)
                .xAxisTitle(xname)
                .yAxisTitle(yname)
                .build();

        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setHasAnnotations(false);

        chart.addSeries("1", xvalues, yvalues);

        this.chart = chart;
    }
    
    @Override
    public void createBarChart(String title, int width, int height, String xname, List<String> xvalues, String yname,
            List<Integer> yvalues) {

        final CategoryChart chart = new CategoryChartBuilder()
                .width(width)
                .height(height)
                .title(title)
                .xAxisTitle(xname)
                .yAxisTitle(yname)
                .build();

        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setHasAnnotations(false);

        chart.getStyler().setAxisTickLabelsFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        chart.getStyler().setXAxisLabelRotation(60);

        chart.addSeries("1", xvalues, yvalues);

        this.chart = chart;
    }

    @Override
    public void createZipfChart(String title, int width, int height, String xname, List<Float> xvalues, String yname,
            List<Float> yvalues) {

        final XYChart chart = new XYChartBuilder()
                .width(width)
                .height(height)
                .title(title)
                .xAxisTitle(xname)
                .yAxisTitle(yname)
                .build();

        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setHasAnnotations(false);

        chart.getStyler().setAxisTickLabelsFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));

        chart.getStyler().setXAxisLogarithmic(true);
        chart.getStyler().setYAxisLogarithmic(true);

        chart.addSeries("1", xvalues, yvalues);

        this.chart = chart;
    }

    @Override
    public void createCumulativeChart(String title, int width, int height, String xname, List<Integer> xvalues,
            String yname, List<Float> yvalues) {

        // TODO? do we need this here
        for (int i1 = 1; i1 < yvalues.size(); i1++) {
            yvalues.set(i1, yvalues.get(i1) + yvalues.get(i1 - 1));
        }

        final XYChart chart = new XYChartBuilder()
                .width(width)
                .height(height)
                .title(title)
                .xAxisTitle(xname)
                .yAxisTitle(yname)
                .build();

        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setHasAnnotations(false);

        chart.getStyler().setAxisTickLabelsFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));

        chart.addSeries("1", xvalues, yvalues);

        this.chart = chart;
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
