package AD2021Exercises.HTTPServer;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
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
            ServerSocket listenSocket = new ServerSocket(PORT);
            System.out.println("Server started.... \n Port is open on " + PORT);
            //Listen until connection is required from user
            while (true) {
                Socket connectionSocket = listenSocket.accept();
                if (verbose) {
                    System.out.println("Connection established on date: " + new Date() + "\n");
                }
                runServer(connectionSocket);

            }

        } catch (IOException e) {
            System.out.println("Server error as: " + e);
        }
    }

    private static void runServer(Socket connectionSocket) {
        //Manage client connection.
        BufferedReader inReader = null;
        BufferedOutputStream outStream = null;

        try {
            //Read characters from the client via input stream.
            inReader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            //Get character output stream to client (for headers).
            //out = new PrintWriter(connectionSocket.getOutputStream());
            //Get binary output stream to client (for request resource)
            outStream = new BufferedOutputStream(connectionSocket.getOutputStream());

            //Parse information from HTTP request, first line.
            String request = inReader.readLine();
            //HTTP request split at "spaces", stored as String[]

            // Format and send response
            formatResponse(request, outStream);



        } catch (IOException e) {
            System.out.println("IO EXCEPTION: " + e.getMessage());
        } finally {
            closeConnection(connectionSocket, inReader, outStream);
        }
    }

    /**
     * Closes given Socket, BufferedReader and BufferedOutputStream objects.
     *
     * @param connection
     * @param reader
     * @param outStream
     */
    private static void closeConnection(Socket connection, BufferedReader reader, BufferedOutputStream outStream) {
        try {
            reader.close();
            outStream.close();
            connection.close();
        } catch (IOException ioe) {
            System.err.println("Exception while closing connection socket or related buffers: " + ioe.getMessage());
        }
    }

    //todo: formatResponse()

    /**
     * Formats an HTTP response to the provided HTTP Request.
     *
     * @param HTTPRequest
     * @param dataOut
     */
    private static void formatResponse(String HTTPRequest, BufferedOutputStream dataOut) {
        try {
            //HTTP Requests have format: "METHOD /pathTo/requestedFile.type HTTP/VERSION"
            String[] requestArray = HTTPRequest.split(" ");
            // HTTP request method
            String method = requestArray[0].toUpperCase();

            switch(method){
                case "HEAD":
                    handleHEADRequest();
                    break;
                case "GET":
                    handleGETRequest();
                    break;
                case "POST":
                    handlePOSTRequest();
                    break;
                default:
                    handleNYIRequest();
            }

            // find mimeType of requested file
            String mimeType = Files.probeContentType(Path.of(requestArray[1]));
            // if mimeType == null, then the file was not found
            // or type not defined in RFC 2045.
            // Either way, we have a problem regarding Content-Type in the response header.
            if (mimeType != null) {
//                formatResponseHeader();
            }
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
            //todo: less lazy plz
        }

    }

    /**
     * Handler for request methods not yet implemented. Default in request handler switch case.
     */
    private static void handleNYIRequest() {
    }

    //todo: formatResponseHeader()

    /**
     * Formats the header of an HTTP response.
     * @param outStream
     * @param statusCode
     * @param contentLength
     * @param mimeType
     */
    private static void formatResponseHeader(BufferedOutputStream outStream, int statusCode, int contentLength, String mimeType) {

    }

    //todo: formatResponseBody()

    /**
     * Formats the body of an HTTP response. Usually, this involves appending the contents of a file.
     */
    private static void formatResponseBody() {

    }


    //todo: handleHEADRequest
    public static void handleHEADRequest() {

    }

    //todo: handleGETRequest
    public static void handleGETRequest(){

    }

    //todo: handlePOSTRequest
    private static void handlePOSTRequest() {

    }

}
