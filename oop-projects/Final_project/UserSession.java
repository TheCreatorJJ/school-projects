// Holds the currently logged-in user's details across screens
public class UserSession {
    private final int id;
    private final String fullName;
    private final String username;
    private final String role;

    public UserSession(int id, String fullName, String username, String role) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }
}