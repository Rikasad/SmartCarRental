import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Rental implements Serializable {
    private int rentalId;
    private String customerUsername;
    private Vehicle vehicle;
    private LocalDate startDate, endDate, returnDate;   // returnDate null = still out

    public Rental(int id, String customer, Vehicle v, LocalDate start, LocalDate end) {
        if (!end.isAfter(start)) throw new IllegalArgumentException("End date must be after start date");
        rentalId = id; customerUsername = customer; vehicle = v; startDate = start; endDate = end;
    }

    public int getRentalId() { return rentalId; }
    public String getCustomerUsername() { return customerUsername; }
    public Vehicle getVehicle() { return vehicle; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate d) { returnDate = d; }
    public boolean isReturned() { return returnDate != null; }

    public boolean isOverdue() { return !isReturned() && LocalDate.now().isAfter(endDate); }

    public double calculateCost() {
        int days = (int) ChronoUnit.DAYS.between(startDate, endDate);
        double cost = vehicle.calculateRate(days);                 // polymorphic call
        if (isReturned() && returnDate.isAfter(endDate)) {
            long lateDays = ChronoUnit.DAYS.between(endDate, returnDate);
            cost += lateDays * vehicle.getDailyRate() * 1.5;       // late penalty
        }
        return cost;
    }

    public String generateInvoice() {
        return "===== INVOICE =====\n"
             + "Rental #" + rentalId + "\nCustomer: " + customerUsername + "\n"
             + "Vehicle: " + vehicle.getDetails() + "\n"
             + "From " + startDate + " to " + endDate + "\n"
             + "Returned: " + (isReturned() ? returnDate : "Not yet") + "\n"
             + "TOTAL: " + String.format("%.2f", calculateCost()) + "\n===================";
    }

    @Override public String toString() {
        return "#" + rentalId + " " + vehicle.getVehicleId() + " " + customerUsername + " "
             + startDate + "→" + endDate + (isReturned() ? " [returned]" : isOverdue() ? " [OVERDUE]" : " [active]");
    }
}