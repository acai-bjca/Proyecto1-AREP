package apps;

import java.io.*;
import java.lang.reflect.Method;
import java.net.FileNameMap;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author estudiante
 */
public class Service {

    public static HashMap<String, Handler> urlsHandler = new HashMap<String, Handler>();
    private static final int PUERTO = 35000;
    private static File RUTA_RESOURCES = new File("src/main/resources");

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

    public static void addMethod(String className) {
        Class<?> clase;
        try {
            clase = Class.forName("WebServiceHello");
            Method[] methods = clase.getMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(Web.class)) {
                    Class[] argTypes = new Class[] { String[].class };
                    System.out.println("Metodo guardado: "+method.getName());
                    System.out.println("NOmbre  a guradar en handler: apps/" + method.getAnnotation(Web.class).value());
                    urlsHandler.put("apps/" + method.getAnnotation(Web.class).value(), new StaticMethodHandler(method));
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

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
            BufferedOutputStream salidaDatos = null;
            //Recibir multiples solicitudes no concurrentes
            try {
                System.out.println("Listo para recibir. Escuchando puerto " + PUERTO);
                clientSocket = serverSocket.accept();
                while (!clientSocket.isClosed()) {
                    // El in y el out son para el flujo de datos por el socket (streams).
                    out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()),
                            true);
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    salidaDatos = new BufferedOutputStream(clientSocket.getOutputStream()); // Muestra los datos respuesta al cliente.
                    processRequest(clientSocket, out, in, salidaDatos);
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

    public static void processRequest(Socket clientSocket, PrintWriter out, BufferedReader in, BufferedOutputStream salidaDatos) throws IOException {
        String inputLine, solicitud = "";        
        while ((inputLine = in.readLine()) != null) {
            System.out.println("Received: " + inputLine);
            if (inputLine.contains("GET")) {
                solicitud = inputLine; // Lee la primera linea de la solicitud
                System.out.println("Solicitud: " + solicitud); //Ejemplo: GET / HTTP/1.1
                readRequest(solicitud, out, salidaDatos);                
            }
            if (!in.ready()) { //Ready devuelve verdadero si la secuencia está lista para ser leída.
                break;
            }
        }
        out.close();
        salidaDatos.flush();
    }

    public static void readRequest(String solicitud, PrintWriter out, BufferedOutputStream salidaDatos) throws IOException {
        StringTokenizer tokens = new StringTokenizer(solicitud); // Divide la solicitud en diferentes "tokens" separados por espacio.
        String metodo = tokens.nextToken().toUpperCase(); // Obtenemos el primer token, que en este caso es el metodo de
        // la solicitud HTTP.
        String requestURI = tokens.nextToken().substring(1); // Obtenemos el segundo token: identificador recurso: /apps/archivo.tipo.
        String[] requestURISplit = requestURI.split("/"); // [apps,archivo.tipo].

        //String archivo = requestURISplit[requestURISplit.length-1];
        String archivo = requestURI;
        System.out.println("archivo " + archivo);

        if (requestURI.contains("apps")) {
            searchFilesInApps(archivo, out, salidaDatos);
        } else {
            searchFilesInStaticResources(archivo.substring(archivo.indexOf("/") + 1), out, salidaDatos);
        }
    }

    public static void searchFilesInApps(String archivo, PrintWriter out, BufferedOutputStream salidaDatos) throws IOException {
        System.out.println("BUSCANDO ARCHIVOS EN APPS");
        boolean useParam = false;
        String parametro = "";
        if(archivo.contains("?")){
            parametro = archivo.substring(archivo.indexOf("=")+1);
            useParam = true;
            archivo = archivo.substring(0, archivo.indexOf("?"));
        }
        System.out.println("NOMBRE ARCHIVO A BUSCAR : "+ archivo);
        if (urlsHandler.containsKey(archivo)) {
            System.out.println("LO ENCONTRO");            
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: text/html");
            out.println("\r\n");
            if(useParam) out.println(urlsHandler.get(archivo).process(parametro));
            else {
                System.out.println("RTA sin parametro: "+urlsHandler.get(archivo).process());
                out.write(urlsHandler.get(archivo).process());
            }            
        }else {            
            archivo = "fileNotFound.html";
            File file = new File(RUTA_RESOURCES, archivo);
            int fileLength = (int) file.length();
            byte[] datos = convertirABytes(file, fileLength);
            
            out.println("HTTP/1.1 404 NOT FOUND");
            out.println("Content-Type: text/html");
            out.println("Content-length: " + fileLength);
            out.println("\r\n");            
            out.flush();
            salidaDatos.write(datos, 0, fileLength);
            salidaDatos.flush();
        }
    }

    public static void searchFilesInStaticResources(String archivo, PrintWriter out, BufferedOutputStream salidaDatos) throws IOException {
        File file = new File(RUTA_RESOURCES, archivo);
        if (file.exists()) {
            int fileLength = (int) file.length();
            byte[] datos = convertirABytes(file, fileLength);
            
            out.println("HTTP/1.1 202 OK");
            
            if(archivo.contains("jpg") || archivo.contains("jpeg")) out.println("Content-Type: image/jpeg");
            else if(archivo.contains("png")) out.println("Content-Type: image/png");
            else if(archivo.contains("html")) out.println("Content-Type: text/html");
            
            out.println("Content-length: " + fileLength);
            out.println("\r\n");
            out.flush();
            
            salidaDatos.write(datos, 0, fileLength);
            salidaDatos.flush();
        } else {
            archivo = "badRequest.html";
            file = new File(RUTA_RESOURCES, archivo);
            int fileLength = (int) file.length();
            byte[] datos = convertirABytes(file, fileLength);
            
            out.println("HTTP/1.1 404 BAD REQUEST");
            out.println("Content-Type: text/html");
            out.println("Content-length: " + fileLength);
            out.println("\r\n");
            out.flush();
            salidaDatos.write(datos, 0, fileLength);
            salidaDatos.flush();
        }
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