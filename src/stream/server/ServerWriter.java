package stream.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;

public class ServerWriter extends Thread  {

    private ArrayList<Socket> socketList;
    private ArrayList<String> tempHistory;

    ServerWriter() {
        this.socketList = new ArrayList<>();
        this.tempHistory = new ArrayList<>();
    }

    void addSocket(Socket socket) {
        this.socketList.add(socket);
    }

    void writeToAll(String line, Socket writerSocket) {
        this.tempHistory.add(line);

        for (Socket socket : this.socketList) {
            try {
                if (writerSocket != socket) {
                    PrintStream socOut = new PrintStream(socket.getOutputStream());
                    socOut.println(line);
                }
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

}
