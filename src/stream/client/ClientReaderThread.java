package stream.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class ClientReaderThread extends Thread {

    private BufferedReader socIn;

    ClientReaderThread(Socket socket) {
        try {
            this.socIn = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    /**
     * Attend les messages envoyés par les autres clients,
     * et les affiche ensuite à l'écran.
     */
    public void run() {
        try {
            while(true) {
                String message = this.socIn.readLine();
                System.out.println(message);
            }
        } catch (IOException e) {
            System.err.println(e);
        }

    }

}
