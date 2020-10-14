package stream.server;

import java.io.*;
import java.net.*;

/**
 * Thread dont le rôle est de gérer les actions liées à un client particulier.
 * Il lit les messages envoyés par ce client et les redirige au ServerWriter,
 * et gère l'historique des messages pour le client lorsqu'il se connecte.
 */
public class ServerPerClientThread extends Thread {

    private ServerWriter serverWriter;

    /**
     * Socket permettant de communiquer avec le client.
     */
    private Socket clientSocket;

    private volatile boolean isRunning;

    ServerPerClientThread(ServerWriter serverWriter, Socket s) {
        this.serverWriter = serverWriter;
        this.clientSocket = s;
    }

    /**
     * Reçoit les messages écrits par le client et se charge de les rediriger
     * au ServerWriter afin qu'il puisse les envoyer à tous les autres clients.
     * Dès l'initialisation, demande au ServerWriter d'envoyer l'historique
     * des messages.
     **/
    public void run() {
        try {
            BufferedReader socIn = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            this.serverWriter.writeHistory(clientSocket);
            while (!Thread.currentThread().isInterrupted()) {
                String line = socIn.readLine();
                if (line != null) {
//                    System.out.println("is connected ? : " + clientSocket.isConnected());
                    this.serverWriter.writeToAll(line, this.clientSocket);
                }
            }

        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
    }

}

  