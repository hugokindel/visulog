package up.visulog.webgen;

import up.visulog.analyzer.AnalyzerPlugin;
import up.visulog.analyzer.AnalyzerResult;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Webgen {

   public final AnalyzerResult result;

   public Webgen(AnalyzerResult result) {
      this.result = result;
   }

   public void generate() {
      this.getFile();
      this.printHTML();
   }

   public void getFile() {
      try {
         PrintWriter p = new PrintWriter("output/results-" + new SimpleDateFormat("yyyy_MM_dd-HH-mm").format(new Date()) + ".html");
         p.write(HTMLEntities.DOCTYPE + HTMLEntities.HEAD);
         p.write("<body>\n" + HTMLEntities.HEADER+ "\n<div class=\"results\">\n");
         p.write("<div class=\"pluginTextual\">");
         p.write(result.getSubResults().stream().map(AnalyzerPlugin.Result::getResultAsHtmlDiv).reduce("", (acc, cur) -> acc + cur));
         p.write("\n</div>\n <div class=\"pluginGraphical\">\n");
         int i = 0;
         List<String> script = new ArrayList<>();
         for (AnalyzerPlugin.Result results : result.getSubResults()) {
            p.write("<div id=\""+results.getPluginName().replace(" ", "") + i + "\">\n");
            script.add("\nvar chart" + results.getPluginName().replace(" ", "") + i + " = new CanvasJS.Chart(\"" + results.getPluginName().replace(" ", "") + i + "\", {\n" +
                  "        animationEnabled: true,\n" +
                  "        theme: \"dark2\"," +
                  "        title:{\n" +
                  "            text: \"" + results.getPluginName() + "\"\n" +
                  "        },\n" +
                  "        axisY: {\n" +
                  "            title: \"Number\"\n" +
                  "        },\n" +
                  "        data: [{\ntype: \"column\",\ndataPoints: [\n");
            for (var item : results.getResults().entrySet()) {
               script.add("{ y: "+item.getValue()+", label: \""+item.getKey().substring(0, item.getKey().indexOf("<"))+"\" },\n");
            }
            script.add("]\n}]\n" +
                  "    });\n" +
                  "    chart"+results.getPluginName().replace(" ", "")+i+".render();\n");
            i++;
            p.write("</div>");
         }

         p.write("<script>window.onload = function() {");
         for (String s : script) {
            p.write(s);
         }
         p.write("}</script>");
         p.write("</div>\n</div>\n");
         p.write("\n</body>\n</html>");
         p.close();
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }
   }

   public void printHTML() {
      System.out.println(result.toHTML());
   }
}
