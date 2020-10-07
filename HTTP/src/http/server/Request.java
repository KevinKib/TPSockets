package http.server;

import java.util.HashMap;
import java.io.PrintWriter;

public class Request {

    private boolean firstLine;
    private String httpMethod;
    private String url;
    private String content;
    private HashMap<String, String> headers;
    private int status;

    public Request() {
        this.firstLine = true;
        this.httpMethod = "";
        this.url = "";
        this.content = "";
        this.headers = new HashMap<>();
    }

    protected void handleLine(String line) {
//        System.out.println(line);

        if (this.firstLine) {
            String[] req = line.split(" ");
            this.httpMethod = req[0];
            this.url = req[1];
            this.content = req[2];

            this.firstLine = false;
        }
        else {
            if (!line.equals("")) {
                String[] req = line.split(":", 2);
                this.headers.put(req[0], req[1].trim());
            }

        }
    }

    protected void handleRequest(PrintWriter out) {
        switch(this.httpMethod) {
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

    private void handleGet(PrintWriter out) {
        System.out.println("handleGet");

        // Send the response
        // Send the headers
        out.println("HTTP/1.0 200 OK");
        out.println("Content-Type: text/html");
        out.println("Server: Bot");
        // this blank line signals the end of the headers
        out.println("");
        // Send the HTML page
        out.println("<H1>Welcome to the Ultra Mini-WebServer</H1>");
        out.flush();
    }

    private void handleHead(PrintWriter out) {
        System.out.println("handleHead");
    }

    private void handlePost(PrintWriter out) {
        System.out.println("handlePost");

    }

    private void handlePut(PrintWriter out) {
        System.out.println("handlePut");

    }

    private void handleDelete(PrintWriter out) {
        System.out.println("handleDelete");
    }

}
