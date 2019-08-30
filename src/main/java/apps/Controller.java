package apps;

/**
 * Hello world!
 *
 */
public class Controller 
{
    public static Service servicio;
    
    
    
    public static void main( String[] args )
    {
        servicio = new Service();
        servicio.init();
    }
}
