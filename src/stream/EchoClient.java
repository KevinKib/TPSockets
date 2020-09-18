/***
 * EchoClient
 * Example of a TCP client 
 * Date: 10/01/04
 * Authors:
 */
package stream;

import java.io.*;
import java.net.*;


public class EchoClient {


    /**
     * main method
     * accepts a connection, receives a message from client then sends an echo to the client
     **/
    public static void startClient(String[] args) throws IOException {

        if (args.length != 2) {
            System.out.println("Usage: java EchoClient <EchoServer host> <EchoServer port>");
            System.exit(1);
        }

        try {
            // creation socket ==> connexion
            final Socket echoSocket = new Socket(args[0], new Integer(args[1]).intValue());
            final BufferedReader socIn = new BufferedReader(
                    new InputStreamReader(echoSocket.getInputStream()));
            final PrintStream socOut = new PrintStream(echoSocket.getOutputStream());
            final BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            Thread read = new Thread(() -> {
                try {
                    while (true) {
                        String line = socIn.readLine();
                        System.out.println("Server: " + line);
                    }
                } catch (Exception e) {
                    System.err.println("Error in EchoClient:" + e);
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
                    System.err.println("Error in EchoClient:" + e);
                } finally {
                    try {
                        stdIn.close();
                        socOut.close();
                        echoSocket.close();
                    }
                    catch (IOException e) {
                        System.err.println("Failed socket.close()");
                    }
                }
            });

            read.start();
            write.start();
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host:" + args[0]);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                    + "the connection to:" + args[0]);
            System.exit(1);
        }
    }

    /**
     * main method
     *
     **/
    public static void main(String args[]) {
        System.out.println("Client start");

        String[] newArgs = {"localhost", "61709"};
        try {
            startClient(newArgs);
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}


