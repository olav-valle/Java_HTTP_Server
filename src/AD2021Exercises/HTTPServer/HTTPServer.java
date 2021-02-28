package AD2021Exercises.HTTPServer;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.logging.*;

public class HTTPServer {


    static final File WEB_ROOT = new File("lib/");
    static final String DEFAULT_FILE = "index.html";
    static final String FILE_NOT_FOUND = "404.html";
    static final String METHOD_NOT_SUPPORTED = "not_supported.html";

    // Port Setting
    static final int PORT = 8080;
    //verbose mode to console output.
    static final boolean verbose = true;

    // Logging
    private static Logger logger;
    private static ConsoleHandler consoleLogger;
    private static FileHandler fileLogger;

    public static void main(String[] args) {
        logger = Logger.getLogger("ServerLog");
        consoleLogger = new ConsoleHandler();
        try {
            fileLogger = new FileHandler(
                    "%h/git/appdev_http/log.txt", true);
            fileLogger.setFormatter(consoleLogger.getFormatter());
            logger.addHandler(fileLogger);
        } catch (IOException ioe) {
            logger.info("Failed to create log file 'log.txt'. IOException: \n" + ioe.getMessage());
        }

        try {
            ServerSocket listenSocket = new ServerSocket(PORT);
            logger.info("Server started.... \n Port is open on " + PORT);
            //Listen until connection is required from user

            while (true) {
                Socket connectionSocket = listenSocket.accept();
                if (verbose) {
                    logger.info("Connection established on date: " + new Date() + "\n");
                }
                runServer(connectionSocket);
            }

        } catch (IOException e) {
            System.out.println("Server error as: " + e);
        }

    }

    /**
     * Runs server functions.
     *
     * @param connectionSocket Socket which client is connected on.
     * @return false, to signal socket connection is no longer open.
     */
    private static boolean runServer(Socket connectionSocket) {
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

            //Parse HTTP request.
            HTTPRequest request = parseRequest(inReader);

            // Format and send response
            HTTPResponse response = formatResponse(request);


        } catch (IOException e) {
            System.out.println("IO EXCEPTION: " + e.getMessage());
        } finally {
            closeConnection(connectionSocket, inReader, outStream);
        }
        return false;
    }

    /**
     * Parses the content of a BufferedReader, creating an HTTPRequest object from its contents.
     * This method naively parses any BufferedReader object as if its contents had the
     * formatting of an HTTP request according to RFC 2616.
     *
     * @param inReader A BufferedReader holding the data of an HTTP request.
     * @return An HTTPRequest object of the request, or null if object creation failed for any reason.
     */
    private static HTTPRequest parseRequest(BufferedReader inReader) {
        String method;
        String url;
        String version;
        HTTPRequest request = null;
        HTTPRequest.Builder reqBuilder;

        try {
            // HTTP request line has format "METHOD /path/to/requested.file HTTP/x.x"
            String[] requestLine = inReader.readLine().split(" ");
            method = requestLine[0]; // METHOD
            logger.info(method);
            url = requestLine[1];// /path/to/requested.file
            logger.info(url);
            version = requestLine[2]; // HTTP/x.x
            logger.info(version);

            // HTTPRequest uses builder pattern
            // Required parameters are method, url and HTTP version of request
            reqBuilder = new HTTPRequest.Builder(method, url, version);
            logger.info("Successfully created request builder.");

            // todo:
            //  double check using the readLine function as loop invariants on these.
            //  Will we get stuck on inner loop if it ends on

            // todo:
            //  Is double check of ready() and blank new-line redundant?
            //  Needs some thinking...

            // Add request header fields to Builder
            if (inReader.ready()) { // Check for presence of first header line
                String headLine = inReader.readLine();
            while (!headLine.trim().isBlank()) { // Head ends with blank newline, i.e. "\r\n".
                String[] keyValue = headLine.split(" "); // split into "FieldName:" "FieldValue"
                reqBuilder.headField(keyValue[0], keyValue[1]); // "FieldName:" is key, "FieldValue" is value
                if (inReader.ready()) { // next line from buffer, if present.
                    headLine = inReader.readLine();
                } else { // or a blank line if not, just to ensure that the loop ends.
                    headLine = "";
                }
            }// done with header fields
            logger.info("Done with header fields");
            }

            // Adding request body to Builder
            StringBuilder body = new StringBuilder();
            while (inReader.ready()) {
                logger.info("Building request body string.");
                String bodyLine = inReader.readLine();

                logger.info("Line to append is: " + bodyLine);
                body.append(bodyLine).append("\r\n"); // Append current line

                logger.info("Line appended to body: " + bodyLine);
            }// Done constructing body string

            // Add body string to Builder
            if (!body.toString().isBlank()) {
                // If body is not blank
                logger.info("Body string added to request object");
                reqBuilder.body(body.toString());
            } else {
                // Don't add body if blank
                logger.info("Body string is blank, nothing to add.");
            }

            // Finally we build the HTTPRequest object
            request = reqBuilder.build();
            logger.info("Printing request.toString:\n" + request.toString());

        } catch (IOException ioe) {
            logger.info(ioe.toString());
        }

        return request;
    }

    /**
     * Closes given Socket, BufferedReader and BufferedOutputStream objects.
     *
     * @param connection Connected socket.
     * @param reader     BufferedReader related to socket being closed.
     * @param outStream  BufferedOutputStream related to socket being closed.
     */
    private static void closeConnection(Socket connection, BufferedReader reader, BufferedOutputStream outStream) {
        try {
            reader.close();
            outStream.close();
            connection.close();
            fileLogger.close();
        } catch (IOException ioe) {
            System.err.println("Exception while closing connection socket or related buffers: " + ioe.getMessage());
        }
    }

    //todo: formatResponse()

    /**
     * Formats an HTTP response to the provided HTTP Request.
     *
     * @param request HTTPRequest object to create a response for.
     */
    private static HTTPResponse formatResponse(HTTPRequest request) {
        switch(request.getMethod()) {
            case "GET":
                break;
            case "HEAD":
                break;
            case "POST":
                break;
            default:
                handleNYIRequest();
        }

        return null;
    }

    /**
     * Handler for request methods not yet implemented. Default in request handler switch case.
     */
    private static void handleNYIRequest() {
        
    }

    //todo: formatResponseHeader()

    /**
     * Formats the header of an HTTP response.
     *
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
    public static void handleGETRequest() {

    }

    //todo: handlePOSTRequest
    private static void handlePOSTRequest() {

    }

    private static void log(String logMessage) {
        logger.finest(logMessage);

    }

}
