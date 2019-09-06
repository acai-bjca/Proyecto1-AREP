
import apps.Web;

public class WebServiceHello {

    @Web("inicio")
    public static String square() {
        String html = "<html lang=\"en\">\n"
                + "\n"
                + "<head>\n"
                + "    <title>Proyecto-AREP</title>\n"
                + "    <meta charset=\"utf-8\">\n"
                + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">\n"
                + "    <meta name=\"description\" content=\"\">\n"
                + "    <meta name=\"author\" content=\"\">\n"
                + "\n"
                + "    <!-- Bootstrap core CSS -->\n"
                + "    <link href=\"../resources/static/bootstrap/css/bootstrap.min.css\" rel=\"stylesheet\">\n"
                + "\n"
                + "    <!-- Custom fonts for this template -->\n"
                + "    <link href=\"https://fonts.googleapis.com/css?family=Catamaran:100,200,300,400,500,600,700,800,900\" rel=\"stylesheet\">\n"
                + "    <!-- Custom styles for this template -->\n"
                + "    <link href=\"../resources/static/css/one-page-wonder.min.css\" rel=\"stylesheet\">\n"
                + "</head>\n"
                + "\n"
                + "<body style=\"background-image: url('../../resources/imagenes/fondo.jpg'); background-position: center center; background-repeat: no-repeat; background-attachment: fixed; background-size: cover;\">\n"
                + "    <form action=\"/calculations\" method=\"GET\">\n"
                + "        <!-- Navigation -->\n"
                + "        <nav class=\"navbar navbar-expand-lg navbar-dark navbar-custom fixed-top\">\n"
                + "            <div class=\"container\">\n"
                + "                <a class=\"navbar-brand\" href=\"#\">AREP-Proyecto1</a>\n"
                + "                <a class=\"navbar-brand\" href=\"#\">Amalia Alfonso</a>\n"
                + "                <button class=\"navbar-toggler\" type=\"button\" data-toggle=\"collapse\" data-target=\"#navbarResponsive\" aria-controls=\"navbarResponsive\" aria-expanded=\"false\" aria-label=\"Toggle navigation\">\n"
                + "                    </button>\n"
                + "            </div>\n"
                + "        </nav>\n"
                + "\n"
                + "        <header class=\"masthead text-center text-white\">\n"
                + "            <div class=\"container\">\n"
                + "                <h5 class=\"masthead-heading mb-0\" style=\"color: indigo; font-size: 3.2em\">Inicio</h5>\n"
                + "                <br>\n"
                + "                <label style=\"color: black; size: 30px; font: Sans-serif;\">Archivos disponibles para solicitar:</label>\n"
                + "                <br>\n"
                + "                <label style=\"color: black; size: 30px; font: Sans-serif;\">&#8226 imagenSistemas.jpg</label>\n"
                + "                <br>\n"
                + "                <label style=\"color: black; size: 30px; font: Sans-serif;\">&#8226 pagina.html</label>\n"
                + "                <br>\n"
                + "            </div>\n"
                + "        </header>\n"
                + "\n"
                + "        <!-- Bootstrap core JavaScript -->\n"
                + "        <script src=\"../resources/static/vendor/jquery/jquery.min.js\"></script>\n"
                + "        <script src=\"../resources/static/bootstrap/js/bootstrap.bundle.min.js\"></script>\n"
                + "    </form>\n"
                + "</body>\n"
                + "\n"
                + "</html>";
        return html;
    }

    @Web("cuadrado2")
    public static String square(String num) {
        int numI = Integer.parseInt(num);
        String html = "<!DOCTYPE html>\n"
                + "<html>\n"
                + "  <head>\n"
                + "    <meta charset=\"utf-8\">\n"
                + "    <link rel=\"shortcut icon\" href=\"/favicon.ico\">"
                + "    <title>Inicio</title>\n"
                + "  </head>\n"
                + "  <body>\n"
                + "  <h3>El cuadrado es: </h3>"
                + "  <h2>" + numI * numI + "</h2>"
                + "  </body>\n"
                + "</html>";
        return html;
    }
}
