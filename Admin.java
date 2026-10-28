public class Admin extends User {
    public Admin(String u, String p) { super(u, p); }
    @Override public String getRole() { return "ADMIN"; }
}