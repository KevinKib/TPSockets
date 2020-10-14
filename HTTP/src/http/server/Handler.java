package http.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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
            default:
                this.sendResponse(out, 405, null);
                break;
        }
    }

    private void handleGet(OutputStream out) {
        String baseUrl = "src/http/server/resources/";
        String relativeUrl = baseUrl+this.request.getUrl();
        String errorUrl = baseUrl+"404.html";
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
            else {
                throw new Exception("File not found");
            }
        } catch (Exception e) {
            try {
                byte[] errorContent = Files.readAllBytes(Paths.get(errorUrl));

                this.response.setHeader("Content-Type", Files.probeContentType(Paths.get(url)));
                this.response.setHeader("Server", "bot");
                this.response.setHeader("Content-Length", Integer.toString(errorContent.length));

                this.response.send(out, "404 Not Found", errorContent);

            } catch (IOException e2) {
                e2.printStackTrace();
            }
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

                this.sendResponse(out, 200, null);
            }
            else {
                this.sendResponse(out, 404, "Resource not found.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handlePost(OutputStream out, StringBuilder data) {
        if (!Files.exists(Paths.get(this.getFileUrl()))) {
            try {
                Files.createFile(Paths.get(this.getFileUrl()));
                FileWriter writer = new FileWriter(this.getFileUrl());
                writer.write("[]");
                writer.close();
            }
            catch (IOException e) {
                e.printStackTrace();
                this.sendResponse(out, 500, null);
                return;
            }
        }

        ArrayList<User> userList = this.getUsers();

        // Add JSON to file
        User newUser = this.gson.fromJson(data.toString(), User.class);

        // In case username is taken
        for (User u : userList) {
            if (u.username.equals(newUser.username)) {
                this.sendResponse(out, 422, "Le nom d'utilisateur est déjà utilisé.");
                return;
            }
        }

        userList.add(newUser);

        if (this.updateJson(userList)) {
            this.sendResponse(out, 201, null);
        }
        else {
            this.sendResponse(out, 500, null);
        }
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
                        this.updateJson(userList);
                        this.sendResponse(out,200, null);
                        return;
                    }
                    else {
                        // wrong password
                        this.sendResponse(out,401, "Le mot de passe entré est incorrect.");
                        return;
                    }
                }
            }

            // user not found
            this.sendResponse(out,404, "L'utilisateur recherché n'existe pas.");
        }
        else {
            this.sendResponse(out,404, "L'utilisateur recherché n'existe pas.");
        }
    }

    private void handleDelete(OutputStream out, StringBuilder data) {
        if (Files.exists(Paths.get(this.getFileUrl()))) {
            ArrayList<User> userList = this.getUsers();
            ArrayList<User> userList2 = new ArrayList<>();
            Collections.copy(userList,userList2);

            // Add JSON to file
            JsonObject object = this.gson.fromJson(data.toString(), JsonObject.class);
            userList.removeIf(u -> object.get("username").getAsString().equals(u.username));
//            boolean removed = false;
//
//            for (User u : userList2) {
//                System.out.println(u.username);
//                if (u.username.equals(object.get("username").getAsString())) {
//                    removed = true;
//                }
//            }
//
//            if (!removed) {
//                this.sendResponse(out,404, "L'utilisateur n'existe pas.");
//            }
//            else {
                if (this.updateJson(userList)) {
                    this.sendResponse(out,200, null);
                }
                else {
                    this.sendResponse(out,500, null);
                }
//            }
        }
        else {
            this.sendResponse(out,404, "L'utilisateur n'existe pas.");
        }
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
    private boolean updateJson(ArrayList<User> list) {
        boolean updated = false;
        try {
            FileWriter writer = new FileWriter(this.getFileUrl());
            String str = this.gson.toJson(list);
            writer.write(str);
            writer.close();
            updated = true;
        } catch(IOException e) {
            e.printStackTrace();
        }

        return updated;
    }

    /**
     * Retourne l'URL du fichier contenant la liste des utilisateurs créés.
     * @return Liste des utilisateurs.
     */
    private String getFileUrl() {
        return "./src/http/server/resources/users.json";
    }

    private StringBuilder dataToString(List<Integer> data) {
        StringBuilder userJson = new StringBuilder();
        for (Integer i : data) {
            userJson.append(Character.toChars(i));
        }
        return userJson;
    }

    private void sendResponse(OutputStream out, int code, String message) {
        byte[] content = null;
        if (message != null) {
            content = message.getBytes();
        }

        switch (code) {
            case 200:
                this.response.send(out, "200 OK", content);
                break;
            case 201:
                this.response.send(out, "201 Created", content);
                break;
            case 204:
                this.response.send(out, "204 No Content", content);
                break;
            case 400:
                this.response.send(out, "400 Bad Request", content);
                break;
            case 401:
                this.response.send(out, "401 Unauthorized", content);
                break;
            case 404:
                this.response.send(out, "404 Not Found", content);
                break;
            case 405:
                this.response.send(out, "405 Method Not Allowed", content);
                break;
            case 422:
                this.response.send(out, "422 Unprocessable entity", content);
                break;
            case 500:
                this.response.send(out, "500 Internal Server Error", content);
                break;

        }
    }


}
