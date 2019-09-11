/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
    private static final int PUERTO = getPort();
    private static String RUTA_RESOURCES = "src/main/resources";

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
            clase = Class.forName(className);
            Method[] methods = clase.getMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(Web.class)) {
                    Class[] argTypes = new Class[] { String[].class };
                    System.out.println("Metodo guardado: "+method.getName());
                    System.out.println("NOmbre  a guradar en handler: apps/" + method.getAnnotation(Web.class).value());
                    urlsHandler.put("/apps/" + method.getAnnotation(Web.class).value(), new StaticMethodHandler(method));
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
                readRequest(solicitud, out, salidaDatos, clientSocket);                
            }
            if (!in.ready()) { //Ready devuelve verdadero si la secuencia está lista para ser leída.
                break;
            }
        }
        out.close();
        salidaDatos.flush();
    }

    public static void readRequest(String solicitud, PrintWriter out, BufferedOutputStream salidaDatos, Socket clientSocket) throws IOException {
        StringTokenizer tokens = new StringTokenizer(solicitud); // Divide la solicitud en diferentes "tokens" separados por espacio.
        String metodo = tokens.nextToken().toUpperCase(); // Obtenemos el primer token, que en este caso es el metodo de
        // la solicitud HTTP.
        String requestURI = tokens.nextToken(); // Obtenemos el segundo token: identificador recurso: /apps/archivo.tipo.
        
        String archivo = requestURI;
        System.out.println("archivo " + archivo);

        if (requestURI.contains("apps")) {
            searchFilesInApps(archivo, out, salidaDatos);
        } else {
            searchFilesInStaticResources(archivo, out, salidaDatos, clientSocket);
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
            out.println("HTTP/1.1 200 OK\r");
            out.println("Content-Type: text/html\r");
            out.println("\r");
            if(useParam) out.println(urlsHandler.get(archivo).process(parametro)+"\r");
            else {
                System.out.println("RTA sin parametro: "+urlsHandler.get(archivo).process());
                out.println(urlsHandler.get(archivo).process()+"\r");
            }           
        }
        else {
            StringBuffer sb = new StringBuffer();
            try (BufferedReader reader = new BufferedReader(new FileReader(RUTA_RESOURCES + "/notFound.html"))) {
                String infile = null;
                while ((infile = reader.readLine()) != null) {
                    sb.append(infile);
                }
            }
            out.println("HTTP/1.1 404 Not Found\r");
            out.println("Content-Type: text/html\r");            
            out.println("\r");
            out.println(sb.toString()+"\r");
        }
    }

    public static void searchFilesInStaticResources(String archivo, PrintWriter out, BufferedOutputStream salidaDatos, Socket clientSocket) throws IOException {       
        BufferedReader br = null;
        if(archivo.equals("/")) archivo="/index.html";
        String path = RUTA_RESOURCES + archivo;
        try {
            br = new BufferedReader(new FileReader(path));
        } catch (Exception e) {
            System.out.println("No lo encontro");
            StringBuffer sb = new StringBuffer();
            try (BufferedReader reader = new BufferedReader(new FileReader(RUTA_RESOURCES + "/notFound.html"))) {
                String infile = null;
                while ((infile = reader.readLine()) != null) {
                    sb.append(infile);
                }
            }
            out.println("HTTP/1.1 404 Not Found\r");
            out.println("Content-Type: text/html\r");            
            out.println("\r");
            out.println(sb.toString()+"\r");
        }
        
        out.println("HTTP/1.1 202 Ok\r");
        if(archivo.contains("jpg")){
            out.println("Content-Type: image/jpeg\r");
            out.println("\r");
            System.out.println("RUTAAAAAAAAAAAAAAAA: "+RUTA_RESOURCES + archivo);
            BufferedImage image = ImageIO.read(new File(RUTA_RESOURCES + archivo));
            ImageIO.write(image, "JPG", clientSocket.getOutputStream());
        }
        else if(archivo.contains("html")){  
            StringBuffer sb = new StringBuffer();
            try (BufferedReader reader = new BufferedReader(new FileReader(RUTA_RESOURCES + archivo))) {
                String infile = null;
                while ((infile = reader.readLine()) != null) {
                    sb.append(infile);
                }
            }
            out.println("Content-Type: text/html\r");
            out.println("\r");
            out.println(sb.toString());         
            
        }
        else if(archivo.contains("favicon.ico")){  
            out.println("Content-Type: image/vnd.microsoft.icon\r");
            out.println("\r");
            List<BufferedImage> images = ICODecoder.read(new File(RUTA_RESOURCES + archivo));
            ICOEncoder.write(images.get(0), clientSocket.getOutputStream());
        }         
    }

    static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567; //returns default port if heroku-port isn't set (i.e.on localhost)
    }

}
