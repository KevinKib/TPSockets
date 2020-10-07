package http.client;

import java.net.InetAddress;
import java.net.Socket;

public class WebPing {
    public static void ping(String address, String port) {

        String httpServerHost = address;
        int httpServerPort = Integer.parseInt(port);

        try {
            InetAddress addr;
            Socket sock = new Socket(httpServerHost, httpServerPort);
            addr = sock.getInetAddress();
            System.out.println("Connected to " + addr);
            sock.close();
        } catch (java.io.IOException e) {
            System.out.println("Can't connect to " + httpServerHost + ":" + httpServerPort);
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        ping("localhost", "3000");
    }
}