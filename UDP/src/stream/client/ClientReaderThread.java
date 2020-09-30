package stream.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

/**
 * Thread ayant pour rôle de lire les messages envoyés par le serveur.
 */
public class ClientReaderThread extends Thread {

    /**
     * Reader du socket recevant les messages envoyés par le serveur (par les autres clients).
     */
    private MulticastSocket socket;

    ClientReaderThread(MulticastSocket socket) {
        this.socket = socket;
    }

    /**
     * Attend les messages envoyés par les autres clients,
     * et les affiche ensuite à l'écran.
     */
    public void run() {
        try {
            while(true) {
                byte[] buf = new byte[1000];
                DatagramPacket response = new DatagramPacket(buf, buf.length);
                this.socket.receive(response);

                String message = new String(buf, 0, response.getLength());
                System.out.println(message);
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

}
