import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

public class RentalManager implements Serializable {
    private ArrayList<Vehicle> vehicles = new ArrayList<>();
    private ArrayList<Rental> rentals = new ArrayList<>();
    private int nextRentalId = 1;

    public void addVehicle(Vehicle v) { vehicles.add(v); }
    public boolean removeVehicle(String id) { return vehicles.removeIf(v -> v.getVehicleId().equals(id)); }
    public ArrayList<Vehicle> getVehicles() { return vehicles; }
    public ArrayList<Rental> getRentals() { return rentals; }

    public Vehicle findVehicle(String id) {
        for (Vehicle v : vehicles) if (v.getVehicleId().equalsIgnoreCase(id)) return v;
        return null;
    }

    // Method OVERLOADING (two versions of searchVehicle)
    public ArrayList<Vehicle> searchVehicle(String category) {
        ArrayList<Vehicle> out = new ArrayList<>();
        for (Vehicle v : vehicles) if (v.getCategory().equalsIgnoreCase(category)) out.add(v);
        return out;
    }
    public ArrayList<Vehicle> searchVehicle(String category, String brand, double maxPrice) {
        ArrayList<Vehicle> out = new ArrayList<>();
        for (Vehicle v : vehicles)
            if (v.getCategory().equalsIgnoreCase(category)
                && v.getBrand().equalsIgnoreCase(brand)
                && v.getDailyRate() <= maxPrice) out.add(v);
        return out;
    }

    public boolean isAvailable(Vehicle v, LocalDate start, LocalDate end) {
        for (Rental r : rentals) {
            if (r.getVehicle() == v && !r.isReturned()
                && start.isBefore(r.getEndDate()) && end.isAfter(r.getStartDate()))
                return false;   // dates overlap
        }
        return true;
    }

    public Rental bookVehicle(String customer, String vehicleId, LocalDate start, LocalDate end)
            throws VehicleNotAvailableException {
        Vehicle v = findVehicle(vehicleId);
        if (v == null) throw new VehicleNotAvailableException("No such vehicle: " + vehicleId);
        if (!isAvailable(v, start, end)) throw new VehicleNotAvailableException("Vehicle already booked for those dates");
        Rental r = new Rental(nextRentalId++, customer, v, start, end);
        rentals.add(r);
        return r;
    }

    public Rental returnVehicle(int rentalId, LocalDate returnDate) {
        for (Rental r : rentals)
            if (r.getRentalId() == rentalId && !r.isReturned()) { r.setReturnDate(returnDate); return r; }
        return null;
    }

    public ArrayList<Rental> getOverdueRentals() {
        ArrayList<Rental> out = new ArrayList<>();
        for (Rental r : rentals) if (r.isOverdue()) out.add(r);
        return out;
    }

    public ArrayList<Rental> getHistory(String customer) {
        ArrayList<Rental> out = new ArrayList<>();
        for (Rental r : rentals) if (r.getCustomerUsername().equals(customer)) out.add(r);
        return out;
    }
}
