///A Simple Web Server (WebServer.java)

package http.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Example program from Chapter 1 Programming Spiders, Bots and Aggregators in
 * Java Copyright 2001 by Jeff Heaton
 * <p>
 * WebServer is a very simple web-server. Any request is responded with a very
 * simple web-page.
 *
 * @author Jeff Heaton
 * @version 1.0
 */
public class WebServer {

    /**
     * WebServer constructor.
     */
    protected void start() {
        ServerSocket s;

        System.out.println("Webserver starting up on port "+getPing());
        System.out.println("(press ctrl-c to exit)");
        try {
            // create the main server socket
            s = new ServerSocket(getPing());
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return;
        }

        System.out.println("Waiting for connection");
        for (; ; ) {
            try {
                // wait for a connection
                Socket remote = s.accept();
                // remote is now the connected socket
//                System.out.println("Connection, sending data.");
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        remote.getInputStream()));

                HandlerManager handlerManager = new HandlerManager(remote.getOutputStream());

                // read the data sent. We basically ignore it,
                // stop reading once a blank line is hit. This
                // blank line signals the end of the client HTTP
                // headers.

                String str = ".";
                while (str != null && !str.equals("")) {
                    str = in.readLine();
                    handlerManager.readLine(str);
                }

                List<Integer> data = new ArrayList<>();
                // on continue de lire si la requete est un POST
                while (in.ready()) {
                    data.add(in.read());
                }

                handlerManager.handleRequest(remote.getOutputStream(), data);
                remote.close();
            } catch (Exception e) {
                System.out.println("Error: " + e);
                e.printStackTrace();
            }
        }
    }

    /**
     * Retourne le ping de l'application pour se connecter.
     * @return Ping.
     */
    protected int getPing() {
        return 3000;
    }

    /**
     * Retourne l'URL de la page d'accueil du serveur.
     * @return Page d'accueil du serveur.
     */
    static String getIndexUrl() {
        return "/index.html";
    }

    /**
     * Start the application.
     *
     * @param args Command line parameters are not used.
     */
    public static void main(String args[]) {
        WebServer ws = new WebServer();
        ws.start();
    }
}

