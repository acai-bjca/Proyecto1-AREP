package apps;

import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import net.sf.image4j.codec.ico.ICODecoder;
import net.sf.image4j.codec.ico.ICOEncoder;

/**
 *
 * @author estudiante
 */
public class Service {

    public static HashMap<String, Handler> urlsHandler = new HashMap<String, Handler>();
    private static final int PUERTO = 35000;
    private static File RUTA_RESOURCES = new File("src/main/resources");

    /**
     * init Inicializa la aplicacion, recorriendo todas las clases dentro del
     * directorio raiz, para guardar los metodos en la lista de manejador de
     * metodos
     */
    public static void init() {
        String ruta = "src/main/java";
        File directorio = new File(ruta);
        File[] ficheros = directorio.listFiles();
        for (int f = 0; f < ficheros.length; f++) {
            if (!ficheros[f].isDirectory()) {
                String className = ficheros[f].getName();
                className = className.substring(0, className.indexOf("."));
                addMethod(className);
            }
        }
    }

    /**
     * addMethod Guarda todos los metodos de una clase dada que tienen una
     * anotacion Web
     *
     * @param className Nombre de la clase
     */
    public static void addMethod(String className) {
        Class<?> clase;
        try {
            clase = Class.forName("WebServiceHello");
            Method[] methods = clase.getMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(Web.class)) {
                    Class[] argTypes = new Class[]{String[].class};
                    System.out.println("Metodo guardado: " + method.getName());
                    System.out.println("NOmbre  a guradar en handler: apps/" + method.getAnnotation(Web.class).value());
                    urlsHandler.put("apps/" + method.getAnnotation(Web.class).value(), new StaticMethodHandler(method));
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * listen Crea y mantieen activos los sockets de Cliente y Servidor para
     * permitir comunicacion constante entre ellos.
     *
     * @throws IOException Excepcion de entrada y salida
     */
    public static void listen() throws IOException {
        while (true) {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(PUERTO);
            } catch (IOException e) {
                System.err.println("Could not listen on port: " + PUERTO + ".");
                System.exit(1);
            }

            Socket clientSocket = null;
            PrintWriter out = null;
            BufferedReader in = null;
            //Recibir multiples solicitudes no concurrentes
            try {
                System.out.println("Listo para recibir. Escuchando puerto " + PUERTO);
                clientSocket = serverSocket.accept();
                while (!clientSocket.isClosed()) {
                    // El in y el out son para el flujo de datos por el socket (streams).
                    out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()),
                            true);
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    processRequest(clientSocket, out, in);
                }
                out.close();
                in.close();
                clientSocket.close();
                serverSocket.close();

            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
        }
    }

    /**
     * processRequest Recibe una solicitud y la procesa
     *
     * @param clientSocket Socket cliente por el que se estan pasando las
     * solicitudes
     * @param out Stream o flujo de salida
     * @param in Stream o flujo de entrada
     * @throws IOException
     */
    public static void processRequest(Socket clientSocket, PrintWriter out, BufferedReader in) throws IOException {
        String inputLine, solicitud = "";
        while ((inputLine = in.readLine()) != null) {
            System.out.println("Received: " + inputLine);
            if (inputLine.contains("GET")) {
                solicitud = inputLine; // Lee la primera linea de la solicitud
                System.out.println("Solicitud: " + solicitud); //Ejemplo: GET / HTTP/1.1
                readRequest(solicitud, out, clientSocket);
            }
            if (!in.ready()) { //Ready devuelve verdadero si la secuencia está lista para ser leída.
                break;
            }
        }
        out.close();
    }

    /**
     * readRequest Interpreta y descompone la solicitud para determinar los
     * recursos solicitados. Dado el nombre del archivo o requestURI, determina
     * en donde se debe hacer la busqueda. Puede ser en apps, o en los recursos
     * estaticos. Delega la busqueda para cada uno.
     *
     * @param solicitud Cadena con la primer linea de la peticion mandada por el
     * cliente
     * @param out Stream o flujo de salida
     * @param clientSocket Socket del cliente
     * @throws IOException Excepcion de entrada y salida
     */
    public static void readRequest(String solicitud, PrintWriter out, Socket clientSocket) throws IOException {
        StringTokenizer tokens = new StringTokenizer(solicitud); // Divide la solicitud en diferentes "tokens" separados por espacio.
        String metodo = tokens.nextToken().toUpperCase(); // Obtenemos el primer token, que en este caso es el metodo de
        // la solicitud HTTP.
        String requestURI = tokens.nextToken().substring(1); // Obtenemos el segundo token: identificador recurso: /apps/archivo.tipo.
        String[] requestURISplit = requestURI.split(" "); // [apps,archivo.tipo].

        //String archivo = requestURISplit[requestURISplit.length-1];
        String archivo = requestURI;
        System.out.println("archivo " + archivo);

        if (requestURI.contains("apps")) {
            searchFilesInApps(archivo.substring(archivo.indexOf("/") + 1), out);
        } else {
            //searchFilesInStaticResources(archivo.substring(archivo.indexOf("/") + 1), out, clientSocket);
            searchFilesInStaticResources(archivo, out, clientSocket);
        }
    }

