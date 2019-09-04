package apps;

import java.net.*;
import java.util.Date;
import java.util.StringTokenizer;
import java.io.*;

public class HttpServer {

    // Paginas del servicio http
    private static final File RUTA = new File("src/main/resources"); // Donde se alojan las p�ginas
    private static String archivo = "index.html"; // P�gina principal
    // Puerto a escuchar
    private static final int PUERTO = 35000;

    /**
     * Este metodo convierte en un arreglo de bytes el archivo file que se le
     * pasa como parametro.
     *
     * @param file
     * @param fileLength
     * @return
     * @throws IOException
     */
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

    public static void respuestaSolicitud(Socket clientSocket, PrintWriter out, BufferedReader in,
        BufferedOutputStream salidaDatos) throws IOException {
        String inputLine, solicitud = "";
        int count = 0;
        while ((inputLine = in.readLine()) != null) {
            System.out.println("Received: " + inputLine);
            if (count < 1) {
                solicitud = inputLine; // Lee la primera linea de la solicitud
            }
            if (!in.ready()) {
                break;
            }
            count++;
        }

        StringTokenizer tokens = new StringTokenizer(solicitud); // Divide la solicitud en diferentes "tokens".
        String metodo = tokens.nextToken().toUpperCase(); // Obtenemos el primer token, que en este caso es el metodo de
        // la solicitud HTTP.
        String archivoSolicitud = tokens.nextToken().toLowerCase(); // Obtenemos el segundo token que es el archivo con
        // su tipo a enviar.

        String content = "";
        String error = "200 ", mensaje = "OK";
        if (!archivoSolicitud.equals("/")) {
            if (archivoSolicitud.contains(".")) {
                String tipoArchivo = archivoSolicitud.substring(archivoSolicitud.indexOf(".") + 1);
                String nombreArchivo = archivoSolicitud.substring(0, archivoSolicitud.indexOf("."));
                archivo = nombreArchivo + "." + tipoArchivo;
                if (tipoArchivo.equals("html")) {
                    content = "text/html";
                } else if (tipoArchivo.equals("jpg")) {
                    content = "image/jpg";
                } else {
                    error = "400 ";
                    mensaje = "BAD REQUEST";
                    archivo = "badRequest.html";
                    content = "text/html";
                }

            } else {
                error = "400 ";
                mensaje = "BAD REQUEST";
                archivo = "badRequest.html";
                content = "text/html";
            }
        }

        File file = null;
        int fileLength = 0;
        byte[] datos = new byte[0];

        file = new File(RUTA, archivo);
        System.out.println(file.exists());
        if (file.exists()) {
            error = "200 ";
            mensaje = "OK";
        } else {
            error = "404 ";
            mensaje = "NOT FOUND";
            archivo = "fileNotFound.html";
            file = new File(RUTA, archivo);
            content = "text/html";
        }
        fileLength = (int) file.length();
        System.out.println("file: "+file);
        System.out.println(fileLength);
        datos = convertirABytes(file, fileLength);

        // Se debe enviar el encabezado de respuesta, para que el cliente entienda y
        // muestre lo que el servidor envio.
        out.println("HTTP/1.1 " + error + mensaje);
        out.println("Content-type: " + content);
        out.println("Content-length: " + fileLength);
        out.println();
        out.flush();

        salidaDatos.write(datos, 0, fileLength);
        salidaDatos.flush();
    }

    public static void main(String[] args) throws IOException {
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
        try {
            System.out.println("Listo para recibir. Escuchando puerto " + PUERTO);
            while (true) {
                clientSocket = serverSocket.accept();
                // El in y el out son para el flujo de datos por el socket (streams).
                out = new PrintWriter(clientSocket.getOutputStream(), true); 
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                salidaDatos = new BufferedOutputStream(clientSocket.getOutputStream()); // Muestra los datos respuesta al cliente.
                respuestaSolicitud(clientSocket, out, in, salidaDatos);
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
}
