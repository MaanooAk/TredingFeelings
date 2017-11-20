
package gr.auth.sam.tredingfeelings;

import java.util.List;

/**
 * Creates basic plots that can be shown with swing or be exported into a PNG
 * images.
 * 
 */
public interface IPlotter {

    void createBarChart(String title, int width, int height, //
            String xname, List<String> xvalues, String yname, List<Integer> yvalues);

    void createZipfChart(String title, int width, int height, //
            String xname, List<Integer> xvalues, String yname, List<Integer> yvalues);

    void createCumulativeChart(String title, int width, int height, //
            String xname, List<Integer> xvalues, String yname, List<Integer> yvalues);

    /**
     * Show the last created chart.
     */
    void showChart();

    /**
     * Export the last created chart into a PNG image.
     * 
     * @param path the path of the exported image
     */
    void exportChart(String path);
    
}
