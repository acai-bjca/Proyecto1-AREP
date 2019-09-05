
import apps.Web;

public class WebServiceHello {

    @Web("inicio")
    public static String square() {
        String html = "<!DOCTYPE html>\n"
                + "<html>\n"
                + "  <head>\n"
                + "    <meta charset=\"utf-8\">\n"
                + "    <title>Inicio</title>\n"
                + "  </head>\n"
                + "  <body>\n"
                + "  <h3>HOLAAAAAAAA</h3>"
                + "  </body>\n"
                + "</html>";
        return html;
    }
    
    @Web("cuadrado2")
    public static String square(int num) {
        String html = "<!DOCTYPE html>\n"
                + "<html>\n"
                + "  <head>\n"
                + "    <meta charset=\"utf-8\">\n"
                + "    <title>Inicio</title>\n"
                + "  </head>\n"
                + "  <body>\n"
                + "  <h3>El cuadrado es: </h3>"
                + "  <h2>"+num*num+"</h2>"
                + "  </body>\n"
                + "</html>";
        return html;
    }
}
