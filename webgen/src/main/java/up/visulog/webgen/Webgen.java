package up.visulog.webgen;

import htmlflow.HtmlView;
import htmlflow.StaticHtml;
import up.visulog.analyzer.AnalyzerResult;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/*
Webgen is a HTML code generator in a file (yyyy_MM_dd-HH-mm.hmtl) send to visulog/output
 */
public class Webgen {

   /*
   Webgen retrieves the result of the plugins analyzer that it will then process
    */
   public final AnalyzerResult result;

   public Webgen(AnalyzerResult result, List<String> pluginNames) {
      this.result = result;
      new HTMLEntities(pluginNames);
   }

   /*
   Generates an html code containing the description of each plugin
   which will be printed both on the new html file but also on the command terminal
   */


   public void getFile() {

      try {
         // TODO : implement HTMLFLow
         HtmlView view = StaticHtml
               .view()
               .html()
                  .head()
                     .title().text("HtmlFlow").__()
                  .__() //head
                  .body()
                     .div().attrClass("container")
                        .span().text("My first page with HtmlFlow").__()
                        .img().attrSrc("http://bit.ly/2MoHwrU").__()
                     .p().text("Typesafe is awesome! :-)").__()
                  .__() //div
                  .__() //body
               .__(); //html


         String html = view.render();        // 1) get a string with the HTML
         PrintWriter p = new PrintWriter("output/results-" + new SimpleDateFormat("yyyy_MM_dd-HH-mm").format(new Date()) + ".html");

         p.write(html);
         p.close();


         // 3) write to details.html file

      } catch (IOException e) {
         e.printStackTrace();
      }



     /*
      try {


         //Creates a new PrintWriter, without automatic line flushing, with the specified file name.
         PrintWriter p = new PrintWriter("output/results-" + new SimpleDateFormat("yyyy_MM_dd-HH-mm").format(new Date()) + ".html");
         // Writes a string in the file name on PrintWriter p
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
                  "        theme: \"light2\"," +
                  "        title:{\n" +
                  "            text: \"" + results.getPluginName() + "\"\n" +
                  "        },\n" +
                  "        data: [{\ntype: \""+results.getChartType()+"\",\ndataPoints: [\n");
            for (var item : results.getResults().entrySet()) {
               script.add("{ y: "+item.getValue()+", label: \""+item.getKey()+"\" },\n");
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
      */

   }

   public void printHTML() {
      System.out.println(result.toHTML());
   }
}
