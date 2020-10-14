/***
 * Client
 * Example of a TCP client 
 * Date: 10/01/04
 * Authors:
 */
package stream.client;

import stream.Message;
import stream.server.Server;

import javax.swing.*;
import java.io.*;
import java.net.*;

/**
 * Classe gérant le client se connectant au serveur.
 */
public class Client {

    Socket socket;
    PrintStream socOut;
    BufferedReader stdIn;
    BufferedReader socIn;
    ClientReaderThread clientReaderThread;

    public Client() {
        this.socket = null;
        this.socOut = null;
        this.stdIn = null;
        this.socIn = null;
        this.clientReaderThread = null;
    }

    /**
     * Démarrage du client.
     * Cette méthode crée le socket du client, et instancie un thread de lecture
     * qui attendra le message du serveur.
     * Egalement, cette méthode gère la connexion au serveur.
     * Enfin, cette méthode initialise les différents objets nécessaires pour communiquer
     * avec le serveur et l'utilisateur derrière son clavier.
     * @param chatBox chat de l'ihm.
     * @throws IOException
     **/
    public void startClient(JTextArea chatBox) throws IOException {

        String[] args = {
                Server.getAddress(),
                Server.getPort(),
        };

        if (args.length != 2) {
            System.out.println("Usage: java Client <EchoServer host> <EchoServer port>");
            System.exit(1);
        }

        try {
            // creation socket ==> connexion
            this.socket = new Socket(args[0], new Integer(args[1]).intValue());
            this.socIn = new BufferedReader(
                    new InputStreamReader(this.socket.getInputStream()));
            this.socOut = new PrintStream(this.socket.getOutputStream());
            this.stdIn = new BufferedReader(new InputStreamReader(System.in));

            this.clientReaderThread = new ClientReaderThread(this.socket, chatBox);
            this.clientReaderThread.start();
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
     * Gère le code attendant un message de l'utilisateur, et envoyant ce message au
     * serveur.
     * @param message Message a envoyer au serveur.
     * @throws IOException
     */
    public void closeClient() throws IOException {
        if(this.clientReaderThread != null)
        {
            this.clientReaderThread.interrupt();
        }
        this.socOut.close();
        this.socIn.close();
        this.stdIn.close();
        this.socket.close();
    }

    /**
     * Envoie un message au serveur
     * @param message Message a envoyer au serveur.
     * @throws IOException
     */
    public void writeMessage(String message) throws IOException {

        if (message.equals("."))
            return;
        this.socOut.println(message);
    }
}


