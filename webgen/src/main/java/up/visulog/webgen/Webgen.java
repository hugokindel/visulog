package up.visulog.webgen;

import up.visulog.analyzer.AnalyzerPlugin;
import up.visulog.analyzer.AnalyzerResult;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

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
         p.write("<body>\n<div class=\"results\">\n");
         p.write(result.getSubResults().stream().map(AnalyzerPlugin.Result::getResultAsHtmlDiv).reduce("", (acc, cur) -> acc + cur));
         p.write("</div>\n</body>\n</html>");
         p.close();
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }
   }

   public void printHTML() {
      System.out.println(result.toHTML());
   }
}