    /**
     * searchFilesInApps Busca un archivo en el directorio apps de la
     * aplicacion.
     *
     * @param archivo requestURI
     * @param out Stream o flujo de salida
     * @throws IOException Excepcion de entrada y salida
     */
    public static void searchFilesInApps(String archivo, PrintWriter out) throws IOException {
        System.out.println("BUSCANDO ARCHIVOS EN APPS");
        boolean useParam = false;
        String parametro = "";
        if (archivo.contains("?")) {
            parametro = archivo.substring(archivo.indexOf("=") + 1);
            useParam = true;
            archivo = archivo.substring(0, archivo.indexOf("?"));
        }
        System.out.println("NOMBRE ARCHIVO A BUSCAR : " + archivo);
        if (urlsHandler.containsKey(archivo)) {
            System.out.println("LO ENCONTRO");
            out.println("HTTP/1.1 200 OK\r");
            out.println("Content-Type: text/html\r");
            out.println("\r");
            if (useParam) {
                out.println(urlsHandler.get(archivo).process(parametro));
            } else {
                System.out.println("RTA sin parametro: " + urlsHandler.get(archivo).process());
                out.write(urlsHandler.get(archivo).process());
            }
        } else {
            StringBuffer sb = new StringBuffer();
            try (BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "notFound.html"))) {
                String infile = null;
                while ((infile = reader.readLine()) != null) {
                    sb.append(infile);
                }
            }
            out.println("HTTP/1.1 404 Not Found\r");
            out.println("Content-Type: text/html\r");
            out.println("\r");
            out.println(sb.toString());
        }
    }

    /**
     * searchFilesInStaticResources Busca un archivo en los recursos estaticos
     * de la aplicacion. Dependiendo el tipo de archivo, delega a otro metodo la
     * generacion y envio de la respuesta por el flujo de salida
     *
     * @param archivo nombre del archivo o requestURI
     * @param out Stream o flujo de salida
     * @param clientSocket Socket del cliente
     * @throws IOException Excepcion de entrada y salida
     */
    public static void searchFilesInStaticResources(String archivo, PrintWriter out, Socket clientSocket) throws IOException {
        System.out.println("Nombre archivo recursos: " + archivo);
        if (archivo.equals("")) {
            archivo = "index.html";
        }

        File file = new File(RUTA_RESOURCES, archivo);
        if (file.exists()) {
            System.out.println("Encontro recurso estatico");
            if (archivo.contains("jpg") || archivo.contains("jpeg")) {
                imageResponse(out, archivo, clientSocket);
            } else if (archivo.contains("html")) {
                htmlResponse(out, archivo);
            } else if (archivo.contains("favicon.ico")) {
                faviconResponse(out, archivo, clientSocket);
            }
        } else {
            notFoundResponse(out, archivo);
        }
    }

    /**
     * htmlResponse Envia la respuesta por el flujo de salida con el html
     * encontrado en los recursos estaticos de la aplicacion.
     * @param out Stream o flujo de salida
     * @param fileName nombre del archivo de tipo html a buscar
     * @throws IOException Excepcion de entrada y salida
     */
    private static void htmlResponse(PrintWriter out, String fileName) throws IOException {
        StringBuffer sb = new StringBuffer();
        try (BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.dir") + fileName))) {
            String infile = null;
            while ((infile = reader.readLine()) != null) {
                sb.append(infile);
            }
        }
        out.println("HTTP/1.1 200 OK\r");
        out.println("Content-Type: text/html\r");
        out.println("\r");
        out.println(sb.toString());
    }

    /**
     * imageResponse Envia la respuesta por el flujo de salida con la imagen
     * encontrada en los recursos estaticos de la aplicacion.
     * @param out Stream o flujo de salida
     * @param fileName nombre del archivo de tipo html a buscar
     * @param clientSocket socket del cliente
     * @throws IOException Excepcion de entrada y salida
     */
    private static void imageResponse(PrintWriter out, String fileName, Socket clientSocket) throws IOException {
        out.println("HTTP/1.1 200 OK\r");
        out.println("Content-Type: image/jpg\r");
        out.println("\r");
        BufferedImage image = ImageIO.read(new File(System.getProperty("user.dir") + "/" + fileName));
        ImageIO.write(image, "JPG", clientSocket.getOutputStream());
    }

    /**
     * htmlResponse Envia o responde al cliente un html indicando que el recurso no fue encontrado.
     * @param out Stream o flujo de salida
     * @param fileName nombre del archivo de tipo html a buscar
     * @throws IOException Excepcion de entrada y salida
     */
    private static void notFoundResponse(PrintWriter out, String fileName) throws IOException {
        StringBuffer sb = new StringBuffer();
        try (BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.dir") + fileName))) {
            String infile = null;
            while ((infile = reader.readLine()) != null) {
                sb.append(infile);
            }
        }
        out.println("HTTP/1.1 404 Not Found\r");
        out.println("Content-Type: text/html\r");
        out.println("\r");
        out.println(sb.toString());
    }

    private static void faviconResponse(PrintWriter out, String fileName, Socket clientSocket) throws IOException {
        out.println("HTTP/1.1 200 OK\r");
        out.println("Content-Type: image/vnd.microsoft.icon\r");
        out.println("\r");
        List<BufferedImage> images = ICODecoder.read(new File(System.getProperty("user.dir") + fileName));
        ICOEncoder.write(images.get(0), clientSocket.getOutputStream());
    }

    private static byte[] convertirABytes(File file, int fileLength) throws IOException {
        FileInputStream fileIn = null;
        byte[] fileData = new byte[fileLength];

        fileIn = new FileInputStream(file);
        fileIn.read(fileData);
        if (fileIn != null) {
            fileIn.close();
        }
        return fileData;
    }

}
