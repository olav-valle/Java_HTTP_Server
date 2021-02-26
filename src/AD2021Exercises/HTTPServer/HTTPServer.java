package AD2021Exercises.HTTPServer;



import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

public class HTTPServer {
    static final File WEB_ROOT = new File("lib/");
    static final String DEFAULT_FILE = "index.html";
    static final String FILE_NOT_FOUND = "404.html";
    static final String METHOD_NOT_SUPPORTED = "not_supported.html";

    // Port Setting
    static final int PORT = 8080;
    //verbose mode to console output.
    static final boolean verbose = true;

    public static void main(String[] args) {
        try {
            ServerSocket serverConnect = new ServerSocket(PORT);
            System.out.println("Server started.... \n Port is open on " + PORT);
            //Listen until connection is required from user
            while(true){
                Socket connect = serverConnect.accept();
                if(verbose){
                    System.out.println("Connection established on date: " + new Date() + "\n");
                }
                runServer(connect);

            }

        } catch (IOException e) {
            System.out.println("Server error as: " + e);
        }
    }

    private static void runServer(Socket connect){
        //Manage client connection.
        BufferedReader in;
        PrintWriter out;
        BufferedOutputStream dataOut;
        String fileRequested;

        try {
            //Read characters from the client via input stream.
            in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            //Get character output stream to client (for headers).
            out = new PrintWriter(connect.getOutputStream());
            //Get binary output stream to client (for request resource)
            dataOut = new BufferedOutputStream(connect.getOutputStream());

            //Parse information from HTTP request, first line.
            String input = in.readLine();
            String[] parsed = input.split(" ");
            StringTokenizer parse = new StringTokenizer(input);
            //Get requested method.
            String func = parsed[0].toUpperCase();
            String method = parse.nextToken().toUpperCase();
            //Get concrete requested resource.
            String file = parsed[1].toLowerCase();
            fileRequested = parse.nextToken().toLowerCase();

            //Check the method. Here we only support GET and HEAD.
            if ((!method.equals("GET")) && (!method.equals("HEAD"))){
                System.out.println("501, method not implemented. " + method +"\n");
            }


        } catch (IOException e) {
            System.out.println("IO EXCEPTION: " + e.getMessage());
        }
    }
}
