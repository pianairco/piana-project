package ir.piana.dev.server.document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.piana.dev.server.asset.PianaAsset;
import ir.piana.dev.server.asset.PianaAssetResolver;
import ir.piana.dev.server.config.PianaRouterConfig;
import org.apache.log4j.Logger;

import javax.ws.rs.core.MediaType;

/**
 * Created by SYSTEM on 7/17/2017.
 */
public abstract class DocumentResolver {
    private static Logger logger = Logger.getLogger(
            DocumentResolver.class);
    private static String rootPath;
    private static PianaAssetResolver assetResolver;

    public static PianaAsset getPianaDocumentHtml(PianaRouterConfig routerConfig) {
        ObjectMapper objectMapper = new ObjectMapper();
        String routerJsonString = null;
        try {
            routerJsonString = objectMapper.writeValueAsString(routerConfig.getJsonNode());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<head>");
        sb.append("<meta charset=\"UTF-8\">");
        sb.append("<title>Piana Document</title>");
        sb.append("<script src=\"https://www.w3schools.com/appml/2.0.3/appml.js\"></script>");
        sb.append("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js\"></script>");
        sb.append("<script type=\"text/javascript\">" +
                "var jsonModel = " + routerJsonString + ";\n" +
                "console.log(jsonModel)" +
                "</script>");
        sb.append("<script type=\"text/javascript\" >\n" +
                "        console.log(\"hi\");\n" +
                "        var docUrl = \"\";\n" +
                "        var xhttp = new XMLHttpRequest();\n" +
                "        var docConf = null;\n" +
                "        xhttp.onreadystatechange = function() {\n" +
                "            if (this.readyState == 4 && this.status == 200) {\n" +
                "                docConf = JSON.parse(this.responseText);\n" +
                "                document.getElementById(\"description\").innerHTML = docConf.description;\n" +
                "            }\n" +
                "        };\n" +
//                "        xhttp.open(\"GET\",\"piana-json-doc=true\", true);\n" +
//                "        xhttp.send();\n" +
                "$.each(jsonModel, function (index, value) {\n" +
                        "    console.log(index + ' => ');" +
                        "$.each(value, function (index, value) {\n" +
                        "    console.log(index + ' => ' + value);" +
                        "});" +
                        "});" +
                "    </script>\n" +
                "    <div id=\"description\" ></div>"
        );
        sb.append("</head>");
        sb.append("<body>");
        sb.append("<h1>Piana Document</h1>");
        sb.append(" <appml security=\"\">");
        sb.append(" <filters>");
        sb.append(" <>");
        sb.append(" </filters>");
        sb.append(" </appml>");
        sb.append("</body>");
        sb.append("</html>");

        PianaAsset pianaAsset = new PianaAsset(sb.toString().getBytes(), null, null);
        pianaAsset.setMediaType(MediaType.TEXT_HTML);
        return pianaAsset;
    }
}
