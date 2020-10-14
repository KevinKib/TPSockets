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
import java.util.HashMap;
import java.util.List;

public class UserHandler extends Handler{

    private Response response;
    private Gson gson;

    /**
     * Constructeur.
     */
    public UserHandler(OutputStream out, Request request) {
        super("/users", out, request);
        this.gson = new Gson();
    }

    /**
     * Interprête une requête POST du client.
     * @param data Données additionnelles du client.
     */
    void handlePost(StringBuilder data) {
        if (!areRequestParametersValid("username", "password")) return;
        HashMap<String, String> opt = this.getRequest().getOptions();
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

    /**
     * Interprête une requête PUT du client.
     * @param data Données additionnelles du client.
     */
    void handlePut(StringBuilder data) {
        if (!areRequestParametersValid("username", "oldPassword", "newPassword")) return;

        if (Files.exists(Paths.get(this.getFileUrl()))) {
            ArrayList<User> userList = this.getUsers();

            HashMap<String, String> opt = this.getRequest().getOptions();

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

    /**
     * Interprête une requête DELETE du client.
     * @param data Données additionnelles du client.
     */
    void handleDelete(StringBuilder data) {
        if (!areRequestParametersValid("username")) return;

        if (Files.exists(Paths.get(this.getFileUrl()))) {
            ArrayList<User> userList = this.getUsers();
            String username = this.getRequest().getOptions().get("username");

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


}
