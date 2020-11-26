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

        for (var commit : gitLog) {
            var nb = result.resultsMap.getOrDefault(commit.date, 0);
            result.resultsMap.put(commit.date, nb + 1);
        }

        return result;
    }

    @Override
    public void run() {
        this.result = processLog(Commit.parseAllFromRepository(configuration.getGitPath()));
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
            StringBuilder html = new StringBuilder("<div>ProjectProgression : \n <ul>\n");
            int a = 0;
            int max = -1;
            String maxDate = null;
            String s="";
            for (var item : this.resultsMap.entrySet()) {
                a = a + item.getValue();
                if (item.getValue() > max) {
                    max = item.getValue();
                    maxDate = item.getKey();
                }
                s= item.getKey();
            }
            html.append("<li> Date of last modification: ").append(s).append("</li>\n");
            html.append("<li> Number of all commits: ").append(a).append("</li>\n");
            html.append("<li> Date when there were the most commits: ").append(maxDate).append("</li>\n");
            html.append("</ul>\n</div>\n");

            return html.toString();
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