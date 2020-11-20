package up.visulog.analyzer.plugin;

import up.visulog.analyzer.AnalyzerPlugin;
import up.visulog.analyzer.AnalyzerShape;
import up.visulog.analyzer.ChartTypes;
import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

import java.util.List;
import java.util.Map;

public class ProjectProgression implements AnalyzerPlugin {

    private final Configuration configuration;

    private Result result;

    public ProjectProgression(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }
    static Result processLog(List<Commit> gitLog) {
        var result = new Result();


        for (var commit : gitLog) {

            var nb = result.resultsMap.getOrDefault(commit.date.toString(), 0);
            result.resultsMap.put(commit.date.toString(), nb + 1);
        }



        return result;
    }

    @Override
    public void run() {
        this.result = processLog(Commit.parseLogFromCommand(configuration.getGitPath()));
    }

    @Override
    public Result getResult() {
        if (result == null) run();
        return result;
    }
    static class Result extends AnalyzerShape implements AnalyzerPlugin.Result{


        Result() {
            super("ProjectProgression", ChartTypes.SPLINE_AREA);
        }

        @Override
        public String getResultAsString() { return this.resultsMap.toString(); }

        @Override
        public String getResultAsHtmlDiv() {
            return null;
        }

        @Override
        public Map<String, Integer> getResults() {
            return this.resultsMap;
        }

        @Override
        public String getPluginName() {
            return this.pluginName;
        }

        @Override
        public String getChartType() {
            return this.chartType.type;
        }


    }

}
