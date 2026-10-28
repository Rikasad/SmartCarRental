public class LuxuryVehicle extends Vehicle {
    public LuxuryVehicle(String id, String brand, String model, double rate) { super(id, brand, model, rate); }
    @Override public double calculateRate(int days) { return getDailyRate() * days * 1.5 + 20; }
    @Override public String getCategory() { return "Luxury"; }
}