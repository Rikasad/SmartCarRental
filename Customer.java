
public class Customer extends User {
    public Customer(String u, String p) { super(u, p); }
    @Override public String getRole() { return "CUSTOMER"; }
}