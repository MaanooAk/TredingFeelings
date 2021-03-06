
package gr.auth.sam.tredingfeelings.serv;

import java.util.List;


/**
 * Creates basic plots that can be shown with swing or be exported into a PNG
 * images.
 * 
 */
public interface IPlotter {

    void createSimpleChart(String title, int width, int height, //
            String xname, List<String> xvalues, String yname, List<Integer> yvalues);
    
    void createBarChart(String title, int width, int height, //
            String xname, List<String> xvalues, String yname, List<Integer> yvalues);

    void createZipfChart(String title, int width, int height, //
            String xname, List<Float> xvalues, String yname, List<Float> yvalues);

    void createCumulativeChart(String title, int width, int height, //
            String xname, List<Integer> xvalues, String yname, List<Float> yvalues);

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
