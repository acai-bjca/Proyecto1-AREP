/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apps;


import eu.medsea.mimeutil.MimeUtil;
import java.io.*;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
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
    private static String error;
    private static String content;
    private static String mensaje;
    private static String archivo;
    private static File rtaFile;
    private static File RUTA_RESOURCES = new File("src/main/resources");

    public static void init(){      
        String ruta = "src/main/java";
        File directorio = new File(ruta);
        File[] ficheros = directorio.listFiles();
        for (int f=0; f<ficheros.length; f++){            
            if(!ficheros[f].isDirectory()){
                String className = ficheros[f].getName();
                className = className.substring(0,className.indexOf("."));
                addMethod(className);                    
            }
        }
    }
    
    public static void addMethod(String className){
        Class<?> clase;
        try {
            clase = Class.forName("WebServiceHello");            
            Method[] methods = clase.getMethods();
            System.out.println("2");
            for(Method method : methods){    
                if(method.isAnnotationPresent(Web.class)){                    
                    //urlsHandler.put("apps/"+method.getAnnotation(Web.class), new StaticMethodHandler(method));
                    urlsHandler.put("apps/"+method.getAnnotation(Web.class).value(), new StaticMethodHandler(method));
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void listen() throws IOException{
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
            while (true) {
                clientSocket = serverSocket.accept();
                // El in y el out son para el flujo de datos por el socket (streams).
                out = new PrintWriter(clientSocket.getOutputStream(), true); 
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                salidaDatos = new BufferedOutputStream(clientSocket.getOutputStream()); // Muestra los datos respuesta al cliente.
                processRequest(clientSocket, out, in, salidaDatos);
            }

        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }

        out.close();
        in.close();
        salidaDatos.close();
        clientSocket.close();
        serverSocket.close();
    }    
    
    public static void processRequest(Socket clientSocket, PrintWriter out, BufferedReader in, BufferedOutputStream salidaDatos) throws IOException {
        String inputLine, solicitud = "";
        int count = 0;
        while ((inputLine = in.readLine()) != null) {
            System.out.println("Received: " + inputLine);
            if (count < 1) {
                solicitud = inputLine; // Lee la primera linea de la solicitud
            }
            if (!in.ready()) { //Ready devuelve verdadero si la secuencia está lista para ser leída.
                break;
            }
            count++;
        }
        System.out.println("Solicitud: " + solicitud); //Ejemplo: GET / HTTP/1.1
        readRequest(solicitud);
        System.out.println("FILE RTA: "+rtaFile);
        int fileLength = (int) rtaFile.length();
        System.out.println(fileLength);
        byte[] datos = convertirABytes(rtaFile, fileLength);
        System.out.println("4444444444");

        // Se debe enviar el encabezado de respuesta, para que el cliente entienda y
        // muestre lo que el servidor envio.
        System.out.println("error: "+error+ " mensaje: "+mensaje+" content: "+content);
        out.println("HTTP/1.1 " + error + mensaje);
        out.println("Content-type: " + content);
        out.println("Content-length: " + fileLength);
        out.println();
        out.flush();
        System.out.println("111111111");

        salidaDatos.write(datos, 0, fileLength);
        System.out.println("22222222222");
        salidaDatos.flush();
    }
    
    public static void readRequest(String solicitud) throws IOException{
        StringTokenizer tokens = new StringTokenizer(solicitud); // Divide la solicitud en diferentes "tokens" separados por espacio.
        String metodo = tokens.nextToken().toUpperCase(); // Obtenemos el primer token, que en este caso es el metodo de
        // la solicitud HTTP.
        String requestURI = tokens.nextToken().substring(1); // Obtenemos el segundo token: identificador recurso: /apps/archivo.tipo.
        String[] requestURISplit = requestURI.split("/"); // [apps,archivo.tipo].        
        System.out.println(requestURISplit.length);
        
        //String archivo = requestURISplit[requestURISplit.length-1];
        String archivo = requestURI;
        System.out.println("archivo "+archivo);
        
        if(requestURISplit.length==2){
            if(requestURISplit[requestURISplit.length-2].equals("apps")) searchFilesInApps(archivo);
            else generateResponse("400");
        }else{
            searchFilesInStaticResources(archivo);
        }        
    }
    
    public static void searchFilesInApps(String archivo) throws IOException{
        System.out.println("BUSCANDO ARCHIVOOOOOOOOOOOOOOOOOOOOOOOOS");
        System.out.println("urlsHandler.length: "+urlsHandler.size());
        for(String s : urlsHandler.keySet()){
            System.out.println("Metodoo: "+s);
        }
        if(urlsHandler.containsKey(archivo)){
            Handler h = urlsHandler.get(archivo);
            archivo = h.process();            
            generateResponse("200");
            content = "text/html";
            rtaFile = new File(archivo);
            
        }else{
           generateResponse("404");
        }        
    }
    
    public static void searchFilesInStaticResources(String archivo) throws IOException{
        rtaFile = new File(RUTA_RESOURCES, archivo);        
        if (rtaFile.exists()) {
            generateResponse("200");
            content = MimeUtil.getMimeTypes(archivo).toString();
        } else {
           generateResponse("400");
        }
    }
    
    public static void generateResponse(String typeResponse) throws IOException{
        if(typeResponse.equals("404")){
            error = "404 ";
            mensaje = "NOT FOUND";
            archivo = "fileNotFound.html";
            content = "text/html";            
            rtaFile = new File(RUTA_RESOURCES, archivo);
        }else if(typeResponse.equals("404")){
            error = "400 ";
            mensaje = "BAD REQUEST";
            archivo = "badRequest.html";
            content = "text/html";
            rtaFile = new File(RUTA_RESOURCES, archivo);
        }else if(typeResponse.equals("200")){
            error = "200 ";
            mensaje = "OK";
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
