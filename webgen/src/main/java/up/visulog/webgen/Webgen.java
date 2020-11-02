package up.visulog.webgen;

import up.visulog.analyzer.AnalyzerPlugin;
import up.visulog.analyzer.AnalyzerResult;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
Webgen is a HTML code generator in a file (yyyy_MM_dd-HH-mm.hmtl) send to visulog/output
 */
public class Webgen {

   /*
   Webgen retrieves the result of the plugins analyzer that it will then process
    */
   public final AnalyzerResult result;

   public Webgen(AnalyzerResult result) {
      this.result = result;
   }

   /*
   Generates an html code containing the description of each plugin
   which will be printed both on the new html file but also on the command terminal
   */
   public void generate() {
      this.getFile();
      this.printHTML();
   }


   public void getFile() {
      try {
         //Creates a new PrintWriter, without automatic line flushing, with the specified file name.
         PrintWriter p = new PrintWriter("output/results-" + new SimpleDateFormat("yyyy_MM_dd-HH-mm").format(new Date()) + ".html");
         // Writes a string in the file name on PrintWriter p
         p.write(HTMLEntities.DOCTYPE + HTMLEntities.HEAD);
         p.write("<body>\n" + HTMLEntities.HEADER+ "\n<div class=\"results\">\n");
         p.write("<div class=\"pluginTextual\">");
         p.write(result.getSubResults().stream().map(AnalyzerPlugin.Result::getResultAsHtmlDiv).reduce("", (acc, cur) -> acc + cur));
         p.write("\n</div>\n <div class=\"pluginGraphical\">");
         //todo: Add js librairy to creat the graph
         p.write("</div></div>\n</body>\n</html>");
         p.close();
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }
   }

   public void printHTML() {
      System.out.println(result.toHTML());
   }
}
