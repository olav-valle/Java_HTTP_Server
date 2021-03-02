package AD2021.HTTPServer1;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestClient {
    static final int EXERCISE_NUM = 1; //exercise questions.

    public static void main(String[] args) throws IOException {
        for (int i = 1; i < 10; i++){


            Socket s = new Socket(InetAddress.getByName("localhost"), 8080);
            PrintWriter pw = new PrintWriter(s.getOutputStream());

            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String response;
            switch (i) {
                //To test 501 not implemented
                case 1:
                    sendRequest(pw, "abc", false, false);
                    break;
                //To test index file
                case 2:
                    sendRequest(pw, "get", false, false);
                    break;
                //To test 404 file not found
                case 3:
                    sendRequest(pw, "get", true, false);
                    break;
                //To test head method with index file
                case 4:
                    sendRequest(pw, "head", false, false);
                    break;
                //To test head method with file not found
                case 5:
                    sendRequest(pw, "head", true, false);
                    break;
                //To test post method with index file
                case 6:
                    sendRequest(pw, "post", false, true);
                    break;
                //To test user validation
                case 7:
                    sendRequestUserValidation(pw, "post", true, true);
                    break;
                //To test poker distribution
                case 8:
                    sendRequestPokerDistribution(pw, "post", true, true);
                    break;
                // To test Text Upload function (if you want more challenge, you can try to upload the image instead).
                case 9:
                    sendRequestUserTextUpload(s, pw, "post", true, true);
                    break;
                default:
                    System.out.println("Wrong Exercise!!");
            }

            while ((response = br.readLine()) != null) System.out.println(response);
            System.out.println();
            pw.close();
            br.close();
        }
    }

    private static void sendRequest(PrintWriter pw, String method, Boolean requestFileFlag, Boolean bodyFlag) {
        //request first line formation.
        pw.print(method + " /");
        if (requestFileFlag) { //requestFileFlag is to specify concrete service in the server.
            //This is a wrong folder, so you should not find index.html.
            pw.print("xyz");
        }
        pw.print(" HTTP/1.1\r\n");
        //request headers formation.
        pw.print("Host: localhost\r\n\r\n");
        //request body formation.
        if (bodyFlag) { //bodyFlag is to specify whether to add a request body in the message.
            pw.print("username = Smith\r\n");
        }
        pw.flush();
    }

    private static void sendRequestPokerDistribution(PrintWriter pw, String method, Boolean requestFileFlag, Boolean bodyFlag) {
        //request first line formation.
        pw.print(method + " /");
        if (requestFileFlag) {
            pw.print("PokerDistribution/");
        }
        pw.print(" HTTP/1.1\r\n");
        //request headers formation.
        pw.print("Host: localhost\r\n\r\n");
        //request body formation.
        if (bodyFlag) {
            pw.print("username = playerA\r\n");
        }
        pw.flush();
    }

    private static void sendRequestUserTextUpload(Socket s, PrintWriter pw, String method, Boolean requestFileFlag, Boolean bodyFlag) throws IOException {
        //request first line formation.
        pw.print(method + " /");
        if (requestFileFlag) {
            pw.print("UserTextUpload/");
        }
        pw.print(" HTTP/1.1\r\n");
        //request headers formation.
        pw.print("Host: localhost\r\n");
        pw.print("Content-Type: text/html\r\n");

        //This is to add a new header with the size of the sent file.
        pw.print("Content-Size: " + Files.size(Path.of(
                "/home/mort/git/appdev_http/src/" +
                        "AD2021Exercises/HTTPServer/" +
                        "testfile.html")) + "\r\n");
        pw.print("\r\n");
        pw.flush();

        //request body formation.
        if (bodyFlag) {
            //Change to your own txt file
            Files.copy(Path.of(
                    "/home/mort/git/appdev_http/src/" +
                            "AD2021Exercises/HTTPServer/" +
                            "testfile.html"), s.getOutputStream());
        }
        pw.flush();
    }

    private static void sendRequestUserValidation(PrintWriter pw, String method, Boolean requestFileFlag, Boolean bodyFlag) {
        //request first line formation.
        pw.print(method + " /");
        if (requestFileFlag) {
            pw.print("UserValidation/");
        }
        pw.print(" HTTP/1.1\r\n");
        //request headers formation.
        pw.print("Host: localhost\r\n\r\n");
        //request body formation.
        if (bodyFlag) {
            StringBuilder uvb = new StringBuilder();
            uvb.append("8");
            uvb.append(System.getProperty("line.separator"));
            uvb.append("Julia");
            uvb.append(System.getProperty("line.separator"));
            uvb.append("Samantha");
            uvb.append(System.getProperty("line.separator"));
            uvb.append("Samantha_21");
            uvb.append(System.getProperty("line.separator"));
            uvb.append("1Samantha");
            uvb.append(System.getProperty("line.separator"));
            uvb.append("Samantha?10_2A");
            uvb.append(System.getProperty("line.separator"));
            uvb.append("JuliaZ007");
            uvb.append(System.getProperty("line.separator"));
            uvb.append("Julia@007");
            uvb.append(System.getProperty("line.separator"));
            uvb.append("_Julia007");
            uvb.append(System.getProperty("line.separator"));


            pw.print(uvb.toString());
            System.out.println(uvb.toString());
        }
        pw.flush();
    }
}
