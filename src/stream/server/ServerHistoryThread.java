package stream.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 */
public class ServerHistoryThread extends Thread {

    private ServerWriter serverWriter;

    ServerHistoryThread(ServerWriter serverWriter) {
        this.serverWriter = serverWriter;
    }

    public void retrieveLog() {
        try {
            File file = new File(this.getLogFilename());
            Scanner reader = new Scanner(file);
            ArrayList<String> messageList = new ArrayList<>();

            while(reader.hasNextLine()) {
                messageList.add(reader.nextLine());
            }

            this.serverWriter.setHistory(messageList);

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
                for (String message: this.serverWriter.getTempHistory()) {
                    writer.write(message + '\n');
                }
                writer.close();
            }
        } catch (IOException e) {
            System.err.println(e);
        }

    }

    private String getLogFilename() {
        return "log.txt";
    }

}
