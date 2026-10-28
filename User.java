import java.io.Serializable;
import java.security.MessageDigest;

public abstract class User implements Serializable {
    private String username, passwordHash;

    public User(String username, String password) {
        this.username = username;
        this.passwordHash = hash(password);
    }
    public String getUsername() { return username; }
    public boolean checkPassword(String p) { return passwordHash.equals(hash(p)); }
    public abstract String getRole();

    private static String hash(String p) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            StringBuilder sb = new StringBuilder();
            for (byte b : md.digest(p.getBytes())) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}