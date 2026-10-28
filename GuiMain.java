import javax.swing.*;
import java.awt.*;

public class GuiMain extends JFrame {
    private Auth auth;
    private RentalManager manager;

    public GuiMain() {
        Object m = FileHandler.loadFromFile("data/manager.dat");
        Object a = FileHandler.loadFromFile("data/auth.dat");
        manager = (m == null) ? new RentalManager() : (RentalManager) m;
        auth = (a == null) ? new Auth() : (Auth) a;

        setTitle("Smart Car Rental - Login");
        setSize(350, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 2, 5, 5));

        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JButton loginBtn = new JButton("Login");
        JButton regBtn = new JButton("Register");

        add(new JLabel("Username:"));
        add(userField);
        add(new JLabel("Password:"));
        add(passField);
        add(loginBtn);
        add(regBtn);

        loginBtn.addActionListener(e -> {
            try {
                User u = auth.login(userField.getText(), new String(passField.getPassword()));
                new DashboardFrame(u, manager); // opens the dashboard
            } catch (InvalidLoginException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        regBtn.addActionListener(e -> {
            try {
                auth.register(userField.getText(), new String(passField.getPassword()));
                FileHandler.saveToFile(auth, "data/auth.dat");
                JOptionPane.showMessageDialog(this, "Registered! Now log in.");
            } catch (InvalidLoginException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GuiMain::new);
    }
}