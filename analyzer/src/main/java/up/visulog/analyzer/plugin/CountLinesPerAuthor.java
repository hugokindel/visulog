package up.visulog.analyzer.plugin;

import up.visulog.analyzer.AnalyzerPlugin;
import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is an analyzer's plugin which goal is to count the
 * lines of commits per author in a given cloned repo.
 */
public class CountLinesPerAuthor implements AnalyzerPlugin {
    /** The configuration to use. */
    private final Configuration configuration;

    /** The result obtained after the computation. */
    private Result result;

    /**
     * Class constructor.
     *
     * @param generalConfiguration The general configuration of the analyzer.
     */
    public CountLinesPerAuthor(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }

    /**
     * A task which processes the logs to obtain the count of commits per author.
     *
     * @param gitLog A list of commits in the given repo (see gitrawdata)/
     * @return the result of the computation.
     */
    static Result processLog(List<Commit> gitLog) {
        return new Result();
    }

    /** Run this analyzer plugin. */
    @Override
    public void run() {

    }

    /** @return the result of this analysis. Runs the analysis first if not already done. */
    @Override
    public Result getResult() {
        if (result == null)
            run();

        return result;
    }

    /** This is the result class for this plugin. */
    static class Result implements AnalyzerPlugin.Result {
        /** @return the result of this analysis, as a string. */
        @Override
        public String getResultAsString() {
            return "";
        }

        /** @return the result of this analysis, as an HTML div (which can be use to render an .html file). */
        @Override
        public String getResultAsHtmlDiv() {
            return "";
        }
    }
}

