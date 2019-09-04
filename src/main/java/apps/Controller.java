package apps;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public class Controller 
{
    public static Service servicio;
    
    public static void main( String[] args )
    {
        try {
            servicio = new Service();
            servicio.init();
            servicio.listen();
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
