
import apps.Web;

public class WebServiceHello {

    @Web("inicio")
    public static String square() {
        String html = "<!DOCTYPE html>\n"
                + "<html lang=\"en\">\n"
                + "\n"
                + "<head>\n"
                + "    <title>Proyecto-AREP</title>\n"
                + "    <meta charset=\"utf-8\">\n"
                + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">\n"
                + "    <meta name=\"description\" content=\"\">\n"
                + "    <meta name=\"author\" content=\"\">\n"
                + "</head>\n"
                + "\n"
                + "<body style=\"background-color: #a6e1ec\">\n"
                + "    <h5 class=\"masthead-heading mb-0\" align=\"center\" style=\"color: #0056b3; font-size: 3.2em\">Inicio</h5>\n"
                + "    <br>\n"
                + "    <label align=\"center\" style=\"color: black; size: 30px; font: Sans-serif;\">Archivos disponibles para solicitar:</label>\n"
                + "    <br>\n"
                + "    <label align=\"center\" style=\"color: black; size: 30px; font: Sans-serif;\">&#8226 imagenSistemas.jpg</label>\n"
                + "    <br>\n"
                + "    <label align=\"center\" style=\"color: black; size: 30px; font: Sans-serif;\">&#8226 pagina.html</label>\n"
                + "    <br>\n"
                + "</body>\n"
                + "\n"
                + "</html>";
        return html;
    }
    
    @Web("cuadrado")
    public static String square(String num) {
        int numI = Integer.parseInt(num);
        String html = "<!DOCTYPE html>\n"
                + "<html lang=\"en\">\n"
                + "\n"
                + "<head>\n"
                + "    <title>Proyecto-AREP</title>\n"
                + "    <meta charset=\"utf-8\">\n"
                + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">\n"
                + "    <meta name=\"description\" content=\"\">\n"
                + "    <meta name=\"author\" content=\"\">\n"
                + "</head>\n"
                + "\n"
                + "<body style=\"background-color: #a6e1ec\">\n"
                + "    <h5 align=\"cente\" class=\"masthead-heading mb-0\" style=\"color: #0056b3; padding:0; font-size: 3.2em\">Cuadrado</h5>"
                + "    <h4 align=\"center\" class=\"masthead-heading mb-0\" style=\"padding-left: 50px; font-size: 1.2em\">"+numI +"²  =  "+numI*numI+"</h5>"
                + "    <br>\n" 
                + "</body>\n"
                + "\n"
                + "</html>";
        return html;
    }
}
