package AD2021Exercises.HTTPServer;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Date;
import java.util.Map;
import java.util.logging.*;
import java.util.stream.Collectors;

public class HTTPServer {


    static final File WEB_ROOT = new File("/home/mort/git/appdev_http/lib");
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

            //Parse HTTP request.
            HTTPRequest request = parseRequest(inReader);

            // Format and send response
            HTTPResponse response = formatResponse(request);

            // buffer the socket output stream
            outStream = new BufferedOutputStream(connectionSocket.getOutputStream());

            // Send message
            writeOutAndFlush(outStream, response);

        } catch (IOException e) {
            System.out.println("IO EXCEPTION: " + e.getMessage());
        } finally {
            closeConnection(connectionSocket, inReader, outStream);
        }
        return false;
    }

    private static void writeOutAndFlush(BufferedOutputStream outStream, HTTPResponse response) {
        // PrintWriter is buffered by outStream
        PrintWriter writeOut = new PrintWriter(outStream);

        // Format and append the response status-line to message
        // HTTP-Version SP Status-Code SP Reason-Phrase CRLF
        writeOut.println(
                response.getVersion()
                        + " " +
                        response.getStatusCode()
                        + " " +
                        response.getReasonPhrase());

        // Append head fields to response message
        response.getHeadFields()
                .entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .forEach(writeOut::println);

        // Append message body to response
        writeOut.println(response.getBody());

        // Flush writer and output stream.
        writeOut.flush();
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
            if (reader != null) reader.close();
            if (outStream != null) outStream.close();
            if (connection != null) connection.close();
        } catch (IOException ioe) {
            logger.info(ioe.getMessage());
        } finally {
            fileLogger.close(); // Write tail to logger file
        }
    }

    //todo: formatResponse()

    /**
     * Formats an HTTP response to the provided HTTP Request.
     *
     * @param request HTTPRequest object to create a response for.
     * @return HTTPResponse object containing response message,
     * or null if HTTPResponse object creation failed for any reason.
     */
    private static HTTPResponse formatResponse(HTTPRequest request) {
        HTTPResponse response = null;
        switch (request.getMethod()) {
            case "GET":
                logger.info("GET request");
                response = handleGETRequest(request);
                break;
            case "HEAD":
                logger.info("HEAD request");
                response = handleHEADRequest(request);
                break;
            case "POST":
                logger.info("POST request");
                response = handlePOSTRequest(request);
                break;
            default:
                logger.info("Not implemented");
                response = handleNYIRequest(request);
        }

        return response;
    }

    /**
     * Handler for request methods not yet implemented. Default in request handler switch case.
     *
     * @param request The HTTPRequest representing the request message.
     */
    private static HTTPResponse handleNYIRequest(HTTPRequest request) {
        logger.info("Building response");
        HTTPResponse.Builder resBuilder = new HTTPResponse.Builder(request.getVersion(), "501", "Not Implemented");

        File file = new File(WEB_ROOT, METHOD_NOT_SUPPORTED);
        try (BufferedReader r = new BufferedReader(new FileReader(new File(WEB_ROOT, METHOD_NOT_SUPPORTED)))) {
            logger.info("Adding response body");

            // Adding element headers
            String filetype = Files.probeContentType(file.toPath());
            logger.info("Content-type from probe: " + filetype);
            resBuilder.headField("Content-type:", filetype);
            resBuilder.headField("Content-length:", String.valueOf(file.length()));
            // adding response body from file
            String body = r.lines().collect(Collectors.joining("\r\n"));
            resBuilder.body(body.toString());
        } catch (IOException ioe) {
            logger.info(ioe.getMessage());
        }

        return resBuilder.build();
    }

    //todo: formatResponseHeader()

    /**
     * Formats the header of an HTTP response.
     */
    private static void formatResponseHeader() {

    }

    //todo: formatResponseBody()

    /**
     * Formats the body of an HTTP response. Usually, this involves appending the contents of a file.
     */
    private static void formatResponseBody() {

    }


    //todo: handleHEADRequest
    public static HTTPResponse handleHEADRequest(HTTPRequest request) {

        return null;
    }

    //todo: handleGETRequest
    public static HTTPResponse handleGETRequest(HTTPRequest request) {

        return null;
    }

    //todo: handlePOSTRequest
    private static HTTPResponse handlePOSTRequest(HTTPRequest request) {

        return null;
    }

    private static void log(String logMessage) {
        logger.finest(logMessage);

    }

}
