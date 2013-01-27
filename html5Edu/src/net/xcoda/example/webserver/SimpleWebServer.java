package net.xcoda.example.webserver;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

import javax.activation.MimetypesFileTypeMap;


public class SimpleWebServer {
    public static int PORT = 9090;
    public static String DOCROOT = "./docroot";
   
    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(PORT);
        System.out.println("WebServer is listening..");
        Socket socket = null;
        while( (socket = server.accept()) !=null){
        	System.out.println(socket.getInetAddress() +" has connected.");
            BufferedReader in = new BufferedReader(
                    				new InputStreamReader(
                    						socket.getInputStream()));
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            String reqLine =  in.readLine();
            System.out.print(reqLine);
            StringTokenizer tokenizer = new StringTokenizer(reqLine);
            if(reqLine !=null && "GET".equals(tokenizer.nextToken())){
                String reqURI = tokenizer.nextToken();
                File file = new File(DOCROOT + reqURI);
                if(file.exists() && file.isFile()){
                    String mimeType = new MimetypesFileTypeMap()
                                            .getContentType(file);
                    int numOfBytes = (int) file.length();
                    FileInputStream inFile = new FileInputStream(file);
                    byte[] fileInBytes = new byte[numOfBytes];
                    inFile.read(fileInBytes);
                    out.writeBytes("HTTP/1.1 200 OK \r\n");
                    out.writeBytes("Content-Type: " + mimeType + "\r\n");
                    out.writeBytes("Content-Length: " + numOfBytes + "\r\n");
                    out.writeBytes("\r\n");
                    out.write(fileInBytes, 0, numOfBytes);
                    inFile.close();
                    System.out.println(" >> 200 OK");
                }else if("/".equals(reqURI)){
                	out.writeBytes("HTTP/1.1 200 OK \r\n");
                    out.writeBytes("\r\n");
                    out.writeBytes("<h1>Welcome Simple Web Server</h1>");
                    out.writeBytes("<p>This is index page.</p>");
                    System.out.println(" >> 200 OK");
                }else{
                    out.writeBytes("HTTP/1.1 404 Not Found \r\n");
                    out.writeBytes("Connection: close\r\n");
                    out.writeBytes("\r\n");
                    out.writeBytes("<h1>404 Not Found</h1>");
                    System.out.println(" >> 404 NotFound");
                }
            }else{
                out.writeBytes("HTTP/1.1 500 Internal Server Error \r\n");
                out.writeBytes("Connection: close\r\n");
                out.writeBytes("\r\n");
                out.writeBytes("<h1>500 Internal Server Error</h1>");
                System.out.println(" >> 500 ServerError");
            }
            out.flush();
            out.close();
            in.close();
            socket.close();   
            System.out.println("response has finished.");
        }
    }
}
