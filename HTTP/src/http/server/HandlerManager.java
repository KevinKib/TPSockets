package http.server;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class HandlerManager {

    +
    private Request request;
    private ArrayList<Handler> handlers;
    private DefaultHandler defaultHandler;
    private OutputStream out;

    public HandlerManager(OutputStream out) {
        this.out = out;
        this.request = new Request();
        this.handlers = new ArrayList<>();
        this.defaultHandler = new DefaultHandler(this.out, this.request);

        this.initHandlers();
    }

    private void initHandlers() {
        this.handlers.add(new UserHandler(this.out, this.request));
    }

    /**
     * Interprête une ligne de la requête envoyée par le client.
     * @param line Ligne envoyée par le client.
     */
    protected void readLine(String line) {
        this.request.parseLine(line);
    }

    /**
     * Interprête la requête lue préalablement envoyée par un client.
     * @param out Stream de réponse au client.
     * @param data Données additionnelles envoyées par le client.
     */
    void handleRequest(OutputStream out, List<Integer> data) {
        boolean handled = false;
        this.out = out;

        for (Handler h : handlers) {
            if (this.request.getUrl().equals(h.getUrl())) {
                // handle
                handled = true;
                this.handle(h, data);
            }
        }

        if (!handled) {
            this.handle(defaultHandler, data);
        }
    }

    private void handle(Handler h, List<Integer> data) {
        StringBuilder strData = dataToString(data);

        switch(this.request.getHttpMethod()) {
            case "GET":
                h.handleGet();
                break;
            case "HEAD":
                h.handleHead();
                break;
            case "POST":
                h.handlePost(strData);
                break;
            case "PUT":
                h.handlePut(strData);
                break;
            case "DELETE":
                h.handleDelete(strData);
                break;
            default:
                h.sendResponse( 405, null);
                break;
        }
    }

    /**
     * Convertit une liste de caractères en string.
     * @param data Liste de caractères.
     * @return String.
     */
    static StringBuilder dataToString(List<Integer> data) {
        StringBuilder userJson = new StringBuilder();
        for (Integer i : data) {
            userJson.append(Character.toChars(i));
        }
        return userJson;
    }

}
