// File: src/application/Main.java
package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

	// Put near the top of Main.java
	private static final String APP_DIR =
	        System.getProperty("user.home")
	                + File.separator + "AppData"
	                + File.separator + "Roaming"
	                + File.separator + "MechanicShop";

	private static final String DATA_FILE_PATH =
	        APP_DIR + File.separator + "mechanic_shop_data.ser";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private List<Client> clientDirectory = new ArrayList<>();

    private Client selectedClient;
    private Vehicle selectedVehicle;
    private ServiceRecord selectedRecord;

    private ListView<String> clientListView;
    private ListView<String> vehicleListView;
    private ListView<String> serviceListView;

    private TextField clientNameField, clientPhoneField;
    private TextField vinField, plateField, makeField, modelField, mileageField;

    private TextArea detailsArea;

    @Override
    public void start(Stage primaryStage) {
        
    	 // ADD THIS AS THE FIRST THING
        Thread.setDefaultUncaughtExceptionHandler((t, ex) -> {
            ex.printStackTrace();
        });
        
     // 🔐 ADD THIS RIGHT HERE
        if (!AuthGate.requireLogin()) {
            System.exit(0);
        }
    	
    	BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #A9A9A9;");

        String fontStyle = "-fx-font-size: 14px; -fx-font-weight: bold; -fx-font-family: Arial;";

        // ---------------- Left: Clients ----------------
        clientListView = new ListView<>();
        clientListView.setPrefWidth(280);
        clientListView.setStyle(fontStyle);
        clientListView.setOnMouseClicked(e -> onSelectClient());

        VBox leftBox = new VBox(8, new Label("Clients"), clientListView);
        leftBox.setPadding(new Insets(10));
        leftBox.setAlignment(Pos.TOP_CENTER);

        // ---------------- Right: Vehicles + Service Records ----------------
        vehicleListView = new ListView<>();
        vehicleListView.setPrefWidth(520);
        vehicleListView.setPrefHeight(260);
        vehicleListView.setStyle(fontStyle);
        vehicleListView.setOnMouseClicked(e -> onSelectVehicle());

        serviceListView = new ListView<>();
        serviceListView.setPrefWidth(520);
        serviceListView.setPrefHeight(260);
        serviceListView.setStyle(fontStyle);
        serviceListView.setOnMouseClicked(e -> onSelectServiceRecord());

        VBox rightBox = new VBox(8,
                new Label("Vehicles"),
                vehicleListView,
                new Label("Service Records (select one to edit/add parts)"),
                serviceListView
        );
        rightBox.setPadding(new Insets(10));
        rightBox.setAlignment(Pos.TOP_CENTER);

        // ---------------- Top: Search ----------------
        TextField searchField = new TextField();
        searchField.setPromptText("Search by Client Name or Phone");
        searchField.setStyle(fontStyle);

        Button searchButton = new Button("Search");
        searchButton.setStyle(fontStyle);
        searchButton.setOnAction(e -> searchClients(searchField.getText()));

        Button showAllButton = new Button("Show All");
        showAllButton.setStyle(fontStyle);
        showAllButton.setOnAction(e -> refreshClientList());

        HBox searchBox = new HBox(10, searchField, searchButton, showAllButton);
        searchBox.setPadding(new Insets(10));
        searchBox.setAlignment(Pos.CENTER);

        // ---------------- Center: Forms + Buttons ----------------
        clientNameField = new TextField();
        clientNameField.setPromptText("Client Name");
        clientNameField.setStyle(fontStyle);

        clientPhoneField = new TextField();
        clientPhoneField.setPromptText("Client Phone");
        clientPhoneField.setStyle(fontStyle);

        vinField = new TextField();
        vinField.setPromptText("VIN");
        vinField.setStyle(fontStyle);

        plateField = new TextField();
        plateField.setPromptText("License Plate");
        plateField.setStyle(fontStyle);

        makeField = new TextField();
        makeField.setPromptText("Make");
        makeField.setStyle(fontStyle);

        modelField = new TextField();
        modelField.setPromptText("Model");
        modelField.setStyle(fontStyle);

        mileageField = new TextField();
        mileageField.setPromptText("Mileage");
        mileageField.setStyle(fontStyle);

        Button addClientBtn = new Button("Add Client");
        addClientBtn.setStyle(fontStyle);
        addClientBtn.setOnAction(e -> addClient());

        Button editClientBtn = new Button("Edit Client");
        editClientBtn.setStyle(fontStyle);
        editClientBtn.setOnAction(e -> loadClientIntoForm());

        Button saveClientBtn = new Button("Save Client");
        saveClientBtn.setStyle(fontStyle);
        saveClientBtn.setOnAction(e -> saveClientChanges());

        Button deleteClientBtn = new Button("Delete Client");
        deleteClientBtn.setStyle(fontStyle);
        deleteClientBtn.setOnAction(e -> deleteClient());

        HBox clientBtns = new HBox(10, addClientBtn, editClientBtn, saveClientBtn, deleteClientBtn);
        clientBtns.setAlignment(Pos.CENTER);

        Button addVehicleBtn = new Button("Add Vehicle");
        addVehicleBtn.setStyle(fontStyle);
        addVehicleBtn.setOnAction(e -> addVehicle());

        Button editVehicleBtn = new Button("Edit Vehicle");
        editVehicleBtn.setStyle(fontStyle);
        editVehicleBtn.setOnAction(e -> loadVehicleIntoForm());

        Button saveVehicleBtn = new Button("Save Vehicle");
        saveVehicleBtn.setStyle(fontStyle);
        saveVehicleBtn.setOnAction(e -> saveVehicleChanges());

        Button deleteVehicleBtn = new Button("Delete Vehicle");
        deleteVehicleBtn.setStyle(fontStyle);
        deleteVehicleBtn.setOnAction(e -> deleteVehicle());

        HBox vehicleBtns = new HBox(10, addVehicleBtn, editVehicleBtn, saveVehicleBtn, deleteVehicleBtn);
        vehicleBtns.setAlignment(Pos.CENTER);

        Button addServiceBtn = new Button("Add Service Record");
        addServiceBtn.setStyle(fontStyle);
        addServiceBtn.setOnAction(e -> addServiceRecord());

        Button addPartBtn = new Button("Add Part to Selected Record");
        addPartBtn.setStyle(fontStyle);
        addPartBtn.setOnAction(e -> addPartToSelectedRecord());

        Button editServiceBtn = new Button("Edit Selected Record");
        editServiceBtn.setStyle(fontStyle);
        editServiceBtn.setOnAction(e -> editSelectedServiceRecord());

        Button deleteServiceBtn = new Button("Delete Selected Record");
        deleteServiceBtn.setStyle(fontStyle);
        deleteServiceBtn.setOnAction(e -> deleteSelectedServiceRecord());

        HBox serviceBtns = new HBox(10, addServiceBtn, addPartBtn, editServiceBtn, deleteServiceBtn);
        serviceBtns.setAlignment(Pos.CENTER);

        VBox formBox = new VBox(
                10,
                new Label("Client Info"),
                clientNameField, clientPhoneField,
                clientBtns,
                new Separator(),
                new Label("Vehicle Info (for selected client)"),
                vinField, plateField, makeField, modelField, mileageField,
                vehicleBtns,
                new Separator(),
                serviceBtns
        );
        formBox.setPadding(new Insets(10));
        formBox.setAlignment(Pos.TOP_CENTER);

        // ---------------- Bottom: Details ----------------
        detailsArea = new TextArea();
        detailsArea.setEditable(false);
        detailsArea.setStyle(fontStyle);
        detailsArea.setPrefHeight(280);

        root.setTop(searchBox);
        root.setLeft(leftBox);
        root.setCenter(formBox);
        root.setRight(rightBox);
        root.setBottom(detailsArea);

        Scene scene = new Scene(root, 1400, 760);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Mechanic Shop Client Tracker");
        primaryStage.show();

        loadData();
        refreshClientList();
        detailsArea.setText("Select a client to view details.");
    }

    // ===================== Client actions =====================
    private void addClient() {
        String name = clientNameField.getText().trim();
        String phone = clientPhoneField.getText().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            showAlert("Missing Info", "Client name and phone are required.");
            return;
        }

        clientDirectory.add(new Client(name, phone));
        clearClientForm();
        saveData();
        refreshClientList();
    }

    private void loadClientIntoForm() {
        if (selectedClient == null) {
            showAlert("No Client Selected", "Select a client first.");
            return;
        }
        clientNameField.setText(selectedClient.getName());
        clientPhoneField.setText(selectedClient.getPhone());
    }

    private void saveClientChanges() {
        if (selectedClient == null) {
            showAlert("No Client Selected", "Select a client first.");
            return;
        }

        String name = clientNameField.getText().trim();
        String phone = clientPhoneField.getText().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            showAlert("Missing Info", "Client name and phone are required.");
            return;
        }

        selectedClient.setName(name);
        selectedClient.setPhone(phone);

        saveData();
        refreshClientList();
        showDetails();
    }

    private void deleteClient() {
        if (selectedClient == null) {
            showAlert("No Client Selected", "Select a client first.");
            return;
        }

        if (!confirm("Delete Client", "Delete this client and all vehicles/records?")) return;

        clientDirectory.remove(selectedClient);
        selectedClient = null;
        selectedVehicle = null;
        selectedRecord = null;

        clearClientForm();
        clearVehicleForm();
        clientListView.getSelectionModel().clearSelection();
        vehicleListView.getItems().clear();
        serviceListView.getItems().clear();
        detailsArea.clear();

        saveData();
        refreshClientList();
    }

    // ===================== Vehicle actions =====================
    private void addVehicle() {
        if (selectedClient == null) {
            showAlert("No Client Selected", "Select a client before adding a vehicle.");
            return;
        }

        String vin = vinField.getText().trim();
        String plate = plateField.getText().trim();
        String make = makeField.getText().trim();
        String model = modelField.getText().trim();

        if (vin.isEmpty() && plate.isEmpty()) {
            showAlert("Missing Info", "VIN or License Plate is required.");
            return;
        }

        int mileage = parseMileageOrAlert(mileageField.getText());
        if (mileage == Integer.MIN_VALUE) return; // error already shown

        Vehicle v = new Vehicle(vin, plate, make, model, mileage);
        selectedClient.getVehicles().add(v);

        clearVehicleForm();
        saveData();
        refreshVehicleList();
        showDetails();
    }

    private void loadVehicleIntoForm() {
        if (selectedVehicle == null) {
            showAlert("No Vehicle Selected", "Select a vehicle first.");
            return;
        }
        vinField.setText(nullSafe(selectedVehicle.getVin()));
        plateField.setText(nullSafe(selectedVehicle.getLicensePlate()));
        makeField.setText(nullSafe(selectedVehicle.getMake()));
        modelField.setText(nullSafe(selectedVehicle.getModel()));
        mileageField.setText(String.valueOf(selectedVehicle.getMileage()));
    }

    private void saveVehicleChanges() {
        if (selectedVehicle == null) {
            showAlert("No Vehicle Selected", "Select a vehicle first.");
            return;
        }

        String vin = vinField.getText().trim();
        String plate = plateField.getText().trim();
        if (vin.isEmpty() && plate.isEmpty()) {
            showAlert("Missing Info", "VIN or License Plate is required.");
            return;
        }

        int mileage = parseMileageOrAlert(mileageField.getText());
        if (mileage == Integer.MIN_VALUE) return;

        selectedVehicle.setVin(vin);
        selectedVehicle.setLicensePlate(plate);
        selectedVehicle.setMake(makeField.getText().trim());
        selectedVehicle.setModel(modelField.getText().trim());
        selectedVehicle.setMileage(mileage);

        saveData();
        refreshVehicleList();
        showDetails();
    }

    private void deleteVehicle() {
        if (selectedClient == null || selectedVehicle == null) {
            showAlert("Missing Selection", "Select a client and vehicle first.");
            return;
        }

        if (!confirm("Delete Vehicle", "Delete this vehicle and its service history?")) return;

        selectedClient.getVehicles().remove(selectedVehicle);
        selectedVehicle = null;
        selectedRecord = null;

        clearVehicleForm();
        vehicleListView.getSelectionModel().clearSelection();
        refreshVehicleList();
        refreshServiceList();
        saveData();
        showDetails();
    }

    // ===================== Service record actions =====================
    private void addServiceRecord() {
        if (selectedVehicle == null) {
            showAlert("No Vehicle Selected", "Select a vehicle first.");
            return;
        }

        String dateStr = prompt("Service Date", "Enter service date (dd/MM/yyyy):");
        if (dateStr == null) return;

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr.trim(), DATE_FMT);
        } catch (Exception ex) {
            showAlert("Invalid Date", "Use dd/MM/yyyy (example: 03/03/2026).");
            return;
        }

        String work = prompt("Work Performed", "Describe work performed:");
        if (work == null) return;

        String totalStr = prompt("Total Charged", "Enter total charged (example: 250.00):");
        if (totalStr == null) return;

        BigDecimal total;
        try {
            total = new BigDecimal(totalStr.trim());
        } catch (Exception ex) {
            showAlert("Invalid Amount", "Enter a valid number (example: 250.00).");
            return;
        }

        ServiceRecord record = new ServiceRecord(date, work.trim(), total);
        selectedVehicle.getServiceRecords().add(record);

        saveData();
        refreshServiceList();
        showDetails();
    }

    private void editSelectedServiceRecord() {
        if (selectedVehicle == null || selectedRecord == null) {
            showAlert("No Record Selected", "Select a service record first.");
            return;
        }

        String dateStr = prompt("Edit Service Date", "Enter service date (dd/MM/yyyy):");
        if (dateStr == null) return;

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr.trim(), DATE_FMT);
        } catch (Exception ex) {
            showAlert("Invalid Date", "Use dd/MM/yyyy.");
            return;
        }

        String work = prompt("Edit Work Performed", "Update work performed:");
        if (work == null) return;

        String totalStr = prompt("Edit Total Charged", "Enter total charged (example: 250.00):");
        if (totalStr == null) return;

        BigDecimal total;
        try {
            total = new BigDecimal(totalStr.trim());
        } catch (Exception ex) {
            showAlert("Invalid Amount", "Enter a valid number.");
            return;
        }

        selectedRecord.setDate(date);
        selectedRecord.setWorkPerformed(work.trim());
        selectedRecord.setTotalCharged(total);

        saveData();
        refreshServiceList();
        showDetails();
    }

    private void deleteSelectedServiceRecord() {
        if (selectedVehicle == null || selectedRecord == null) {
            showAlert("No Record Selected", "Select a service record first.");
            return;
        }

        if (!confirm("Delete Service Record", "Delete this service record?")) return;

        selectedVehicle.getServiceRecords().remove(selectedRecord);
        selectedRecord = null;

        saveData();
        refreshServiceList();
        showDetails();
    }

    // ===================== Parts actions =====================
    private void addPartToSelectedRecord() {
        if (selectedVehicle == null) {
            showAlert("No Vehicle Selected", "Select a vehicle first.");
            return;
        }
        if (selectedRecord == null) {
            showAlert("No Record Selected", "Select a service record first.");
            return;
        }

        String partName = prompt("Part Name", "Enter part name/description:");
        if (partName == null || partName.trim().isEmpty()) return;

        String vendor = prompt("Vendor", "Where was it purchased? (AutoZone, O'Reilly, dealer, online, etc.)");
        if (vendor == null || vendor.trim().isEmpty()) return;

        String qtyStr = prompt("Quantity", "Enter quantity (example: 1):");
        if (qtyStr == null) return;

        int qty;
        try {
            qty = Integer.parseInt(qtyStr.trim());
            if (qty <= 0) throw new IllegalArgumentException();
        } catch (Exception ex) {
            showAlert("Invalid Quantity", "Quantity must be a positive whole number.");
            return;
        }

        String unitStr = prompt("Unit Price", "Enter unit price (example: 39.99):");
        if (unitStr == null) return;

        BigDecimal unit;
        try {
            unit = new BigDecimal(unitStr.trim());
        } catch (Exception ex) {
            showAlert("Invalid Price", "Enter a valid number (example: 39.99).");
            return;
        }

        selectedRecord.getParts().add(new PartPurchase(partName.trim(), vendor.trim(), qty, unit));
        saveData();
        showDetails();
        refreshServiceList(); // optional, keeps selection list aligned
    }

    // ===================== Selection handlers =====================
    private void onSelectClient() {
        String selected = clientListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        selectedClient = null;
        selectedVehicle = null;
        selectedRecord = null;

        for (Client c : clientDirectory) {
            if (formatClientListItem(c).equals(selected)) {
                selectedClient = c;
                break;
            }
        }

        refreshVehicleList();
        refreshServiceList();
        showDetails();
    }

    private void onSelectVehicle() {
        if (selectedClient == null) return;

        String selected = vehicleListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        selectedVehicle = null;
        selectedRecord = null;

        for (Vehicle v : selectedClient.getVehicles()) {
            if (formatVehicleListItem(v).equals(selected)) {
                selectedVehicle = v;
                break;
            }
        }

        refreshServiceList();
        showDetails();
    }

    private void onSelectServiceRecord() {
        if (selectedVehicle == null) return;

        String selected = serviceListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        selectedRecord = null;
        for (ServiceRecord r : selectedVehicle.getServiceRecords()) {
            if (formatServiceListItem(r).equals(selected)) {
                selectedRecord = r;
                break;
            }
        }
        showDetails();
    }

    // ===================== List refresh + formatting =====================
    private void refreshClientList() {
        clientListView.getItems().clear();
        for (Client c : clientDirectory) {
            clientListView.getItems().add(formatClientListItem(c));
        }
    }

    private void refreshVehicleList() {
        vehicleListView.getItems().clear();
        if (selectedClient == null) return;

        for (Vehicle v : selectedClient.getVehicles()) {
            vehicleListView.getItems().add(formatVehicleListItem(v));
        }
    }

    private void refreshServiceList() {
        serviceListView.getItems().clear();
        selectedRecord = null;

        if (selectedVehicle == null) return;

        for (ServiceRecord r : selectedVehicle.getServiceRecords()) {
            serviceListView.getItems().add(formatServiceListItem(r));
        }
    }

    private void searchClients(String text) {
        String q = text == null ? "" : text.trim().toLowerCase();
        clientListView.getItems().clear();

        for (Client c : clientDirectory) {
            if (c.getName().toLowerCase().contains(q) || c.getPhone().toLowerCase().contains(q)) {
                clientListView.getItems().add(formatClientListItem(c));
            }
        }
    }

    private void showDetails() {
        if (selectedClient == null) {
            detailsArea.setText("Select a client to view details.");
            return;
        }

        StringBuilder sb = new StringBuilder();

        sb.append("CLIENT\n");
        sb.append("Name: ").append(selectedClient.getName()).append("\n");
        sb.append("Phone: ").append(selectedClient.getPhone()).append("\n");
        sb.append("Vehicles: ").append(selectedClient.getVehicles().size()).append("\n\n");

        if (selectedVehicle == null) {
            sb.append("Select a vehicle to view service history.\n");
            detailsArea.setText(sb.toString());
            return;
        }

        sb.append("VEHICLE\n");
        sb.append("Make/Model: ").append(nullSafe(selectedVehicle.getMake())).append(" ").append(nullSafe(selectedVehicle.getModel())).append("\n");
        sb.append("Plate: ").append(nullSafe(selectedVehicle.getLicensePlate())).append("\n");
        sb.append("VIN: ").append(nullSafe(selectedVehicle.getVin())).append("\n");
        sb.append("Mileage: ").append(selectedVehicle.getMileage()).append("\n\n");

        sb.append("SERVICE HISTORY\n");
        if (selectedVehicle.getServiceRecords().isEmpty()) {
            sb.append("No service records.\n");
            detailsArea.setText(sb.toString());
            return;
        }

        int i = 1;
        for (ServiceRecord r : selectedVehicle.getServiceRecords()) {
            sb.append("Record #").append(i++).append("\n");
            sb.append("Date: ").append(r.getDate()).append("\n");
            sb.append("Work: ").append(r.getWorkPerformed()).append("\n");
            sb.append("Total Charged: $").append(r.getTotalCharged()).append("\n");

            if (r.getParts().isEmpty()) {
                sb.append("Parts: none\n");
            } else {
                sb.append("Parts:\n");
                for (PartPurchase p : r.getParts()) {
                    sb.append("  - ").append(p).append("\n");
                }
            }
            sb.append("\n");
        }

        if (selectedRecord != null) {
            sb.append("SELECTED RECORD\n");
            sb.append(formatServiceListItem(selectedRecord)).append("\n");
        }

        detailsArea.setText(sb.toString());
    }

    private String formatClientListItem(Client c) {
        return c.getName() + " (" + c.getPhone() + ")";
    }

    private String formatVehicleListItem(Vehicle v) {
        String mm = (v.getMake() == null ? "" : v.getMake()) + " " + (v.getModel() == null ? "" : v.getModel());
        String plate = v.getLicensePlate() == null ? "" : v.getLicensePlate();
        String vin = v.getVin() == null ? "" : v.getVin();

        String label = mm.trim().isEmpty() ? "Vehicle" : mm.trim();
        if (!plate.isEmpty()) label += " | " + plate;
        if (!vin.isEmpty()) label += " | " + vin;
        label += " | " + v.getMileage() + " mi";
        return label;
    }

    private String formatServiceListItem(ServiceRecord r) {
        String work = r.getWorkPerformed() == null ? "" : r.getWorkPerformed().trim();
        if (work.length() > 28) work = work.substring(0, 28) + "...";
        return r.getDate() + " | $" + r.getTotalCharged() + " | " + work;
    }

    // ===================== Small helpers =====================
    private static String nullSafe(String s) {
        return s == null ? "" : s;
    }

    private int parseMileageOrAlert(String mileageText) {
        String s = mileageText == null ? "" : mileageText.trim();
        if (s.isEmpty()) return 0;
        try {
            int m = Integer.parseInt(s);
            if (m < 0) throw new IllegalArgumentException();
            return m;
        } catch (Exception ex) {
            showAlert("Invalid Mileage", "Mileage must be a non-negative whole number.");
            return Integer.MIN_VALUE;
        }
    }

    private void clearClientForm() {
        clientNameField.clear();
        clientPhoneField.clear();
    }

    private void clearVehicleForm() {
        vinField.clear();
        plateField.clear();
        makeField.clear();
        modelField.clear();
        mileageField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean confirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private String prompt(String title, String message) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(message);
        return dialog.showAndWait().orElse(null);
    }

    // ===================== Persistence =====================
    private void saveData() {
        try {
            // Ensure directory exists
            File dir = new File(APP_DIR);
            if (!dir.exists() && !dir.mkdirs()) {
                showAlert("Save Error", "Could not create app folder:\n" + APP_DIR);
                return;
            }

            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE_PATH))) {
                out.writeObject(clientDirectory);
            }
        } catch (IOException e) {
            showAlert("Save Error", "Could not save data:\n" + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        try {
            // Ensure directory exists (safe even if file doesn't)
            File dir = new File(APP_DIR);
            if (!dir.exists()) dir.mkdirs();

            File file = new File(DATA_FILE_PATH);
            if (!file.exists()) return;

            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                clientDirectory = (List<Client>) in.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            showAlert(
                "Load Error",
                "Could not load data:\n" + e.getMessage()
                + "\n\nIf you changed fields, delete:\n" + DATA_FILE_PATH
            );
            clientDirectory = new ArrayList<>();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}