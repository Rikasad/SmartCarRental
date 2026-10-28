import java.io.Serializable;
import java.util.ArrayList;

public class Auth implements Serializable {
    private ArrayList<User> users = new ArrayList<>();

    public Auth() { users.add(new Admin("admin", "admin123")); }   // default admin

    public void register(String username, String password) throws InvalidLoginException {
        if (username.isBlank() || password.isBlank()) throw new InvalidLoginException("Fields cannot be empty");
        for (User u : users)
            if (u.getUsername().equalsIgnoreCase(username)) throw new InvalidLoginException("Username already taken");
        users.add(new Customer(username, password));
    }

    public User login(String username, String password) throws InvalidLoginException {
        for (User u : users)
            if (u.getUsername().equalsIgnoreCase(username) && u.checkPassword(password)) return u;
        throw new InvalidLoginException("Wrong username or password");
    }
}