import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    static Scanner sc = new Scanner(System.in);
    static RentalManager manager;
    static Auth auth;

    static String ask(String msg) {
        System.out.print(msg);
        return sc.nextLine().trim();
    }

    static void saveAll() {
        FileHandler.saveToFile(manager, "data/manager.dat");
        FileHandler.saveToFile(auth, "data/auth.dat");
    }

    public static void main(String[] args) {
        Object m = FileHandler.loadFromFile("data/manager.dat");
        Object a = FileHandler.loadFromFile("data/auth.dat");
        manager = (m == null) ? new RentalManager() : (RentalManager) m;
        auth = (a == null) ? new Auth() : (Auth) a;

        if (manager.getVehicles().isEmpty()) { // sample data on first run
            manager.addVehicle(new Car("C1", "Toyota", "Axio", 50));
            manager.addVehicle(new SUV("S1", "Honda", "CR-V", 80));
            manager.addVehicle(new LuxuryVehicle("L1", "Mercedes", "E-Class", 150));
            saveAll();
        }

        while (true) {
            System.out.println("\n--- SMART CAR RENTAL ---\n1. Login\n2. Register\n3. Exit");
            String c = ask("Choice: ");
            try {
                if (c.equals("1")) {
                    User u = auth.login(ask("Username: "), ask("Password: "));
                    if (u instanceof Admin)
                        adminMenu();
                    else
                        customerMenu((Customer) u);
                } else if (c.equals("2")) {
                    auth.register(ask("Username: "), ask("Password: "));
                    saveAll();
                    System.out.println("Registered! Now log in.");
                } else if (c.equals("3")) {
                    saveAll();
                    break;
                }
            } catch (InvalidLoginException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    static void customerMenu(Customer cu) {
        for (Rental r : manager.getOverdueRentals())
            if (r.getCustomerUsername().equals(cu.getUsername()))
                System.out.println("⚠ OVERDUE: " + r);
        while (true) {
            System.out.println("\n1. Browse  2. Book  3. Return  4. History  5. Logout");
            String c = ask("Choice: ");
            try {
                if (c.equals("1")) {
                    for (Vehicle v : manager.getVehicles())
                        System.out.println(v.getDetails());
                } else if (c.equals("2")) {
                    Rental r = manager.bookVehicle(cu.getUsername(), ask("Vehicle ID: "),
                            LocalDate.parse(ask("Start (yyyy-mm-dd): ")),
                            LocalDate.parse(ask("End (yyyy-mm-dd): ")));
                    System.out.println("Booked! Estimated cost: " + r.calculateCost());
                    saveAll();
                } else if (c.equals("3")) {
                    Rental r = manager.returnVehicle(Integer.parseInt(ask("Rental ID: ")), LocalDate.now());
                    System.out.println(r == null ? "Rental not found" : r.generateInvoice());
                    saveAll();
                } else if (c.equals("4")) {
                    for (Rental r : manager.getHistory(cu.getUsername()))
                        System.out.println(r);
                } else if (c.equals("5"))
                    return;
            } catch (VehicleNotAvailableException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (java.time.format.DateTimeParseException | NumberFormatException e) {
                System.out.println("Invalid input format");
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    static void adminMenu() {
        while (true) {
            System.out.println("\n1. Add vehicle  2. Remove vehicle  3. All rentals  4. Overdue  5. Logout");
            String c = ask("Choice: ");
            try {
                if (c.equals("1")) {
                    String type = ask("Type (car/suv/luxury): ").toLowerCase();
                    String id = ask("ID: "), brand = ask("Brand: "), model = ask("Model: ");
                    double rate = Double.parseDouble(ask("Daily rate: "));
                    switch (type) {
                        case "car" -> manager.addVehicle(new Car(id, brand, model, rate));
                        case "suv" -> manager.addVehicle(new SUV(id, brand, model, rate));
                        case "luxury" -> manager.addVehicle(new LuxuryVehicle(id, brand, model, rate));
                        default -> System.out.println("Unknown type");
                    }
                    saveAll();
                } else if (c.equals("2")) {
                    System.out.println(manager.removeVehicle(ask("ID: ")) ? "Removed" : "Not found");
                    saveAll();
                } else if (c.equals("3")) {
                    for (Rental r : manager.getRentals())
                        System.out.println(r);
                } else if (c.equals("4")) {
                    for (Rental r : manager.getOverdueRentals())
                        System.out.println(r);
                } else if (c.equals("5"))
                    return;
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input: " + e.getMessage());
            }
        }
    }
}