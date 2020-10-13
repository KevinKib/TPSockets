package http.server;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Handler {

    private Request request;
    private Response response;

    public Handler() {
        this.request = new Request();
        this.response = new Response();
    }

    protected void readLine(String line) {
        this.request.parseLine(line);
    }

    protected void handleRequest(OutputStream out, List<Integer> data) {
        switch(this.request.getHttpMethod()) {
            case "GET":
                this.handleGet(out);
                break;
            case "HEAD":
                this.handleHead(out);
                break;
            case "POST":
                this.handlePost(out, data);
                break;
            case "PUT":
                this.handlePut(out);
                break;
            case "DELETE":
                this.handleDelete(out);
                break;
            case "":
                break;
        }
    }

    private void handleGet(OutputStream out) {
        String baseUrl = "src/http/server/resources/";
        String relativeUrl = baseUrl+this.request.getUrl();
        File f = new File(relativeUrl);

        String url = f.getAbsolutePath();

        try {
            if (Files.exists(Paths.get(url))) {
                byte[] content = Files.readAllBytes(Paths.get(url));

                this.response.setHeader("Content-Type", Files.probeContentType(Paths.get(url)));
                this.response.setHeader("Server", "bot");
                this.response.setHeader("Content-Length", Integer.toString(content.length));

                this.response.send(out, "200 OK", content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleHead(OutputStream out) {
        String baseUrl = "src/http/server/resources/";
        String relativeUrl = baseUrl+this.request.getUrl();
        File f = new File(relativeUrl);

        String url = f.getAbsolutePath();

        try {
            if (Files.exists(Paths.get(url))) {
                byte[] content = Files.readAllBytes(Paths.get(url));

                this.response.setHeader("Content-Type", Files.probeContentType(Paths.get(url)));
                this.response.setHeader("Server", "bot");
                this.response.setHeader("Content-Length", Integer.toString(content.length));

                this.response.send(out, "200 OK", null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handlePost(OutputStream out, List<Integer> data) {
        StringBuilder userJson = new StringBuilder();
        for (Integer i : data) {
            userJson.append(Character.toChars(i));
        }

        try {
            File f = new File("./src/http/server/users.json");
            if (!f.exists()) {
                f.createNewFile();
            }

            // Add JSON to file
            Gson g = new Gson();
            User u = g.fromJson(userJson.toString(), User.class);

        } catch(IOException e) {

        }

        this.response.setHeader("Content-Type", "");
        this.response.setHeader("Content-Length", Integer.toString(data.size()));
        this.response.send(out, "200 OK", null);

    }

    private void handlePut(OutputStream out) {

    }

    private void handleDelete(OutputStream out) {

    }

    private User[] getUsers() {
//        User[]

        try {
            File f = new File("./src/http/server/users.json");
            if (!f.exists()) {
                f.createNewFile();
            }

        } catch(IOException e) {

        }

        return null;
    }


}
