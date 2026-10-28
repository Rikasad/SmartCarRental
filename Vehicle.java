import java.io.Serializable;

public abstract class Vehicle implements Serializable {
    private String vehicleId, brand, model;
    private double dailyRate;

    public Vehicle(String vehicleId, String brand, String model, double dailyRate) {
        this.vehicleId = vehicleId;
        this.brand = brand;
        this.model = model;
        setDailyRate(dailyRate);
    }

    public String getVehicleId() { return vehicleId; }
    public String getBrand()     { return brand; }
    public String getModel()     { return model; }
    public double getDailyRate() { return dailyRate; }
    public void setDailyRate(double r) {
        if (r <= 0) throw new IllegalArgumentException("Rate must be positive");
        dailyRate = r;
    }

    public abstract double calculateRate(int days);   // overridden = polymorphism
    public abstract String getCategory();

    public String getDetails() {
        return vehicleId + " | " + getCategory() + " | " + brand + " " + model + " | " + dailyRate + "/day";
    }
}