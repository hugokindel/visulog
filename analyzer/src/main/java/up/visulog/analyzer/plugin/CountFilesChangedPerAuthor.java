package up.visulog.analyzer.plugin;

import up.visulog.analyzer.AnalyzerPlugin;
import up.visulog.analyzer.AnalyzerShape;
import up.visulog.analyzer.ChartTypes;
import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CountFilesChangedPerAuthor implements AnalyzerPlugin {
    private final Configuration configuration;

    private Result result;

    public CountFilesChangedPerAuthor(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }

    static Result processLog(List<Commit> gitLog) {
        var result = new Result();

        for (var commit : gitLog) {
            var nb = result.resultsMap.getOrDefault(commit.author, 0);
            result.resultsMap.put(commit.author, nb + commit.numberOfFilesChanged);
        }

        return result;
    }

    @Override
    public void run() {
        result = processLog(Objects.requireNonNull(Commit.parseAllFromRepository(configuration.getGitPath())));
    }

    @Override
    public Result getResult() {
        if (result == null) run();
        return result;
    }

    static class Result extends AnalyzerShape implements AnalyzerPlugin.Result {

        public Result() {
            super("Count files changed per author", ChartTypes.COLUMN);
        }

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

        @Override
        public String getResultAsString() {
            return this.resultsMap.toString();
        }

        @Override
        public String getResultAsHtmlDiv() {
            StringBuilder html = new StringBuilder("<div>Files changed per author: \n<ul>\n");

            for (var item : this.resultsMap.entrySet()) {
                html.append("<li>").append(item.getKey()).append(": ").append(item.getValue()).append("</li>\n");
            }

            html.append("</ul>\n</div>\n");

            return html.toString();
        }
    }
}