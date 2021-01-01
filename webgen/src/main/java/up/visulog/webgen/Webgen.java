package up.visulog.webgen;

import htmlflow.HtmlView;
import htmlflow.StaticHtml;
import org.xmlet.htmlapifaster.EnumRelType;
import org.xmlet.htmlapifaster.Head;
import org.xmlet.htmlapifaster.Html;
import up.visulog.analyzer.AnalyzerPlugin;
import up.visulog.analyzer.AnalyzerResult;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
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
   public final List<String> pluginNames;

   public Webgen(AnalyzerResult result, List<String> pluginNames) {
      this.result = result;
      this.pluginNames = pluginNames;
   }

   /*
   Generates an html code containing the description of each plugin
   which will be printed both on the new html file but also on the command terminal
   */


   public void getFile(Path gitPath, boolean open, String[] cssToAdd) {
      try {
         Head<Html<HtmlView>> head = StaticHtml
                 .view()
                 .html().attrLang("fr")
                 .head()
                 .meta().attrCharset("UTF-8").__()
                 .link().attrRel(EnumRelType.STYLESHEET).attrHref("css/style.css").__();

         if (cssToAdd != null) {
            for (int i = 0; i < cssToAdd.length; i++) {
               head = head.link().attrRel(EnumRelType.STYLESHEET).attrHref(cssToAdd[i]).__();
            }
         }

         HtmlView view =
                 head
                         .title().text(this.pluginNames.toString()).__()
                         .script().attrSrc("js/canvasjs.min.js").__()
                         .__() //head
                         .body()
                         .header().attrClass("head")
                         .a().attrHref("https://gaufre.informatique.univ-paris-diderot.fr/hugokindel/visulog").img().attrSrc("css/git-hub.png").__().__()
                         .span().text("VISULOG").__()
                         .div().attrClass("phantomDiv").__()
                         .__() // header
                         .div().attrClass("results")
                         .div().attrClass("pluginTextual")
                         .text(result.getSubResults().stream().map(AnalyzerPlugin.Result::getResultAsHtmlDiv).reduce("", (acc, cur) -> acc + cur))
                         .__() // div.pluginTextual
                         .div().attrClass("pluginGraphical")
                         .text(createGraphsDivs())
                         .__() // div.pluginGraphical
                         .__() //div
                         .script()
                         .text("window.onload = function() {" + createScripts() + "}")
                         .__()// script
                         .__() //body
                         .__(); //html


         String html = view.render();
         String fileName = gitPath.toAbsolutePath().toString() + "/output/results-" + new SimpleDateFormat("yyyy_MM_dd-HH-mm").format(new Date()) + ".html";
         PrintWriter p = new PrintWriter(fileName);

         p.write(html);
         p.close();

         if (open) {
            Desktop.getDesktop().open(new File(fileName));
         }

      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public String createGraphsDivs() {
      StringBuilder res = new StringBuilder();
      int i = 0;
      for (AnalyzerPlugin.Result results : result.getSubResults()) {
         res.append("<div id=\"").append(results.getPluginName().replace(" ", "")).append(i).append("\"></div>\n");
         i++;
      }

      return res.toString();
   }

   public String createScripts() {
      StringBuilder res = new StringBuilder();
      int i = 0;
      for (AnalyzerPlugin.Result results : result.getSubResults()) {
         if(!results.getChartType().equals("")) {
            res.append("var chart")
                    .append(results.getPluginName().replace(" ", "")).append(i).append(" = new CanvasJS.Chart(\"")
                    .append(results.getPluginName().replace(" ", "")).append(i).append("\",")
                    .append("{           \n" +
                            "                  animationEnabled: true,\n" +
                            "                  theme: \"ligth2\",\n" +
                            "                  title: {\n" +
                            "                     text: \"").append(results.getPluginName()).append("\"")
                    .append("},\n" +
                            "               data: [{\n" +
                            "                  type: \"").append(results.getChartType()).append("\",")
                    .append("\n" +
                            "               dataPoints: [");
            for (var item : results.getResults().entrySet()) {
               res.append("{ y: ").append(item.getValue()).append(", label: \"").append(item.getKey()).append("\"},\n");
            }
            res.append("]\n}\n]\n});\n");
            res.append("chart").append(results.getPluginName().replace(" ", "")).append(i).append(".render();\n");
            i++;
         }
      }
      return res.toString();
   }

   public void printHTML() {
      System.out.println(result.toHTML());
   }
}