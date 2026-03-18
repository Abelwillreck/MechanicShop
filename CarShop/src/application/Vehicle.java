package application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Vehicle implements Serializable {
    private static final long serialVersionUID = 1L;

    private String vin;
    private String licensePlate;
    private String make;
    private String model;

    private int mileage; // ✅ NEW

    private final List<ServiceRecord> serviceRecords = new ArrayList<>();

    // ✅ UPDATED constructor
    public Vehicle(String vin, String licensePlate, String make, String model, int mileage) {
        this.vin = vin;
        this.licensePlate = licensePlate;
        this.make = make;
        this.model = model;
        this.mileage = mileage;
    }

    public String getVin() { return vin; }
    public String getLicensePlate() { return licensePlate; }
    public String getMake() { return make; }
    public String getModel() { return model; }
    public int getMileage() { return mileage; }          // ✅ NEW
    public List<ServiceRecord> getServiceRecords() { return serviceRecords; }

    public void setVin(String vin) { this.vin = vin; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public void setMake(String make) { this.make = make; }
    public void setModel(String model) { this.model = model; }
    public void setMileage(int mileage) { this.mileage = mileage; } // ✅ NEW

    @Override
    public String toString() {
        String mm = (make == null ? "" : make) + " " + (model == null ? "" : model);
        return mm.trim()
                + " | Plate: " + (licensePlate == null ? "" : licensePlate)
                + " | VIN: " + (vin == null ? "" : vin)
                + " | Mileage: " + mileage;
    }
}