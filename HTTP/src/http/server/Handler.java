package http.server;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Handler {

    private Request request;
    private Response response;
    private Gson gson;

    public Handler() {
        this.request = new Request();
        this.response = new Response();
        this.gson = new Gson();
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
        String url = "./src/http/server/users.json";
        StringBuilder userJson = new StringBuilder();
        for (Integer i : data) {
            userJson.append(Character.toChars(i));
        }

        if (Files.exists(Paths.get(url))) {
            ArrayList<User> userList = this.getUsers();

            // Add JSON to file
            User u = this.gson.fromJson(userJson.toString(), User.class);
            userList.add(u);

            this.updateJson(userList);
        }

        this.response.setHeader("Content-Type", "");
        this.response.setHeader("Content-Length", Integer.toString(data.size()));
        this.response.send(out, "200 OK", null);

    }

    private void handlePut(OutputStream out) {

    }

    private void handleDelete(OutputStream out) {

    }

    /**
     * Retourne une liste d'utilisateurs dans users.json.
     * @return Liste d'utilisateurs.
     */
    private ArrayList<User> getUsers() {
        String url = this.getFileUrl();
        String str = "";
        ArrayList<User> userList = new ArrayList<>();
        try {
            if (Files.exists(Paths.get(url))) {
                str = new String(Files.readAllBytes(Paths.get(url)));
                User[] list = this.gson.fromJson(str, User[].class);
                userList = new ArrayList<>(Arrays.asList(list));
                System.out.println(userList.get(0).username);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

        return userList;
    }

    /**
     * Retourne une liste d'utilisateurs dans users.json.
     * @return Liste d'utilisateurs.
     */
    private void updateJson(ArrayList<User> list) {
        ArrayList<User> userList = new ArrayList<>();
        try {
            FileWriter writer = new FileWriter(this.getFileUrl());
            String str = this.gson.toJson(list);
            writer.write(str);
            writer.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private String getFileUrl() {
        return "./src/http/server/users.json";
    }


}
