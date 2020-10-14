package http.server;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class DefaultHandler extends Handler {

    private Response response;

    /**
     * Constructeur.
     */
    public DefaultHandler(OutputStream out, Request request) {
        super("/", out, request);

        this.response = new Response();
    }

    /**
     * Interprête une requête GET du client.
     */
    void handleGet() {
        String baseUrl = "src/http/server/resources/";
        String relativeUrl = baseUrl+this.getRequest().getUrl();
        String errorUrl = baseUrl+"404.html";
        File f = new File(relativeUrl);

        String url = f.getAbsolutePath();

        try {
            if (Files.exists(Paths.get(url))) {
                byte[] content = Files.readAllBytes(Paths.get(url));

                this.response.setHeader("Content-Type", Files.probeContentType(Paths.get(url)));
                this.response.setHeader("Server", "bot");
                this.response.setHeader("Content-Length", Integer.toString(content.length));

                this.response.send(getOutputStream(), "200 OK", content);
            }
            else {
                throw new Exception("File not found");
            }
        } catch (Exception e) {
            try {
                byte[] errorContent = Files.readAllBytes(Paths.get(errorUrl));

                this.response.setHeader("Content-Type", Files.probeContentType(Paths.get(url)));
                this.response.setHeader("Server", "bot");
                this.response.setHeader("Content-Length", Integer.toString(errorContent.length));

                this.response.send(getOutputStream(), "404 Not Found", errorContent);

            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    /**
     * Interprête une requête HEAD du client.
     */
    void handleHead() {
        String baseUrl = "src/http/server/resources/";
        String relativeUrl = baseUrl+this.getRequest().getUrl();
        File f = new File(relativeUrl);

        String url = f.getAbsolutePath();

        try {
            if (Files.exists(Paths.get(url))) {
                byte[] content = Files.readAllBytes(Paths.get(url));

                this.response.setHeader("Content-Type", Files.probeContentType(Paths.get(url)));
                this.response.setHeader("Server", "bot");
                this.response.setHeader("Content-Length", Integer.toString(content.length));

                this.sendResponse(200, null);
            }
            else {
                this.sendResponse(404, "Resource not found.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
