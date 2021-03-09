package HTTPServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

//// TODO: 02/03/2021 Automate this stuff with some asserts or something...
public class ServerTestClient implements Runnable {

    static String FILE_PATH;


    public ServerTestClient(){

    }

    @Override
    public void run() {
        FILE_PATH = System.getProperty("user.dir") +  "/test/HTTPServer/testfile.html";
        for (int i = 1; i < 10; i++) {
            System.out.println("\n------\nRunning case " + i + "\n------");
            try {
                Socket s = new Socket(InetAddress.getByName("localhost"), 8080);
                PrintWriter pw = new PrintWriter(s.getOutputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));

                String response;
                switch (i) {
                    //To test 501 not implemented
                    case 1:
                        System.out.println("1. Method not supported.");
                        sendRequest(pw, "abc", false, false);
                        break;
                    //To test index file
                    case 2:
                        System.out.println("2. Get method with default response.");
                        sendRequest(pw, "get", false, false);
                        break;
                    //To test 404 file not found
                    case 3:
                        System.out.println("3. Get method with file not found.");
                        sendRequest(pw, "get", true, false);
                        break;
                    //To test head method with index file
                    case 4:
                        System.out.println("4. Head method with index file");
                        sendRequest(pw, "head", false, false);
                        break;
                    //To test head method with file not found
                    case 5:
                        System.out.println("5. Head method with file not found.");
                        sendRequest(pw, "head", true, false);
                        break;
                    //To test post method with index file
                    case 6:
                        System.out.println("6. Post method.");
                        sendRequest(pw, "post", false, true);
                        break;
                    //To test user validation
                    case 7:
                        System.out.println("7. User validation.");
                        sendRequestUserValidation(pw, "post", true, true);
                        break;
                    //To test poker distribution
                    case 8:
                        System.out.println("8. Poker distribution.");
                        sendRequestPokerDistribution(pw, "post", true, true);
                        break;
                    // To test Text Upload function (if you want more challenge, you can try to upload the image instead).
                    case 9:
                        System.out.println("9. User image/file upload.");
                        sendRequestUserTextUpload(s, pw, "post", true, true);
                        break;
                    default:
                        System.out.println("Wrong Exercise!!");
                }

                while ((response = br.readLine()) != null) System.out.println(response);
                pw.close();

                br.close();
            } catch (Exception e) {
                System.out.println("Case " + i + " failed due to exception: \n\t" + e.getMessage() );
            }
        }

    }

    public static void main(String[] args) {
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
        pw.print("Content-Size: " + Files.size(Path.of(FILE_PATH)) + "\r\n");
        pw.print("\r\n");
        pw.flush();

        //request body formation.
        if (bodyFlag) {
            //Change to your own txt file
            //Files.lines(Path.of(FILE_PATH)).forEach(System.out::println);
            //Files.lines(Path.of(FILE_PATH)).forEach(pw::println);

//            for(String line : Files.readAllLines(Path.of(FILE_PATH))) {
//                pw.print(line + "\r\n");
//            }

             Files.copy(Path.of(FILE_PATH), s.getOutputStream());
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
