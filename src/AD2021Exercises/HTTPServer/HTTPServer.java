package AD2021Exercises.HTTPServer;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
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

    // DEBUGGING
    // todo: remove
    // ----------------------------------------------
    static final boolean verbose = true;
    // Set this field to dump request message to terminal, instead of handling it
    private static final boolean DUMP_REQUEST_TO_TERMINAL = false;
    // ----------------------------------------------


    // Logging
    private static Logger logger;
    private static ConsoleHandler consoleLogger;
    private static FileHandler fileLogger;

    public static void main(String[] args) {
        logger = Logger.getLogger("ServerLog");
        consoleLogger = new ConsoleHandler();
        consoleLogger.setFormatter(new SimpleFormatter());
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

            if (DUMP_REQUEST_TO_TERMINAL) {
                dumpRequest(inReader);
            } else {
                //Parse HTTP request.

                HTTPRequest request = parseRequest(inReader);

                // Format and send response
                HTTPResponse response = formatResponse(request);

                // buffer the socket output stream
                outStream = new BufferedOutputStream(connectionSocket.getOutputStream());

                // Send message
                if (response != null) {
                    writeOutAndFlush(outStream, response);
                } else {
                    outStream.flush();
                }
                closeConnection(connectionSocket, inReader, outStream);
            }

        } catch (IOException e) {
            System.out.println("IO EXCEPTION: " + e.getMessage());
        } finally {
            closeConnection(connectionSocket, inReader, outStream);
        }
        return false;
    }

    private static void dumpRequest(BufferedReader in) {
        in.lines().forEach(System.out::println);
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
            //  Will we get stuck on inner loop if inReader has no more lines?

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

            Thread.sleep(1);

            StringBuilder bodyBuilder = new StringBuilder();
            //inReader.lines().forEach(body::append);
//            String whatTheFuck = inReader.readLine();
//            logger.info("What the fuck is this? " + whatTheFuck);
            logger.info("Building request body string.");
            while (inReader.ready()) {
                String bodyLine = inReader.readLine();
                bodyBuilder.append(bodyLine).append("\r\n"); // Append current line
                //logger.info("Line appended to body: " + bodyLine);
            }// Done constructing body string

            String body = bodyBuilder.toString();
            // Add body string to Builder
            if (!body.isBlank()) {
                // If body is not blank
                logger.info("Body string added to request object");
                reqBuilder.body(body);
            } else {
                // Don't add body if blank
                logger.info("Body string is blank, nothing to add.");
            }

            // Finally we build the HTTPRequest object
            request = reqBuilder.build();
            logger.info("Printing request.toString:\n" + request.toString());

        } catch (IOException | InterruptedException ioe) {
            logger.info(ioe.toString());
        }

        return request;
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
        writeOut.print("\r\n");

        // Append message body, if any, to response
        if (!response.getBody().equals("")) {

            writeOut.println(response.getBody());
        }

        // Flush writer and output stream.
        writeOut.flush();
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

    /**
     * Formats an HTTP response to the provided HTTP Request.
     *
     * @param request HTTPRequest object to create a response for.
     * @return HTTPResponse object containing response message,
     * or null if HTTPResponse object creation failed for any reason.
     */
    private static HTTPResponse formatResponse(HTTPRequest request) {
        HTTPResponse response = null;
        HTTPResponse.Builder builder = null;
        switch (request.getMethod().toUpperCase()) {
            case "GET":
                logger.info("GET request");
                builder = handleGETRequest(request);
                break;
            case "HEAD":
                logger.info("HEAD request");
                builder = handleHEADRequest(request);
                break;
            case "POST":
                logger.info("POST request");
                builder = handlePOSTRequest(request);
                break;
            default:
                logger.info("Not implemented");
                builder = handleNYIRequest(request);
        }
        if (builder != null) {
            response = builder.build();
        }
        ;
        return response;
    }

    /**
     * Handler for request methods not yet implemented. Default in request handler switch case.
     *
     * @param request The HTTPRequest representing the request message.
     */
    // TODO: 01/03/2021 Refactor this into createResponseBuilder
    private static HTTPResponse.Builder handleNYIRequest(HTTPRequest request) {
        logger.info("Building response");
        HTTPResponse.Builder resBuilder = new HTTPResponse.Builder(request.getVersion(), "501", "Not Implemented");

        File file = new File(WEB_ROOT, METHOD_NOT_SUPPORTED);
        formatGeneralHeaders(resBuilder);
        formatContentHeaders(resBuilder, file);
        formatResponseBody(resBuilder, file);

        return resBuilder;
    }

    /**
     * Formats a response to a HEAD request message.
     *
     * @param request The HEAD request message to respond to.
     * @return A Builder object for the response message.
     */
    private static HTTPResponse.Builder handleHEADRequest(HTTPRequest request) {
        return formatMessage(request);
    }

    /**
     * Formats a response to a GET request message.
     *
     * @param request The GET request message to respond to.
     * @return A Builder object for the response message.
     */
    private static HTTPResponse.Builder handleGETRequest(HTTPRequest request) {
        return formatMessage(request);
    }

    /**
     * Creates an HTTPResponse builder object,
     * which has the appropriate status-line, header fields and body content for
     * the given HTTPRequest.
     *
     * @param request HTTPRequest object to create response for
     * @return HTTPResponse message builder object
     */
    private static HTTPResponse.Builder formatMessage(HTTPRequest request) {
        //Minimal response with only status-line
        HTTPResponse.Builder response = createResponseBuilder(request);

        File file;
        if (request.getUrl().equals("/")) {
            file = new File(WEB_ROOT, DEFAULT_FILE);        // Requested file was root, we return server specific default.
        } else if (Files.exists(Path.of(request.getUrl()))) {
            file = new File(WEB_ROOT, request.getUrl());    // Requested file exists
        } else {
            file = new File(WEB_ROOT, FILE_NOT_FOUND);      // Requested file does not exists
        }

        // General headers
        formatGeneralHeaders(response);
        // Content headers
        formatContentHeaders(response, file);
        // Response body, if request was GET
        if (request.getMethod().equalsIgnoreCase("GET")) {
            formatResponseBody(response, file);
        }

        return response;
    }


    //TODO: 01/03/2021 handlePOSTRequest
    // 1. Ensure that HTTPRequest handles body correctly.
    // 2. Implement methods used by POST tests: Poker, TextUpload and UserAuthenticate.
    // 3. Expand formatting and header methods to handle eventual output from external methods above.
    private static HTTPResponse.Builder handlePOSTRequest(HTTPRequest request) {

        return null;
    }


    /**
     * Creates a response message builder, and formats the status-line of the response based on the request message.
     * The builder object can be expanded with header fields and a body, before being built.
     *
     * @param request HTTP request being responded to.
     * @return A Builder object for an HTTP response, with only the status-line content.
     */
    public static HTTPResponse.Builder createResponseBuilder(HTTPRequest request) {
        String statusCode = "";
        String reasonPhrase = "";

        // Status-Line
        if (Files.exists(Path.of(request.getUrl()))) {
            // 200 OK
            statusCode = String.valueOf(HttpStatusCode.OK.getValue());
            reasonPhrase = HttpStatusCode.OK.getDescription();
        } else if (request.getMethod().equals("POST")) {
            statusCode = String.valueOf(HttpStatusCode.OK.getValue());
            reasonPhrase = HttpStatusCode.OK.getDescription();
        } else {
            // 404 File Not Found
            statusCode = String.valueOf(HttpStatusCode.NOT_FOUND.getValue());
            reasonPhrase = HttpStatusCode.NOT_FOUND.getDescription();
        }
        return new HTTPResponse
                .Builder(request.getVersion(), // HTTP-version same as request
                statusCode,
                reasonPhrase);
    }

    /**
     * Formats the general headers of an HTTP response.
     *
     * @param response Builder object for response message being formatted.
     */
    private static void formatGeneralHeaders(HTTPResponse.Builder response) {
        //todo: add more general headers?

        response.headField("Server", "Basic HTTP Server 0.1");

        response.headField("Date", toServerHTTPTime(new Date().toInstant()));

    }

    /**
     * Formats the content-related header fields of an HTTP response, like length and type.
     *
     * @param response Builder object for response message being formatted.
     * @param file     the file holding the content, used for probing values.
     */
    private static void formatContentHeaders(HTTPResponse.Builder response, File file) {
        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String filetype = Files.probeContentType(file.toPath());
            String modified = toServerHTTPTime(Files.getLastModifiedTime(file.toPath()).toInstant());
            ;
            logger.info("Content-type from probe: " + filetype);
            response.headField("Content-length", String.valueOf(file.length()));
            response.headField("Content-type", filetype);
            response.headField("Last-Modified", modified);
        } catch (IOException ioe) {
            logger.info(ioe.getMessage());
        }
    }

    /**
     * Adds the content of the requested file to the response message body.
     *
     * @param response Builder for response being formatted.
     * @param file     File requested.
     */
    private static void formatResponseBody(HTTPResponse.Builder response, File file) {
        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            logger.info("Adding response body");
            // adding response body from file
            String s = Files.readString(file.toPath());
            response.body(s);
        } catch (IOException ioe) {
            logger.info(ioe.getMessage());
        }
    }

    /**
     * Formats the given Instant according to RFC 1123.
     * Example: Sun, 06 Nov 1994 08:49:37 GMT.
     *
     * @param time The Instant to format.
     * @return The Instant, formatted according to RFC 1123.
     */
    private static String toServerHTTPTime(Instant time) {
        return DateTimeFormatter // RFC 1123 compliant HTTP time
                .ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)
                .withZone(ZoneId.of("GMT"))
                .format(time);

    }


}
