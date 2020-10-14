package http.server;

import java.util.HashMap;

public class Request {

    private boolean firstLine;
    private String httpMethod;
    private String url;
    private String options;
    private HashMap<String, String> hashOpt;
    private String protocol;
    private String content;
    private HashMap<String, String> headers;
    private int status;

    public Request() {
        this.firstLine = true;
        this.httpMethod = "";
        this.url = "";
        this.options = "";
        this.protocol = "";
        this.content = "";
        this.headers = new HashMap<>();
        this.hashOpt = new HashMap<>();
    }

    void parseLine(String line) {
        if (this.firstLine && line != null) {
            String[] sep = line.split("\\?");
            String[] req = sep[0].split(" ");
            this.httpMethod = req[0];
            this.url = req[1];
            if (sep.length > 1) {
                this.options = sep[1].split(" ")[0];
                this.protocol = sep[1].split(" ")[1];
                this.parseOptions();
            }
            else {
                this.protocol = req[2];
            }

            System.out.println(httpMethod);
            System.out.println(url);
            System.out.println(options);
            System.out.println(protocol);

            if (this.url.equals("/")) {
                this.url = WebServer.getIndexUrl();
            }

//            this.content = req[2];

            this.firstLine = false;
        } else {
            if (line != null && !line.equals("")) {
                String[] req = line.split(":", 2);
                this.headers.put(req[0], req[1].trim());
            }
        }
    }

    void parseOptions() {
        String[] split = this.options.split("&");

        for (String s : split) {
            String[] field = s.split("=");
            this.hashOpt.put(field[0], field[1]);
        }
    }

    String getHttpMethod() {
        return this.httpMethod;
    }

    String getUrl() {
        return this.url;
    }

    HashMap<String, String> getOptions() { return this.hashOpt; }

    String getProtocol() { return this.protocol; }


}
