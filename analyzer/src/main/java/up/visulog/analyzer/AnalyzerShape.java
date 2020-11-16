package up.visulog.analyzer;

import java.util.Map;

public abstract class AnalyzerShape {

   public Map<String, Integer> resultsMap;

   public final String pluginName;
   public final String chartType;


   // Dont let a plugin be created without arguments
   private AnalyzerShape() {
      this.pluginName = "";
      this.chartType = "";
   }

   protected AnalyzerShape(String pluginName, ChartTypes chartTypes) {
      this.chartType = chartTypes.type;
      this.pluginName = pluginName;
   }
}
