package stream.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Gère l'envoi de message aux différents clients connectés.
 * Gère également l'envoi de l'historique à un client donné.
 */
public class ServerWriter {

    /**
     * Liste des sockets clients connectés au serveur.
     */
    private ArrayList<Socket> socketList;

    private ServerHistoryThread historyThread;

    ServerWriter(ServerHistoryThread historyThread) {
        this.socketList = new ArrayList<>();
        this.historyThread = historyThread;
    }

    /**
     * Ajoute un nouveau socket client à la liste, lorsqu'un client se connecte.
     * @param socket Socket client à ajouter.
     */
    void addSocket(Socket socket) {
        this.socketList.add(socket);
    }

    /**
     * Transfère le message d'un client à tous les autres clients, sauf le client
     * ayant envoyé le message.
     * @param line Message à transférer.
     * @param writerSocket TODO: Ne plus passer le socket en paramètre
     */
    void writeToAll(String line, Socket writerSocket) {
        for (Socket socket : this.socketList) {
            try {
                PrintStream socOut = new PrintStream(socket.getOutputStream());
                socOut.println(line);
            } catch (IOException e) {
                System.err.println(e);
            }
        }

        this.historyThread.addMessageToHistory(line);
    }

    /**
     * Transfère l'historique des messages déjà envoyés à un client venant de se connecter.
     * @param socket Socket client devant recevoir l'historique.
     */
    void writeHistory(Socket socket) {
        try {
            PrintStream socOut = new PrintStream(socket.getOutputStream());
            for (String message : this.historyThread.getHistory()) {
                socOut.println(message);
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
