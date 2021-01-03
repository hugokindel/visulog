package up.visulog.analyzer.plugin;

import up.visulog.analyzer.AnalyzerPlugin;
import up.visulog.analyzer.AnalyzerShape;
import up.visulog.analyzer.ChartTypes;
import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * This plugin allow us to count commits per mail. It's a little bit different
 * of the CountCommitsPerAuthor plugin since this one count the mail in the commit and not the author.
 * It's a bit more accurate if a user did change his global git name during the project.
 * It can be also a bit more comfortable to read.
 */
public class CountCommitsPerMail implements AnalyzerPlugin {

   /** The configuration to use. */
   private final Configuration configuration;

   /** The results obtained after computation. */
   private Result result;


   /**
    * Class constructor.
    *
    * @param generalConfiguration   The general configaration to use.
    */
   public CountCommitsPerMail(Configuration generalConfiguration) {
      this.configuration = generalConfiguration;
   }

   static Result processLog(List<Commit> gitLog) {
      var result = new Result();

      for (var commit : gitLog) {
         var nb = result.resultsMap.getOrDefault(commit.author.getPrimaryMail(), 0);
         result.resultsMap.put(commit.author.getPrimaryMail(), nb + 1);
      }

      return result;
   }

   @Override
   public void run() {
      this.result = processLog(Objects.requireNonNull(Commit.parseAllFromBranch(configuration.branch, configuration.start, configuration.end, configuration.aliases, configuration.mailBlacklist, configuration.mailWhitelist, configuration.format)));
   }

   /** */
   @Override
   public Result getResult() {
      if (result == null) run();
      return result;
   }

   /** This is the result class for this plugin. */
   static class Result extends AnalyzerShape implements AnalyzerPlugin.Result {


      protected Result() {
         super("Count commits per mail", ChartTypes.BAR);
      }

      /** @return the result of the analysis as a string */
      @Override
      public String getResultAsString() {
         return this.resultsMap.toString();
      }

      /** @return the result of the analysis but as an html div. */
      @Override
      public String getResultAsHtmlDiv() {
         StringBuilder html = new StringBuilder("<div>Commits per mail : \n<ul>\n");

         for (var item : this.resultsMap.entrySet())
            html.append("<li>").append(item.getKey()).append(": ").append(item.getValue()).append("</li>\n");

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
