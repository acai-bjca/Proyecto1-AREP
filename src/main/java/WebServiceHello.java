
import apps.Web;

public class WebServiceHello {

    @Web("cuadrado")
    public static String square() {
        String html = "<!DOCTYPE html>\n"
                + "<html>\n"
                + "  <head>\n"
                + "    <meta charset=\"utf-8\">\n"
                + "    <title>Inicio</title>\n"
                + "  </head>\n"
                + "  <body>\n"
                + "  </body>\n"
                + "</html>";
        return html;
    }
}
