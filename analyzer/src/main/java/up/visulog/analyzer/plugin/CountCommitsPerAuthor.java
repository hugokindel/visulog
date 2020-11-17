package up.visulog.analyzer.plugin;

import up.visulog.analyzer.AnalyzerPlugin;
import up.visulog.analyzer.AnalyzerShape;
import up.visulog.analyzer.ChartTypes;
import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

import java.util.List;
import java.util.Map;

/**
 * This is an analyzer's plugin which goal is to count the
 * number of commits per author in a given cloned repo.
 */
public class CountCommitsPerAuthor implements AnalyzerPlugin {
    /** The configuration to use. */
    private final Configuration configuration;

    /** The result obtained after the computation. */
    private Result result;


    /**
     * Class constructor.
     *
     * @param generalConfiguration The general configuration of the analyzer.
     */
    public CountCommitsPerAuthor(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }

    /**
     * A task which processes the logs to obtain the count of commits per author.
     *
     * @param gitLog A list of commits in the given repo (see gitrawdata)/
     * @return the result of the computation.
     */
    static Result processLog(List<Commit> gitLog) {
        var result = new Result();

        for (var commit : gitLog) {
            var nb = result.resultsMap.getOrDefault(commit.author, 0);
            result.resultsMap.put(commit.author, nb + 1);
        }

        return result;
    }

    /** Run this analyzer plugin. */
    @Override
    public void run() {
        result = processLog(Commit.parseLogFromCommand(configuration.getGitPath()));
    }

    /** @return the result of this analysis. Runs the analysis first if not already done. */
    @Override
    public Result getResult() {
        if (result == null) run();
        return result;
    }



    /** This is the result class for this plugin. */
    static class Result extends AnalyzerShape implements AnalyzerPlugin.Result {

        public Result() {
            super("Count commits per author", ChartTypes.BAR);
        }

        /** Get the hash map containing the count of commits per author. */
        public Map<String, Integer> getResults() {
            return this.resultsMap;
        }

        /**
         * @return the plugin name
         */
        @Override
        public String getPluginName() {
            return this.pluginName;
        }

        @Override
        public String getChartType() {
            return this.chartType.type;
        }

        /** @return the result of this analysis, as a string. */
        @Override
        public String getResultAsString() {
            return this.resultsMap.toString();
        }

        /** @return the result of this analysis, as an HTML div (which can be use to render an .html file). */
        @Override
        public String getResultAsHtmlDiv() {
            StringBuilder html = new StringBuilder("<div>Commits per author: \n<ul>\n");

            for (var item : this.resultsMap.entrySet()) {
                html.append("<li>").append(item.getKey()).append(": ").append(item.getValue()).append("</li>\n");
            }

            html.append("</ul>\n</div>\n");

            return html.toString();
        }


    }
}
