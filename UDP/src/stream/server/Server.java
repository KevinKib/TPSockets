package stream.server;

import java.io.IOException;
import java.net.*;

/**
 * Classe gérant le serveur multithread TCP.
 */
public class Server {

    private static ServerWriter serverWriter;
    private static ServerHistoryThread serverHistoryThread;

    /**
     * Retourne le port qui sera utilisé par les clients pour se connecter.
     * @return String correspondant au port.
     */
    public static Integer getPort() {
        return 6789;
        // 61709
    }

    /**
     * Retourne l'adresse IP qui sera utilisée par les clients pour se connecter.
     * @return String correspondant à l'adresse IP.
     */
    public static String getAddress() {
        return "228.5.6.7";
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
        serverHistoryThread = new ServerHistoryThread();
        Runtime.getRuntime().addShutdownHook(serverHistoryThread);
        serverHistoryThread.retrieveLog();

        ServerSocket listenSocket;
        serverWriter = new ServerWriter(serverHistoryThread);

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

        startServer(args);
    }
}
