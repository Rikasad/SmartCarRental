public class SUV extends Vehicle {
    public SUV(String id, String brand, String model, double rate) { super(id, brand, model, rate); }
    @Override public double calculateRate(int days) { return getDailyRate() * days * 1.2; }
    @Override public String getCategory() { return "SUV"; }
}
