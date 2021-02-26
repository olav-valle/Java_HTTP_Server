package AD2021Exercises.HTTPServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class TestClient {
    public static void main(String[] args) throws IOException {
        Socket s = new Socket(InetAddress.getByName("localhost"), 8080);
        PrintWriter pw = new PrintWriter(s.getOutputStream());
        pw.print("POST / HTTP/1.1\r\n");
        pw.print("Host: localhost\r\n\r\n");
        pw.flush();
        BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        String t;
 //       while((t = br.readLine()) != null) System.out.println(t);
        br.close();
    }
}