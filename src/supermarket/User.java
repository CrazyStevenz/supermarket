package supermarket;

class User {
    private int id;
    private String username;
    private String name;
    private int kind;

    private static User userInstance = null;

    User(int id, String username, String name, int kind) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.kind = kind;

        userInstance = this;
    }

    static User getUserInstance() {
        return userInstance;
    }

    void delete() {
        userInstance = null;
    }

    int getId() {
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
