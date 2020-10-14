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
    private OutputStream out;

    public Handler() {
        this.request = new Request();
        this.response = new Response();
        this.gson = new Gson();
    }

    protected void readLine(String line) {
        System.out.println(line);
        this.request.parseLine(line);
    }

    protected void handleRequest(OutputStream out, List<Integer> data) {
        StringBuilder strData = dataToString(data);
        this.out = out;

        switch(this.request.getHttpMethod()) {
            case "GET":
                this.handleGet();
                break;
            case "HEAD":
                this.handleHead();
                break;
            case "POST":
                this.handlePost(strData);
                break;
            case "PUT":
                this.handlePut(strData);
                break;
            case "DELETE":
                this.handleDelete(strData);
                break;
            default:
                this.sendResponse( 405, null);
                break;
        }
    }

    private void handleGet() {
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

    private void handleHead() {
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

                this.sendResponse(200, null);
            }
            else {
                this.sendResponse(404, "Resource not found.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handlePost(StringBuilder data) {
        if (!areRequestParametersValid("username", "password")) return;
        HashMap<String, String> opt = this.request.getOptions();
        if (opt.get("username").equals("")) {
            this.sendResponse(422, "Le nom d'utilisateur ne peut être vide.");
        }
        if (opt.get("password").equals("")) {
            this.sendResponse(422, "Le mot de passe ne peut être vide.");
        }

        if (!Files.exists(Paths.get(this.getFileUrl()))) {
            try {
                Files.createFile(Paths.get(this.getFileUrl()));
                FileWriter writer = new FileWriter(this.getFileUrl());
                writer.write("[]");
                writer.close();
            }
            catch (IOException e) {
                e.printStackTrace();
                this.sendResponse(500, null);
                return;
            }
        }

        ArrayList<User> userList = this.getUsers();


        // In case username is taken
        for (User u : userList) {
            if (u.getUsername().equals(opt.get("username"))) {
                this.sendResponse(422, "Le nom d'utilisateur est déjà utilisé.");
                return;
            }
        }

        User newUser = new User(
                opt.get("username"),
                opt.get("password")
        );
        userList.add(newUser);

        if (this.updateJson(userList)) {
            this.sendResponse(201, null);
        }
        else {
            this.sendResponse(500, null);
        }
    }

    private void handlePut(StringBuilder data) {
        if (!areRequestParametersValid("username", "oldPassword", "newPassword")) return;

        if (Files.exists(Paths.get(this.getFileUrl()))) {
            ArrayList<User> userList = this.getUsers();

            HashMap<String, String> opt = this.request.getOptions();

            for (User u : userList) {
                if (opt.get("username").equals(u.getUsername())) {
                    if (opt.get("oldPassword").equals(u.getPassword())) {
                        u.setPassword(opt.get("newPassword"));
                        this.updateJson(userList);
                        this.sendResponse(200, null);
                        return;
                    }
                    else {
                        // wrong password
                        this.sendResponse(401, "Le mot de passe entré est incorrect.");
                        return;
                    }
                }
            }

            // user not found
            this.sendResponse(404, "L'utilisateur recherché n'existe pas.");
        }
        else {
            this.sendResponse(404, "L'utilisateur recherché n'existe pas.");
        }
    }

    private void handleDelete(StringBuilder data) {
        if (!areRequestParametersValid("username")) return;

        if (Files.exists(Paths.get(this.getFileUrl()))) {
            ArrayList<User> userList = this.getUsers();
            String username = request.getOptions().get("username");

            // Si le nom d'utilisateur est invalide
            if (username.equals("")) {
                this.sendResponse(422, "Nom d'utilisateur non spécifié.");
                return;
            }

            boolean removed = userList.removeIf(u -> u.getUsername().equals(username));

            if (removed) {
                if (this.updateJson(userList)) {
                    this.sendResponse(200, null);
                }
                else {
                    this.sendResponse(500, null);
                }
            }
            else {
                this.sendResponse(404, "L'utilisateur n'existe pas.");
            }
        }
        else {
            this.sendResponse(404, "L'utilisateur n'existe pas.");
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

    private void sendResponse(int code, String message) {
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

    /**
     * Vérifie si les paramètres nécessaires de la requêtes ont tous été entrés;
     * retourne false si ce n'est pas le cas.
     * @param args
     * @return
     */
    boolean areRequestParametersValid(String... args) {
        for (String arg : args) {
            if (!this.request.getOptions().containsKey(arg)) {
                this.sendResponse(422, "Les paramètres sont invalides.");
                System.out.println("we in there");
                return false;
            }
        }
        return true;
    }


}
