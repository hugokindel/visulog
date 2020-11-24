package up.visulog.analyzer;

public enum ChartTypes {
   COLUMN("column"),
   LINE("line"),
   BAR("bar"),
   AREA("area"),
   PIE("pie"),
   DOUGHNUT("doughnut"),
   SPLINE_AREA("splineArea");


   public String type;

   ChartTypes(String type) {
      this.type = type;
   }
}
