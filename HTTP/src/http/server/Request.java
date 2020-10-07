package http.server;

import java.util.HashMap;

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

    void parseLine(String line) {
        if (this.firstLine) {
            String[] req = line.split(" ");
            this.httpMethod = req[0];
            this.url = req[1];

            if (this.url.equals("/")) {
                this.url = WebServer.getIndexUrl();
            }
            this.content = req[2];

            this.firstLine = false;
        } else {
            if (!line.equals("")) {
                String[] req = line.split(":", 2);
                this.headers.put(req[0], req[1].trim());
            }
        }
    }

    String getHttpMethod() {
        return this.httpMethod;
    }

    String getUrl() {
        return this.url;
    }


}
