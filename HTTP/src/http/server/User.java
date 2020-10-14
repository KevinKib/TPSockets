package http.server;

/**
 * CLasse gérant les données d'un utilisateur.
 */
public class User {

    /**
     * Nom de l'utilisateur.
     */
    private String username;
    /**
     * Mot de passe de l'utilisateur.
     */
    private String password;

    /**
     * Constructeur.
     * @param username Nom de l'utilisateur.
     * @param password Mot de passe de l'utilisateur.
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Getter du nom d'utilisateur.
     * @return Nom d'utilisateur.
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Getter du mot de passe.
     * @return Mot de passe.
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Setter du nom d'utilisateur.
     * @param username Nom d'utilisateur.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Setter du mot de passe.
     * @param password Mot de passe.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
