package http.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

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
        StringBuilder strData = dataToString(data);

        switch(this.request.getHttpMethod()) {
            case "GET":
                this.handleGet(out);
                break;
            case "HEAD":
                this.handleHead(out);
                break;
            case "POST":
                this.handlePost(out, strData);
                break;
            case "PUT":
                this.handlePut(out, strData);
                break;
            case "DELETE":
                this.handleDelete(out, strData);
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

    private void handlePost(OutputStream out, StringBuilder data) {
        if (Files.exists(Paths.get(this.getFileUrl()))) {
            ArrayList<User> userList = this.getUsers();

            // Add JSON to file
            User u = this.gson.fromJson(data.toString(), User.class);
            userList.add(u);

            this.updateJson(userList);
        }

        this.response.setHeader("Content-Type", "");
        this.response.setHeader("Content-Length", Integer.toString(data.length()));
        this.response.send(out, "200 OK", null);
    }

    private void handlePut(OutputStream out, StringBuilder data) {
        if (Files.exists(Paths.get(this.getFileUrl()))) {
            ArrayList<User> userList = this.getUsers();

            // Add JSON to file
            JsonObject object = this.gson.fromJson(data.toString(), JsonObject.class);

            for (User u : userList) {
                if (object.get("username").getAsString().equals(u.username)) {
                    if (object.get("oldPassword").getAsString().equals(u.password)) {
                        u.password = object.get("newPassword").getAsString();
                        System.out.println("1");
                        break;
                        // TODO : dire que tout s'est bien passé
                    }
                    else {
                        System.out.println("2");
                        // TODO : mauvais mdp
                    }
                }
                else {
                    System.out.println("3");
                    // TODO : user not found
                }
            }

            System.out.println(userList.get(0).password);
            this.updateJson(userList);

        }

        this.response.setHeader("Content-Type", "");
        this.response.setHeader("Content-Length", Integer.toString(data.length()));
        this.response.send(out, "200 OK", null);
    }

    private void handleDelete(OutputStream out, StringBuilder data) {
        if (Files.exists(Paths.get(this.getFileUrl()))) {
            ArrayList<User> userList = this.getUsers();

            // Add JSON to file
            JsonObject object = this.gson.fromJson(data.toString(), JsonObject.class);
            userList.removeIf(u -> object.get("username").getAsString().equals(u.username));

            this.updateJson(userList);
        }

        this.response.setHeader("Content-Type", "");
        this.response.setHeader("Content-Length", Integer.toString(data.length()));
        this.response.send(out, "200 OK", null);
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

    private StringBuilder dataToString(List<Integer> data) {
        StringBuilder userJson = new StringBuilder();
        for (Integer i : data) {
            userJson.append(Character.toChars(i));
        }
        return userJson;
    }


}
