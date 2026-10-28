public class Car extends Vehicle {
    public Car(String id, String brand, String model, double rate) { super(id, brand, model, rate); }
    @Override public double calculateRate(int days) { return getDailyRate() * days; }
    @Override public String getCategory() { return "Car"; }
}