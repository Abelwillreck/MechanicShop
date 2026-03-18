package application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Client implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String phone;
    private final List<Vehicle> vehicles = new ArrayList<>();

    public Client(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() { return name; }
    public String getPhone() { return phone; }
    public List<Vehicle> getVehicles() { return vehicles; }

    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String toString() {
        return name + " (" + phone + ")";
    }
}