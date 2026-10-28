import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class DashboardFrame extends JFrame {
    private User user;
    private RentalManager manager;
    private boolean isAdmin;
    private JTabbedPane tabs;
    private DefaultTableModel vehicleModel, rentalModel;
    private JTable vehicleTable, rentalTable;
    private ArrayList<Rental> shownRentals = new ArrayList<>();
    private JComboBox<String> rentVehicleBox = new JComboBox<>();

    public DashboardFrame(User user, RentalManager manager) {
        this.user = user;
        this.manager = manager;
        this.isAdmin = user instanceof Admin;

        setTitle("Dashboard - " + user.getUsername() + " (" + user.getRole() + ")");
        setSize(780, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        tabs = new JTabbedPane();
        tabs.addTab("Vehicles", buildVehiclesTab()); // index 0
        tabs.addTab("Rent a Car", buildRentTab()); // index 1
        tabs.addTab(isAdmin ? "All Rentals" : "My Rentals", buildRentalsTab()); // index 2
        if (isAdmin)
            tabs.addTab("Add Vehicle", buildAddTab()); // index 3
        add(tabs);

        refreshVehicles();
        refreshRentals();
        setVisible(true);
    }

    private void save() {
        FileHandler.saveToFile(manager, "data/manager.dat");
    }

    private void showInvoice(Rental r) {
        JTextArea area = new JTextArea(r.generateInvoice());
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JOptionPane.showMessageDialog(this, area, "Invoice", JOptionPane.INFORMATION_MESSAGE);
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ---------- Vehicles tab ----------
    private JPanel buildVehiclesTab() {
        JPanel p = new JPanel(new BorderLayout());
        vehicleModel = new DefaultTableModel(
                new String[] { "ID", "Category", "Brand", "Model", "Rate/day" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        vehicleTable = new JTable(vehicleModel);
        p.add(new JScrollPane(vehicleTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));

        if (isAdmin) {
            JButton remove = new JButton("Remove selected");
            remove.addActionListener(e -> {
                int row = vehicleTable.getSelectedRow();
                if (row == -1) {
                    error("Select a vehicle first");
                    return;
                }
                String id = vehicleModel.getValueAt(row, 0).toString();
                manager.removeVehicle(id);
                save();
                refreshVehicles();
            });
            bottom.add(remove);
        } else {
            JTextField startField = new JTextField(8);
            JTextField endField = new JTextField(8);
            JButton book = new JButton("Book selected");
            bottom.add(new JLabel("Start (yyyy-mm-dd):"));
            bottom.add(startField);
            bottom.add(new JLabel("End:"));
            bottom.add(endField);
            bottom.add(book);

            book.addActionListener(e -> {
                int row = vehicleTable.getSelectedRow();
                if (row == -1) {
                    error("Select a vehicle first");
                    return;
                }
                String id = vehicleModel.getValueAt(row, 0).toString();
                try {
                    Rental r = manager.bookVehicle(user.getUsername(), id,
                            LocalDate.parse(startField.getText().trim()),
                            LocalDate.parse(endField.getText().trim()));
                    save();
                    refreshRentals();
                    JOptionPane.showMessageDialog(this,
                            "Booked! Estimated cost: " + String.format("%.2f", r.calculateCost()));
                } catch (VehicleNotAvailableException ex) {
                    error(ex.getMessage());
                } catch (DateTimeParseException ex) {
                    error("Use the date format yyyy-mm-dd, e.g. 2026-11-01");
                } catch (IllegalArgumentException ex) {
                    error(ex.getMessage());
                }
            });
        }
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    private void refreshVehicles() {
        vehicleModel.setRowCount(0);
        for (Vehicle v : manager.getVehicles()) {
            vehicleModel.addRow(new Object[] {
                    v.getVehicleId(), v.getCategory(), v.getBrand(), v.getModel(), v.getDailyRate() });
        }
        refreshRentBox();
    }

    // ---------- Rent a Car tab ----------
    private JPanel buildRentTab() {
        JPanel p = new JPanel(new GridLayout(5, 2, 8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        JTextField customerField = new JTextField(isAdmin ? "" : user.getUsername());
        customerField.setEditable(isAdmin);
        JTextField startField = new JTextField(LocalDate.now().toString());
        JTextField endField = new JTextField(LocalDate.now().plusDays(3).toString());
        JButton rentBtn = new JButton("Rent this car");

        p.add(new JLabel("Customer username:"));
        p.add(customerField);
        p.add(new JLabel("Choose car:"));
        p.add(rentVehicleBox);
        p.add(new JLabel("Start (yyyy-mm-dd):"));
        p.add(startField);
        p.add(new JLabel("End (yyyy-mm-dd):"));
        p.add(endField);
        p.add(new JLabel(""));
        p.add(rentBtn);

        rentBtn.addActionListener(e -> {
            String selected = (String) rentVehicleBox.getSelectedItem();
            if (selected == null) {
                error("No vehicles available. Add one first.");
                return;
            }
            String customer = customerField.getText().trim();
            if (customer.isEmpty()) {
                error("Enter the customer username");
                return;
            }
            String id = selected.split(" \\| ")[0];
            try {
                Rental r = manager.bookVehicle(customer, id,
                        LocalDate.parse(startField.getText().trim()),
                        LocalDate.parse(endField.getText().trim()));
                save();
                refreshRentals();
                JOptionPane.showMessageDialog(this,
                        "Rented! Rental #" + r.getRentalId()
                                + "\nEstimated cost: " + String.format("%.2f", r.calculateCost()));
            } catch (VehicleNotAvailableException ex) {
                error(ex.getMessage());
            } catch (DateTimeParseException ex) {
                error("Use the date format yyyy-mm-dd, e.g. 2026-11-01");
            } catch (IllegalArgumentException ex) {
                error(ex.getMessage());
            }
        });
        return p;
    }

    private void refreshRentBox() {
        rentVehicleBox.removeAllItems();
        for (Vehicle v : manager.getVehicles())
            rentVehicleBox.addItem(v.getDetails());
    }

    // ---------- Rentals tab ----------
    private JPanel buildRentalsTab() {
        JPanel p = new JPanel(new BorderLayout());
        rentalModel = new DefaultTableModel(
                new String[] { "Rental ID", "Vehicle", "Customer", "Start", "End", "Status", "Cost" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        rentalTable = new JTable(rentalModel);
        p.add(new JScrollPane(rentalTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton invoiceBtn = new JButton("Show invoice");
        invoiceBtn.addActionListener(e -> {
            int row = rentalTable.getSelectedRow();
            if (row == -1) {
                error("Select a rental first");
                return;
            }
            showInvoice(shownRentals.get(row));
        });
        bottom.add(invoiceBtn);

        if (isAdmin) {
            JButton addRentalBtn = new JButton("Add rental");
            addRentalBtn.addActionListener(e -> tabs.setSelectedIndex(1));
            JButton addCarBtn = new JButton("Add new car");
            addCarBtn.addActionListener(e -> tabs.setSelectedIndex(3));
            bottom.add(addRentalBtn);
            bottom.add(addCarBtn);
        } else {
            JButton returnBtn = new JButton("Return selected vehicle");
            returnBtn.addActionListener(e -> {
                int row = rentalTable.getSelectedRow();
                if (row == -1) {
                    error("Select a rental first");
                    return;
                }
                Rental r = shownRentals.get(row);
                if (r.isReturned()) {
                    error("This vehicle is already returned");
                    return;
                }
                manager.returnVehicle(r.getRentalId(), LocalDate.now());
                save();
                refreshRentals();
                showInvoice(r);
            });
            bottom.add(returnBtn);
        }

        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> {
            refreshVehicles();
            refreshRentals();
        });
        bottom.add(refresh);

        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    private void refreshRentals() {
        rentalModel.setRowCount(0);
        shownRentals = isAdmin ? new ArrayList<>(manager.getRentals())
                : manager.getHistory(user.getUsername());
        for (Rental r : shownRentals) {
            String status = r.isReturned() ? "Returned" : (r.isOverdue() ? "OVERDUE" : "Active");
            rentalModel.addRow(new Object[] {
                    r.getRentalId(), r.getVehicle().getVehicleId(), r.getCustomerUsername(),
                    r.getStartDate(), r.getEndDate(), status,
                    String.format("%.2f", r.calculateCost()) });
        }
    }

    // ---------- Add Vehicle tab (admin only) ----------
    private JPanel buildAddTab() {
        JPanel p = new JPanel(new GridLayout(6, 2, 8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JComboBox<String> typeBox = new JComboBox<>(new String[] { "Car", "SUV", "Luxury" });
        JTextField idField = new JTextField();
        JTextField brandField = new JTextField();
        JTextField modelField = new JTextField();
        JTextField rateField = new JTextField();
        JButton addBtn = new JButton("Add vehicle");

        p.add(new JLabel("Type:"));
        p.add(typeBox);
        p.add(new JLabel("ID:"));
        p.add(idField);
        p.add(new JLabel("Brand:"));
        p.add(brandField);
        p.add(new JLabel("Model:"));
        p.add(modelField);
        p.add(new JLabel("Daily rate:"));
        p.add(rateField);
        p.add(new JLabel(""));
        p.add(addBtn);

        addBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            String brand = brandField.getText().trim();
            String model = modelField.getText().trim();
            if (id.isEmpty() || brand.isEmpty() || model.isEmpty()) {
                error("Fill in all fields");
                return;
            }
            if (manager.findVehicle(id) != null) {
                error("A vehicle with that ID already exists");
                return;
            }
            try {
                double rate = Double.parseDouble(rateField.getText().trim());
                String type = (String) typeBox.getSelectedItem();
                Vehicle v;
                if (type.equals("Car"))
                    v = new Car(id, brand, model, rate);
                else if (type.equals("SUV"))
                    v = new SUV(id, brand, model, rate);
                else
                    v = new LuxuryVehicle(id, brand, model, rate);
                manager.addVehicle(v);
                save();
                refreshVehicles();
                idField.setText("");
                brandField.setText("");
                modelField.setText("");
                rateField.setText("");
                JOptionPane.showMessageDialog(this, "Vehicle added!");
            } catch (NumberFormatException ex) {
                error("Daily rate must be a number");
            } catch (IllegalArgumentException ex) {
                error(ex.getMessage());
            }
        });
        return p;
    }
}