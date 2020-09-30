package stream.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 *
 */
public class ServerHistoryThread extends Thread {

    /**
     * Historique des messages envoyés depuis l'ouverture du serveur.
     */
    private ArrayList<String> history;

    ServerHistoryThread() {
        this.history = new ArrayList<>();
    }

    public void retrieveLog() {
        try {
            File file = new File(this.getLogFilename());
            Scanner reader = new Scanner(file);

            while(reader.hasNextLine()) {
                history.add(reader.nextLine());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        try {
            File file = new File(this.getLogFilename());
            if (!file.exists()) {
                file.createNewFile();
            }

            if (file.exists()) {
                FileWriter writer = new FileWriter(this.getLogFilename());
                for (String message: this.history) {
                    writer.write(message + '\n');
                }
                writer.close();
            }
        } catch (IOException e) {
            System.err.println(e);
        }

    }

    /**
     *
     * @return
     */
    List<String> getHistory() {
        return Collections.unmodifiableList(this.history);
    }

    void addMessageToHistory(String message) {
        this.history.add(message);
    }

    private String getLogFilename() {
        return "log.txt";
    }
}
