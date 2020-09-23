package stream.server;

import java.io.IOException;
import java.net.*;

/**
 * Classe gérant le serveur multithread TCP.
 */
public class Server {

    private static ServerWriter serverWriter;

    /**
     * Retourne le port qui sera utilisé par les clients pour se connecter.
     * @return String correspondant au port.
     */
    public static String getPort() {
        return "61709";
    }

    /**
     * Retourne l'adresse IP qui sera utilisée par les clients pour se connecter.
     * @return String correspondant à l'adresse IP.
     */
    public static String getAddress() {
        return "localhost";
    }

    /**
     * Méthode démarrant le serveur.
     **/
    private static void startServer(String args[]) {
        /*
         * Cette méthode crée un ServerSocket pour que tous les
         * clients puissent se connecter dessus.
         * Elle crée également le thread d'écriture qui sera chargé de transmettre
         * les messages à tous les clients.
         */
        ServerSocket listenSocket;
        serverWriter = new ServerWriter();

        if (args.length != 1) {
            System.out.println("Usage: java EchoServer <EchoServer port>");
            System.exit(1);
        }
        try {
            listenSocket = new ServerSocket(Integer.parseInt(args[0])); //port
            System.out.println("Server ready...");
            waitForConnexions(listenSocket);
        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
    }

    /**
     * Gère les différentes connexions au serveur.
     * A chaque connexion, un nouveau thread est instancié pour gérer les
     * messages du nouveau client.
     * @param listenSocket Socket du serveur.
     * @throws IOException
     */
    private static void waitForConnexions(ServerSocket listenSocket) throws IOException {
        while (true) {
            Socket clientSocket = listenSocket.accept();
            serverWriter.addSocket(clientSocket);
            System.out.println("Connexion from:" + clientSocket.getInetAddress());
            ServerPerClientThread ct = new ServerPerClientThread(serverWriter, clientSocket);
            ct.start();
        }
    }

    /**
     * Méthode de lancement du programme associé au serveur TCP.
     * @param args
     */
    public static void main(String args[]) {
        System.out.println("Multithreaded server start");

        String[] newArgs = {getPort()};
        startServer(newArgs);
    }
}
