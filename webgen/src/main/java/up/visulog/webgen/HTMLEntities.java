package up.visulog.webgen;

import java.util.List;

public class HTMLEntities {


   public final static String DOCTYPE = "<!DOCTYPE html>\n" +
           "<html lang=\"en\">\n";

   public static String HEAD = "<head>\n" +
         "    <meta charset=\"UTF-8\">\n" +
         "    <link rel=\"stylesheet\" href=\"css/style.css\">" +
         "    <script src=\"js/canvasjs.min.js\"></script>" +
         "    <title>";


   public final static String HEADER = "<header>\n" +
           "    <div class=\"head\">\n" +
           "        <a href=\"https://gaufre.informatique.univ-paris-diderot.fr/hugokindel/visulog\" target=\"_blank\"><img src=\"css/git-hub.png\"></a>\n" +
           "        <span>Visulog</span>\n" +
           "        <div class=\"headerFantomDiv\"></div>\n" +
           "    </div>\n" +
           "</header>";

   public HTMLEntities(List<String> plugins) {
      HEAD += plugins;
      HEAD += "</title>\n" +
              "         </head>\n";
   }


}