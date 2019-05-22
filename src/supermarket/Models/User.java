package supermarket.Models;

public class User {
    private int id;
    private String username;
    private String name;
    private int kind;

    private static User userInstance = null;

    public User(int id, String username, String name, int kind) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.kind = kind;
    }

    public void login() {
        userInstance = this;
    }

    public static User getUserInstance() {
        return userInstance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }
}
