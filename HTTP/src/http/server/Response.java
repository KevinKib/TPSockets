package http.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe gérant les données d'une réponse HTTP.
 */
public class Response {

    /**
     * Headers de la réponse.
     */
    private HashMap<String, String> headers;

    /**
     * Constructeur.
     */
    public Response() {
        this.headers = new HashMap<>();
    }

    /**
     * Ajoute un nouveau header à la réponse.
     * @param key Nom du header.
     * @param content Contenu du header.
     */
    void setHeader(String key, String content) {
        this.headers.put(key, content);
    }

    /**
     * Envoie la réponse sur l'OutputStream du client.
     * @param out OutputStream du client.
     * @param status Statut de la réponse (code HTTP).
     * @param content Contenu message de la réponse.
     */
    void send(OutputStream out, String status, byte[] content) {

        PrintWriter writer = new PrintWriter(out);

        // Send the HTML page
        try {
            // Send the response
            // Send the headers
            out.write(("HTTP/1.0 "+status+"\r\n").getBytes());

            for (Map.Entry<String, String> header : headers.entrySet()) {
                out.write((header.getKey() + ": "+header.getValue()+"\r\n").getBytes());
            }

            // this blank line signals the end of the headers
            out.write("\r\n".getBytes());

            if (content != null) {
                out.write(content);
            }
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
        writer.flush();
    }

}
