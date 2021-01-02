package up.visulog.analyzer.plugin;
import up.visulog.analyzer.AnalyzerPlugin;
import up.visulog.analyzer.AnalyzerShape;
import up.visulog.analyzer.ChartTypes;
import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Author;
import up.visulog.gitrawdata.Branch;
import up.visulog.gitrawdata.Commit;

import java.util.*;

public class NumberOfContributors implements AnalyzerPlugin {

    private final Configuration configuration;

    private Result result;

    public NumberOfContributors(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }

    static Result processLog(List<Commit> gitLog) {
        var result = new NumberOfContributors.Result();

        gitLog.sort(Comparator.comparing((Commit c) -> c.date));


        List<Author> authors = new ArrayList<>();

        for (var commit : gitLog) {
            boolean anyMatch = false;

            for (Author author : authors) {
                if (author.mails.stream().anyMatch(commit.author::is)) {
                    anyMatch = true;
                }
            }

            if (!anyMatch) {
                authors.add(commit.author);
            }
        }

        result.resultsMap.put("result", authors.size());

        return result;
    }

    @Override
    public void run() {
        this.result = processLog(Objects.requireNonNull(Commit.parseAllFromBranch(configuration.branch, configuration.start, configuration.end, configuration.aliases, configuration.mailBlacklist, configuration.mailWhitelist, configuration.format)));
    }

    @Override
    public Result getResult() {
        if (result == null) run();
        return result;
    }

    static class Result extends AnalyzerShape implements AnalyzerPlugin.Result{

        Result() {
            super("Number of contributors", ChartTypes.POUBELLE);
        }

        @Override
        public String getResultAsString() { return this.resultsMap.toString(); }

        @Override
        public String getResultAsHtmlDiv() {
            return "<div>Number of contributors: " + this.resultsMap.entrySet().iterator().next().getValue() + "\n</div>";
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