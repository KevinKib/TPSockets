/***
 * Client
 * Example of a TCP client 
 * Date: 10/01/04
 * Authors:
 */
package stream.client;

import stream.server.Server;

import java.io.*;
import java.net.*;

/**
 * Classe gérant le client se connectant au serveur.
 */
public class Client {


    /**
     * Démarrage du client.
     * Cette méthode crée le socket du client, et instancie un thread de lecture
     * qui attendra le message du serveur.
     * Egalement, cette méthode gère la connexion au serveur.
     * Enfin, cette méthode initialise les différents objets nécessaires pour communiquer
     * avec le serveur et l'utilisateur derrière son clavier.
     **/
    public static void startClient() throws IOException {

        Socket echoSocket = null;
        PrintStream socOut = null;
        BufferedReader stdIn = null;
        BufferedReader socIn = null;

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
            echoSocket = new Socket(args[0], new Integer(args[1]).intValue());
            socIn = new BufferedReader(
                    new InputStreamReader(echoSocket.getInputStream()));
            socOut = new PrintStream(echoSocket.getOutputStream());
            stdIn = new BufferedReader(new InputStreamReader(System.in));

            ClientReaderThread clientReaderThread = new ClientReaderThread(echoSocket);
            clientReaderThread.start();
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host:" + args[0]);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                    + "the connection to:" + args[0]);
            System.exit(1);
        }

        writeMessage(stdIn, socOut);

        socOut.close();
        socIn.close();
        stdIn.close();
        echoSocket.close();
    }

    /**
     * Gère le code attendant un message de l'utilisateur, et envoyant ce message au
     * serveur.
     * @param stdIn Reader qui attend une entrée clavier de l'utilisateur.
     * @param socOut Stream qui envoie le message écrit au serveur.
     * @throws IOException
     */
    private static void writeMessage(BufferedReader stdIn, PrintStream socOut) throws IOException {
        String line;

        while (true) {
            line = stdIn.readLine();
            if (line.equals(".")) break;
            socOut.println(line);
        }
    }

    /**
     * Méthode de démarrage du programme lié au client.
     **/
    public static void main(String args[]) {
        System.out.println("Client start");

        try {
            startClient();
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}


