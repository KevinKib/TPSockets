package http.server;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

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

    protected void handleRequest(OutputStream out) {
        switch(this.request.getHttpMethod()) {
            case "GET":
                this.handleGet(out);
                break;
            case "HEAD":
                this.handleHead(out);
                break;
            case "POST":
                this.handlePost(out);
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
        System.out.println("handleGet");

        String baseUrl = "src/http/server/resources/";
        String relativeUrl = baseUrl+this.request.getUrl();
        File f = new File(relativeUrl);

        String url = f.getAbsolutePath();
        System.out.println(url);

        try {
            if (Files.exists(Paths.get(url))) {
                System.out.println("Found file");
                byte[] content = Files.readAllBytes(Paths.get(url));

                this.response.send(out,
                        "200 OK",
                        content,
                        Files.probeContentType(Paths.get(url)),
                        "bot"
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleHead(OutputStream out) {
        System.out.println("handleHead");
    }

    private void handlePost(OutputStream out) {
        System.out.println("handlePost");

    }

    private void handlePut(OutputStream out) {
        System.out.println("handlePut");

    }

    private void handleDelete(OutputStream out) {
        System.out.println("handleDelete");
    }


}
