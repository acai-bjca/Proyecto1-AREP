
import apps.Web;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author estudiante
 */
public class WebServiceHello {
    
    @Web("cuadrado")
    public static String square(){
        String html="<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "  <head>\n" +
                    "    <meta charset=\"utf-8\">\n" +
                    "    <title>Inicio</title>\n" +
                    "  </head>\n" +
                    "  <body>\n" +   
                    "  </body>\n" +
                    "</html>";
        return html;
    }
}
