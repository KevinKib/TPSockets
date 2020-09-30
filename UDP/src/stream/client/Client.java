/***
 * Client
 * Example of a TCP client 
 * Date: 10/01/04
 * Authors:
 */
package stream.client;

import stream.Message;
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
    private static void startClient() throws IOException {

        BufferedReader stdIn = null;

        InetAddress groupAddr = InetAddress.getByName(Server.getAddress());
        Integer groupPort = Server.getPort();

        MulticastSocket s = new MulticastSocket(groupPort);
        s.joinGroup(groupAddr);

        stdIn = new BufferedReader(new InputStreamReader(System.in));

        ClientReaderThread clientReaderThread = new ClientReaderThread(s);
        clientReaderThread.start();


        // Write & send message
        String line;
        while (true) {
            line = stdIn.readLine();
            if (line.equals(".")) break;
            DatagramPacket dp = new DatagramPacket(
                    line.getBytes(),
                    line.length(),
                    groupAddr,
                    groupPort);
            s.send(dp);
        }

        stdIn.close();
    }

    /**
     * Gère le code attendant un message de l'utilisateur, et envoyant ce message au
     * serveur.
     * @param stdIn Reader qui attend une entrée clavier de l'utilisateur.
     * @param socOut Stream qui envoie le message écrit au serveur.
     * @throws IOException
     */
    private static void writeMessage(BufferedReader stdIn, InetAddress groupAddr, Integer groupPort) throws IOException {

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


