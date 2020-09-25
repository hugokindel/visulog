package up.visulog.analyzer;

import java.util.List;

/** This is a wrapper for various plugin's results. */
public class AnalyzerResult {
    /** The list of each plugin's result individually. */
    private final List<AnalyzerPlugin.Result> subResults;

    /** @return a list of the result of each plugin's individually. */
    public List<AnalyzerPlugin.Result> getSubResults() {
        return subResults;
    }

    /**
    * Class constructor.
    *
    * @param subResults The list of results to keep.
    */
    public AnalyzerResult(List<AnalyzerPlugin.Result> subResults) {
        this.subResults = subResults;
    }

    /** Returns a string representation of the object. */
    @Override
    public String toString() {
        return subResults.stream().map(AnalyzerPlugin.Result::getResultAsString).reduce("", (acc, cur) -> acc + "\n" + cur);
    }

    /** Returns an HTML div representation of the object. */
    public String toHTML() {
        return "<html><body>" + subResults.stream().map(AnalyzerPlugin.Result::getResultAsHtmlDiv).reduce("", (acc, cur) -> acc + cur) + "</body></html>";
    }
}
