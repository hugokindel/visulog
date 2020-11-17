package up.visulog.analyzer;

import java.util.HashMap;
import java.util.Map;

public abstract class AnalyzerShape {

   public Map<String, Integer> resultsMap = new HashMap<>();

   public final String pluginName;
   public final ChartTypes chartType;


   // Dont let a plugin be created without arguments
   private AnalyzerShape() {
      this.pluginName = "";
      this.chartType = ChartTypes.COLUMN;
   }

   protected AnalyzerShape(String pluginName, ChartTypes chartTypes) {
      this.chartType = chartTypes;
      this.pluginName = pluginName;
   }
}
