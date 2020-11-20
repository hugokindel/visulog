package up.visulog.analyzer.plugin;

import up.visulog.analyzer.AnalyzerPlugin;
import up.visulog.analyzer.AnalyzerShape;
import up.visulog.analyzer.ChartTypes;
import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

import javax.xml.crypto.Data;
import java.util.*;

public class ProjectProgression implements AnalyzerPlugin {

    private final Configuration configuration;

    private Result result;

    public ProjectProgression(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }


    static Result processLog(List<Commit> gitLog) {
        var result = new Result();

        gitLog.sort(Comparator.comparing((Commit c) -> c.date));

        int i = 0;
        for (var commit : gitLog) {
            result.resultsMap.put(commit.date.toString().substring(0, 10), i + 1);
            i++;
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