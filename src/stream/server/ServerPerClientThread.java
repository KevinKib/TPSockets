/***
 * ServerPerClientThread
 * Example of a TCP server
 * Date: 14/12/08
 * Authors:
 */

package stream.server;

import java.io.*;
import java.net.*;

public class ServerPerClientThread extends Thread {

    private ServerWriter serverWriter;
    private Socket clientSocket;

    ServerPerClientThread(ServerWriter serverWriter, Socket s) {
        this.serverWriter = serverWriter;
        this.clientSocket = s;
    }

    /**
     * receives a request from client then sends an echo to the client
     * @param clientSocket the client socket
     **/
    public void run() {
        try {
            BufferedReader socIn = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

            while (true) {
                String line = socIn.readLine();
                System.out.println("Message reçu par le thread client");
                this.serverWriter.writeToAll(line, this.clientSocket);
            }

        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
    }

}

  