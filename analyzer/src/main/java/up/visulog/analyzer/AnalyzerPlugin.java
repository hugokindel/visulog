package up.visulog.analyzer;

import java.util.Map;

/** This is the interface to use for every analyzer's plugin. */
public interface AnalyzerPlugin {



	/** This is the interface to use for every plugin's result. */
    interface Result {

       /** @return the result of this analysis, as a string. */
        String getResultAsString();
        
        /** @return the result of this analysis, as an HTML div (which can be use to render an .html file). */
        String getResultAsHtmlDiv();

        Map<String, Integer> getResults();

        String getPluginName();

        String getChartType();
    }

    /** Run this analyzer plugin. */
    void run();

    /** @return the result of this analysis. Runs the analysis first if not already done. */
    Result getResult();
}
