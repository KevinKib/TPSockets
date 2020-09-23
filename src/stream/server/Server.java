/***
 * EchoServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */

package stream.server;

import java.net.*;

public class Server {

    /**
     * main method
     *
     * @param EchoServer port
     **/
    public static void startServer(String args[]) {
        ServerSocket listenSocket;

        if (args.length != 1) {
            System.out.println("Usage: java EchoServer <EchoServer port>");
            System.exit(1);
        }
        try {
            listenSocket = new ServerSocket(Integer.parseInt(args[0])); //port
            System.out.println("Server ready...");
            while (true) {
                Socket clientSocket = listenSocket.accept();
                System.out.println("Connexion from:" + clientSocket.getInetAddress());
                ServerPerClientThread ct = new ServerPerClientThread(clientSocket);
                ct.start();
            }
        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
    }

    public static void main(String args[]) {
        System.out.println("Multithreaded server start");

        String[] newArgs = {"61709"};
        startServer(newArgs);
    }
}
