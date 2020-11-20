package up.visulog.analyzer.plugin;

import up.visulog.analyzer.AnalyzerPlugin;
import up.visulog.analyzer.AnalyzerShape;
import up.visulog.analyzer.ChartTypes;

import java.util.Map;

public class ProjectProgression implements AnalyzerPlugin {

    @Override
    public void run() {
    }

    @Override
    public AnalyzerPlugin.Result getResult() {
        return null;
    }

    static class Result extends AnalyzerShape implements AnalyzerPlugin.Result{


        Result() {
            super("ProjectProgression", ChartTypes.SPLINE_AREA);
        }

        @Override
        public String getResultAsString() {
            return null;
        }

        @Override
        public String getResultAsHtmlDiv() {
            return null;
        }

        @Override
        public Map<String, Integer> getResults() {
            return null;
        }

        @Override
        public String getPluginName() {
            return null;
        }

        @Override
        public String getChartType() {
            return null;
        }
    }

}
