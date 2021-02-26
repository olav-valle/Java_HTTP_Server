package AD2021.HTTPServer1;
/*
This HTTPServer4 is to realize POST method.
 */
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class HTTPServer4 {
    //file definition, change the web_root to your absolute path if needed.
    // All the files you can define in your own folder and style.
    static final File WEB_ROOT = new File("lib/");
    //Default file is for successful request.
    static final String DEFAULT_FILE = "index.html";
    //404 file is for not found request.
    static final String FILE_NOT_FOUND = "404.html";
    //not_supported file is for not supported methods. Currently this server can accept GET, HEAD and POST methods.
    static final String METHOD_NOT_SUPPORTED = "not_supported.html";
    //server port definition.
    static final int PORT = 8080;

    public static void main(String[] args) {
        //serversocket definition.
        ServerSocket server = null;
        try {
            // initialize server socket with PORT.
            server = new ServerSocket(PORT);
            System.out.println("Server started.\n Listening for connections on port: " + PORT + "...\n");

            while (true) {
                // we listen until user halts server execution
                Socket connection = server.accept();
                System.out.println("Connection opened. (" + new Date() + ")");
                // Start to run the server logics. connection is for the IO stream.
                runServer(connection);
            }

        } catch (IOException e) {
            System.out.println("Error while accepting request!");
        } finally {
            try {
                // server close connection.
                server.close();

            } catch (IOException e) {
                System.out.println("Unable to close the server socket!");

            }
        }

    }

    private static void runServer(Socket connection) {
        //BufferedReader is a connection for characters (text).
        BufferedReader request = null;
        //BufferedOutputStream is a byte based connection can be used for all types of files (images...)
        BufferedOutputStream response = null;
        try {
            // READ CHARACTERS FROM THE CLIENT VIA INPUT STREAM ON THE SOCKET
            // Initialization for BufferedReader instance.
            request = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            // WE GET output stream to client.
            response = new BufferedOutputStream(connection.getOutputStream());

            // Read only the first line of the request, and break it where there is a space
            // character
            String[] fragments = request.readLine().split(" ");
            //method extraction
            String method = fragments[0].toUpperCase();
            //Requested service extraction
            String fileRequested = fragments[1].toLowerCase();

            // To get the requestBody, because it can contain several lines, so we use a StringBuilder.
            StringBuilder requestBody = new StringBuilder();
            //The string for every line.
            String line;
            // Whether we arrive at the request body.
            Boolean bodyFlag = false;
            try {
//                while ((line = request.readLine()) != null) {
                //condition to read the request after
                while (request.ready()) {
                    line = request.readLine();
                    if (bodyFlag) {
                        //To read the requestBody for several lines.
                        requestBody.append(line);
                    }else if (line.trim().isBlank()) {
                        bodyFlag = true;
                    }
                    else {
                        //you can use here to read headers.

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();

            }


            // we support only GET, HEAD and POST methods. SO we check
            if (!method.equals("GET") && !method.equals("HEAD") && !method.equals("POST")) {
                // If it is not supporting method, use replyMethodNotSupported method.
                replyMethodNotSupported(response, method);

            } else {
                try {
                    // If it is correct method, call the replyWithRequestedFile method.
                    fileRequested = replyWithRequestedFile(response, fileRequested, requestBody.toString(), method);

                } catch (FileNotFoundException fnfe) {
                    try {
                        // If they cannot find the requested file, there will be an exception.
                        // call respondFileNotFound if exception occurs.
                        respondFileNotFound(response, fileRequested, method);

                    } catch (IOException ioe) {
                        System.out.println("Error with file not found exception: " + ioe.getMessage());
                    }
                }
            }

        } catch (IOException ioe) {
            replyServerError(ioe);

        } finally {
            //close Connection
            closeConnection(connection, request, response);
        }
    }

    private static void closeConnection(Socket connect, BufferedReader in, BufferedOutputStream dataOut) {
        //close connection for socket, input reader and output stream.
        // handle exceptions.
        try {
            in.close();
            dataOut.close();
            connect.close();

        } catch (Exception e) {
            System.err.println("Error closing stream: " + e.getLocalizedMessage());

        }

        System.out.println("Connection closed.\n");
    }

    private static void replyServerError(IOException ioe) {
        //server running error exception
        System.out.println("Server error: " + ioe);
    }

    private static String replyWithRequestedFile(BufferedOutputStream response, String fileRequested, String requestBody, String method)
            throws IOException {

        //Step 1: if what you read is a directory, try to find whether this directory exist,
        // if not there will be filenotfound exception thrown by the program.
        // if it exists, use default file to reply.
        // ADD MORE CONDITIONS FOR SPECIFIC REQUEST, SUCH AS
        // "/uservalidation/", "/pokerdistribution/" AND "/usertextupload/".
        // YOU CAN ADD THE DEFAULT_FILE IN THE RESPONSE AS WELL.
        if (fileRequested.endsWith("/")) {
            fileRequested += DEFAULT_FILE;
        }

        File file = new File(WEB_ROOT, fileRequested);
        int fileLength = (int) file.length();
        String content = getContentType(fileRequested);

        //because the output is a outputstream, so we need to change the file to byte[] array.
        byte[] fileData = readFileData(file, fileLength);
        //call the method to format headers.
        formatHttpResponseHeader(response, "200 OK", fileLength, content);

        if (method.equals("GET")){
            //GET method will reply response body
            //HEAD method will not, so we don't add anything for HEAD method.
            response.write(fileData, 0, fileLength);
        }

        if(method.equals("POST")){
            response.write(fileData, 0, fileLength);
            //StringBuilder to format response body.
            StringBuilder bodyFormat = new StringBuilder();
            bodyFormat.append("\n\n Requested body is: ");
            // HERE you have to add the request body.
            //Example:
            bodyFormat.append(requestBody);
            //response is based on byte array, so we have to change bodyFormat to the correct data type.
            response.write(bodyFormat.toString().getBytes(StandardCharsets.UTF_8));

            // HERE YOU SHOULD ADD OTHER TYPE OF REQUESTS
            //"/uservalidation/", "/pokerdistribution/" AND "/usertextupload/".
            // EACH CAN USE bodyFormat for response.

            //Example:
            //fileRe is the requestFile without folder information.
//            if (fileRe.equals("/uservalidation/")) {
//                  //Define the instance of UserNameValidation
//                UserNameValidation userNameValidation = new UserNameValidation(requestBody);
//                    //define in this UserNameValidation class a method for the NameList.
//                userNameValidation.setNameList(requestBody);
//                    //Validate the NameList.
//                userNameValidation.validate();
//                bodyFormat.append(requestBody);
//                bodyFormat.append(System.getProperty("line.separator"));
//                bodyFormat.append("Validation Results are: ");
//                bodyFormat.append(System.getProperty("line.separator"));
//                    //Print out the results by call the method getValidationResult() in UserNameValidation.
//                bodyFormat.append(userNameValidation.getValidationResult());
//            }

        }
        response.flush();
        System.out.println("File " + fileRequested + " of type " + content + " returned!");
        return fileRequested;
    }

    private static void replyMethodNotSupported(BufferedOutputStream response, String method) throws IOException {
        // method for not supported method.
        System.out.println("501 NOT IMPLEMENTED: " + method + " method.");

        //get this file length and type.
        File file = new File(WEB_ROOT, METHOD_NOT_SUPPORTED);
        int fileLength = (int) file.length();
        String contentMimeType = "text/html";

        formatHttpResponseHeader(response, "501 not implemented", fileLength, contentMimeType);

        // change to byte array for response message
        byte[] fileData = readFileData(file, fileLength);
        // use output stream for response message.
        response.write(fileData, 0, fileLength);
        response.flush();
    }

    private static void formatHttpResponseHeader(BufferedOutputStream dataOut, String responseStatus, int fileLength,
                                                 String contentMimeType) {
        // format response headers.
        // use a character (text) based output stream as PrintWriter.
        final PrintWriter out = new PrintWriter(dataOut);
        //response first line
        out.println("HTTP/1.1 " + responseStatus);
        // response header information
        out.println("Server: Java HTTP Server from Di: 1.0");
        out.println("Date: " + new Date());
        out.println("Content-type: " + contentMimeType);
        out.println("Content-length: " + fileLength);
        out.println(); // blank line between headers and body. VERY IMPORTANT.
        out.flush();
    }

    private static byte[] readFileData(File file, int fileLength) throws IOException {
        FileInputStream fileIn = null;
        byte[] fileData = new byte[fileLength];
        fileIn = new FileInputStream(file);
        fileIn.read(fileData);

        fileIn.close();
        return fileData;
    }

    // return supported Mime Types
    private static String getContentType(String fileRequested) {
        if (fileRequested.endsWith(".htm") || fileRequested.endsWith(".html"))
            return "text/html";
        else
            return "text/plain";
    }

    private static void respondFileNotFound(BufferedOutputStream response, String fileRequested, String method) throws IOException {
        File file = new File(WEB_ROOT, FILE_NOT_FOUND);
        int fileLength = (int) file.length();

        formatHttpResponseHeader(response, "404 file not found", fileLength, "text/html");

        byte[] fileData = readFileData(file, fileLength);
        if(method.equals("GET") || method.equals("POST")) {
            response.write(fileData, 0, fileLength);
        }
        response.flush();
        System.out.println("File " + fileRequested + " not found!");
    }
}

