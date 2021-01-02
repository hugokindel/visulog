package up.visulog.analyzer.plugin;
import up.visulog.analyzer.AnalyzerPlugin;
import up.visulog.analyzer.AnalyzerShape;
import up.visulog.analyzer.ChartTypes;
import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Branch;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ListOfBranches implements AnalyzerPlugin {

    private final Configuration configuration;

    private Result result;

    public ListOfBranches(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }

    static Result processLog(List<Branch> gitLog) {
        var result = new Result();
        int i=0;
        for (var branch : gitLog) {
            if (!branch.name.equals("HEAD") && !branch.name.endsWith("/HEAD")) {
                result.resultsMap.put(branch.name.substring(20) + " --- ID: " + branch.id.substring(0, 8), i);
            }
            i++;
        }

        return result;
    }

    @Override
    public void run() {
        this.result = processLog(Objects.requireNonNull(Branch.parseAll(configuration.repo, true)));
    }

    @Override
    public Result getResult() {
        if (result == null) run();
        return result;
    }

    static class Result extends AnalyzerShape implements AnalyzerPlugin.Result{

        Result() {
            super("List of branches", ChartTypes.POUBELLE);
        }

        @Override
        public String getResultAsString() { return this.resultsMap.toString(); }

        @Override
        public String getResultAsHtmlDiv() {
            StringBuilder html = new StringBuilder("<div>List of branches (not ordered): \n<ul>\n");

            for (var item : this.resultsMap.entrySet())
                html.append("<li>").append(item.getKey()).append("</li>\n");

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