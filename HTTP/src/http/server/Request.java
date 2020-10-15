package http.server;

import java.util.HashMap;

/**
 * CLasse de données d'une requête HTTP.
 */
public class Request {

    /**
     * Booléen déterminant si la ligne de la requête actuellement lue
     * est la première ou non - utile pour le parsing.
     */
    private boolean firstLine;

    /**
     * Méthode HTTP de la requête.
     */
    private String httpMethod;

    /**
     * URL de la requête.
     */
    private String url;

    /**
     * Options de la requête, en brut en string.
     */
    private String options;

    /**
     * Options de la requête, dans un tableau à double entrées.
     */
    private HashMap<String, String> hashOpt;

    /**
     * Protocole de la requête (ex: HTTP/1.1)
     */
    private String protocol;

    /**
     * Headers de la requête.
     */
    private HashMap<String, String> headers;

    /**
     * Constructeur.
     */
    public Request() {
        this.firstLine = true;
        this.httpMethod = "";
        this.url = "";
        this.options = "";
        this.protocol = "";
        this.headers = new HashMap<>();
        this.hashOpt = new HashMap<>();
    }

    /**
     * Interprête une ligne de la requête envoyée par le client.
     * @param line String Ligne.
     */
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

            System.out.println(httpMethod + ' ' + url + ' ' + options);

            if (this.url.equals("/")) {
                this.url = WebServer.getIndexUrl();
            }

            this.firstLine = false;
        } else {
            if (line != null && !line.equals("")) {
                String[] req = line.split(":", 2);
                this.headers.put(req[0], req[1].trim());
            }
        }
    }

    /**
     * Interprête les options entrées en brut et les stocke dans un tableau à double entrées.
     */
    void parseOptions() {
        String[] split = this.options.split("&");

        for (String s : split) {
            String[] field = s.split("=");
            this.hashOpt.put(field[0], field[1]);
        }
    }

    /**
     * Getter sur la méthode HTTP.
     * @return Méthode HTTP de la requête.
     */
    String getHttpMethod() {
        return this.httpMethod;
    }

    /**
     * Getter sur l'URL de la requête.
     * @return URL de la requête.
     */
    String getUrl() {
        return this.url;
    }

    /**
     * Getter sur les options de la requête.
     * @return Options de la requête.
     */
    HashMap<String, String> getOptions() { return this.hashOpt; }

    /**
     * Getter sur le protocole de la requête.
     * @return Protocole de la requête.
     */
    String getProtocol() { return this.protocol; }


}
