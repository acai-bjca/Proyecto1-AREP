/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
    private static String error;
    private static String contentType;
    private static String mensaje;
    private static String archivo;
    private static File rtaFile;
    private static String rtaFile1;
    private static BufferedReader br = null;
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
        //Recibir multiples solicitudes no concurrentes
        try {
            System.out.println("Listo para recibir. Escuchando puerto " + PUERTO);
            while (true) {
                clientSocket = serverSocket.accept();
                while (!clientSocket.isClosed()) {
                    // El in y el out son para el flujo de datos por el socket (streams).
                    out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()),
                        true); 
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    //salidaDatos = new BufferedOutputStream(clientSocket.getOutputStream()); // Muestra los datos respuesta al cliente.
                    processRequest(clientSocket, out, in);
                }
                
            }

        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }

        out.close();
        in.close();
        clientSocket.close();
        serverSocket.close();
    }    
    
    public static void processRequest(Socket clientSocket, PrintWriter out, BufferedReader in) throws IOException {
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
        System.out.println("FILE longitud: "+rtaFile.length());
        System.out.println("FILE RTA1: "+rtaFile1);        
        
        System.out.println("error: "+error+ " mensaje: "+mensaje+" content: "+contentType);
        out.println("HTTP/1.1 " + error + mensaje);
        out.println("Content-type: " + contentType);
        out.println("Content-length: " + rtaFile.length());
        BufferedOutputStream salidaDatos = new BufferedOutputStream(clientSocket.getOutputStream()); // Muestra los datos respuesta al cliente.        
        if(solicitud.contains("apps")){
            File file = new File(RUTA_RESOURCES, archivo);
            System.out.println(file.exists());
            int fileLength = (int) file.length();
            byte[] datos = convertirABytes(file, fileLength);
            salidaDatos.write(datos, 0, fileLength);
        }else if(error.equals("200")){
            String temp;
            while ((temp = br.readLine()) != null)
                out.write(temp);
            br.close();
        }
        out.close();
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
            searchFilesInStaticResources(archivo.substring(archivo.indexOf("/")+1));
        }        
    }
    
    public static void searchFilesInApps(String archivo) throws IOException{        
        if(urlsHandler.containsKey(archivo)){            
            System.out.println("LO encontro");
            Handler h = urlsHandler.get(archivo);
            archivo = h.process();            
            generateResponse("200");
            contentType = "text/html";
            rtaFile1 = archivo;
            
        }else{
           generateResponse("404");
        }        
    }
    
    public static void searchFilesInStaticResources(String archivo) throws IOException{
        rtaFile = new File(RUTA_RESOURCES, archivo);        
        if (rtaFile.exists()) {
            generateResponse("200");           
            rtaFile1 = archivo;
            FileNameMap fileNameMap = URLConnection.getFileNameMap();
            contentType = fileNameMap.getContentTypeFor (rtaFile1);
            System.out.println("Contenido "+contentType);
            br = new BufferedReader(new FileReader(RUTA_RESOURCES+"/"+archivo));
        } else {
           generateResponse("400");
        }
    }
    
    public static void generateResponse(String typeResponse) throws IOException{
        if(typeResponse.equals("404")){
            error = "404 ";
            mensaje = "NOT FOUND";
            archivo = "fileNotFound.html";
            //contentType = "text/html";
            rtaFile1 = archivo;            
            FileNameMap fileNameMap = URLConnection.getFileNameMap();
            contentType = fileNameMap.getContentTypeFor (rtaFile1);
            System.out.println("Contenido "+contentType);
        }else if(typeResponse.equals("404")){
            error = "400 ";
            mensaje = "BAD REQUEST";
            archivo = "badRequest.html";
            //contentType = "text/html";
            rtaFile1 = archivo;
            System.out.println("rtaFile1 "+ rtaFile1);
            FileNameMap fileNameMap = URLConnection.getFileNameMap();
            contentType = fileNameMap.getContentTypeFor (rtaFile1);
            System.out.println(contentType);
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
