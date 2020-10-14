package http.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Handler {

    private Request request;
    private Response response;
    private Gson gson;
    private OutputStream out;

    private String url;

    /**
     * Constructeur.
     */
    public Handler(String url, OutputStream out, Request request) {
        this.url = url;
        this.request = request;
        this.response = new Response();
        this.gson = new Gson();
        this.out = out;
    }

    /**
     * Interprête une requête GET du client.
     */
    void handleGet() {
        this.sendResponse(400, null);
    }

    /**
     * Interprête une requête HEAD du client.
     */
    void handleHead() {
        this.sendResponse(400, null);
    }

    /**
     * Interprête une requête POST du client.
     * @param data Données additionnelles du client.
     */
    void handlePost(StringBuilder data) {
        this.sendResponse(400, null);
    }

    /**
     * Interprête une requête PUT du client.
     * @param data Données additionnelles du client.
     */
    void handlePut(StringBuilder data) {
        this.sendResponse(400, null);
    }

    /**
     * Interprête une requête DELETE du client.
     * @param data Données additionnelles du client.
     */
    void handleDelete(StringBuilder data) {
        this.sendResponse(400, null);
    }

    /**
     * Envoie une réponse HTTP au client.
     * @param code Code de la réponse HTTP.
     * @param message Message de la réponse.
     */
    void sendResponse(int code, String message) {
        byte[] content = null;
        if (message != null) {
            content = message.getBytes();
        }

        switch (code) {
            case 200:
                this.response.send(out, "200 OK", content);
                break;
            case 201:
                this.response.send(out, "201 Created", content);
                break;
            case 204:
                this.response.send(out, "204 No Content", content);
                break;
            case 400:
                this.response.send(out, "400 Bad Request", content);
                break;
            case 401:
                this.response.send(out, "401 Unauthorized", content);
                break;
            case 404:
                this.response.send(out, "404 Not Found", content);
                break;
            case 405:
                this.response.send(out, "405 Method Not Allowed", content);
                break;
            case 422:
                this.response.send(out, "422 Unprocessable entity", content);
                break;
            case 500:
                this.response.send(out, "500 Internal Server Error", content);
                break;

        }
    }

    /**
     * Vérifie si les paramètres nécessaires de la requêtes ont tous été entrés;
     * retourne false si ce n'est pas le cas.
     * @param args
     * @return
     */
    boolean areRequestParametersValid(String... args) {
        for (String arg : args) {
            if (!this.request.getOptions().containsKey(arg)) {
                this.sendResponse(422, "Les paramètres sont invalides.");
                return false;
            }
        }
        return true;
    }

    OutputStream getOutputStream() {
        return this.out;
    }

    String getUrl() {
        return this.url;
    }

    Request getRequest() {
        return this.request;
    }

}
