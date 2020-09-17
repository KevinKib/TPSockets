/***
 * EchoServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */

package stream;

import java.io.*;
import java.net.*;

public class EchoServer {

    /**
     * receives a request from client then sends an echo to the client
     *
     * @param clientSocket the client socket
     **/
    static void doService(Socket clientSocket) {
        try {
            final BufferedReader socIn = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            final BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            final PrintStream socOut = new PrintStream(clientSocket.getOutputStream());

            Thread read = new Thread(() -> {
                try {
                    while (true) {
                        String line = socIn.readLine();
                        System.out.println("Client: "+line);
                    }
                } catch (Exception e) {
                    System.err.println("Error in EchoServer:" + e);
                } finally {
                    try {
                        socIn.close();
                    }
                    catch (IOException e) {
                        System.err.println("Failed socket.close()");
                    }
                }

            });

            Thread write = new Thread(() -> {
                try {
                    while (true) {
                        String line = stdIn.readLine();
                        if (line.equals(".")) break;
                        socOut.println(line);
                    }
                } catch (Exception e) {
                    System.err.println("Error in EchoServer:" + e);
                } finally {
                    try {
                        stdIn.close();
                        socOut.close();
                    }
                    catch (IOException e) {
                        System.err.println("Failed socket.close()");
                    }
                }
            });

            read.start();
            write.start();

        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
    }

    static void startServer(String args[]) {
        ServerSocket listenSocket;

        if (args.length != 1) {
            System.out.println("Usage: java EchoServer <EchoServer port>");
            System.exit(1);
        }
        try {
            listenSocket = new ServerSocket(Integer.parseInt(args[0])); //port
            while (true) {
                Socket clientSocket = listenSocket.accept();
                System.out.println("connexion from:" + clientSocket.getInetAddress());
                doService(clientSocket);
            }
        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
    }

    /**
     * main method
     *
     * @param EchoServer port
     **/
    public static void main(String args[]) {
        System.out.println("Server start");

        String[] newArgs = {"61709"};
        startServer(newArgs);
    }
}

  
