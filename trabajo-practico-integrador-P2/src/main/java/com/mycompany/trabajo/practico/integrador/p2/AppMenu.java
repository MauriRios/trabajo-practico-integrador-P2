package com.mycompany.trabajo.practico.integrador.p2;

import com.mycompany.trabajo.practico.integrador.p2.entities.InsuranceVehicle;
import com.mycompany.trabajo.practico.integrador.p2.entities.Vehicle;
import com.mycompany.trabajo.practico.integrador.p2.entities.enums.CoverType;
import com.mycompany.trabajo.practico.integrador.p2.services.InsuranceVehicleService;
import com.mycompany.trabajo.practico.integrador.p2.services.VehicleService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class AppMenu {

    private VehicleService vehicleService = new VehicleService();
    private InsuranceVehicleService insuranceService = new InsuranceVehicleService();
    private Scanner scanner = new Scanner(System.in);
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void start() {
        boolean exit = false;

        while (!exit) {
            printMainMenu();
            String option = scanner.nextLine().trim();

            try {
                switch (option) {
                    case "1":
                        vehicleMenu();
                        break;
                    case "2":
                        insuranceMenu();
                        break;
                    case "3":
                        searchMenu();
                        break;
                    case "0":
                        exit = true;
                        System.out.println("\nGoodbye! Closing application...");
                        break;
                    default:
                        System.out.println("\nInvalid option. Please try again.");
                }
            } catch (Exception e) {
                System.err.println("\n✗ Error: " + e.getMessage());
            }

            if (!exit) {
                System.out.println("\nPress ENTER to continue...");
                scanner.nextLine();
            }
        }

        scanner.close();
    }

    private void printMainMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("      VEHICLE INSURANCE MANAGEMENT SYSTEM");
        System.out.println("=".repeat(50));
        System.out.println("1. Vehicle Management");
        System.out.println("2. Insurance Management");
        System.out.println("3. Search");
        System.out.println("0. Exit");
        System.out.println("=".repeat(50));
        System.out.print("Select an option: ");
    }

    // ==================== VEHICLE MENU ====================

    private void vehicleMenu() {
        boolean back = false;

        while (!back) {
            printVehicleMenu();
            String option = scanner.nextLine().trim();

            try {
                switch (option) {
                    case "1":
                        createVehicle();
                        break;
                    case "2":
                        listAllVehicles();
                        break;
                    case "3":
                        viewVehicleById();
                        break;
                    case "4":
                        updateVehicle();
                        break;
                    case "5":
                        deleteVehicle();
                        break;
                    case "0":
                        back = true;
                        break;
                    default:
                        System.out.println("\nInvalid option. Please try again.");
                }
            } catch (Exception e) {
                System.err.println("\n✗ Error: " + e.getMessage());
            }

            if (!back) {
                System.out.println("\nPress ENTER to continue...");
                scanner.nextLine();
            }
        }
    }

    private void printVehicleMenu() {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("           VEHICLE MANAGEMENT");
        System.out.println("-".repeat(50));
        System.out.println("1. Create Vehicle");
        System.out.println("2. List All Vehicles");
        System.out.println("3. View Vehicle by ID");
        System.out.println("4. Update Vehicle");
        System.out.println("5. Delete Vehicle (Logical)");
        System.out.println("0. Back to Main Menu");
        System.out.println("-".repeat(50));
        System.out.print("Select an option: ");
    }

    private void createVehicle() throws Exception {
        System.out.println("\n--- CREATE NEW VEHICLE ---");

        System.out.print("Domain (max 10 chars): ");
        String domain = scanner.nextLine().trim().toUpperCase();

        System.out.print("Brand (max 50 chars): ");
        String brand = scanner.nextLine().trim();

        System.out.print("Model (max 50 chars): ");
        String model = scanner.nextLine().trim();

        System.out.print("Year: ");
        Integer year = parseInteger(scanner.nextLine().trim());

        System.out.print("Chassis Number (max 50 chars, optional): ");
        String chassisNumber = scanner.nextLine().trim();
        if (chassisNumber.isEmpty()) {
            chassisNumber = null;
        }

        System.out.print("\nDo you want to add insurance now? (Y/N): ");
        String addInsurance = scanner.nextLine().trim().toUpperCase();

        Vehicle vehicle = new Vehicle(domain, brand, model, year, chassisNumber);

        if (addInsurance.equals("Y")) {
            InsuranceVehicle insurance = createInsuranceData();
            vehicle.setInsurance(insurance);
        }

        vehicleService.insert(vehicle);
    }

    private void listAllVehicles() throws Exception {
        System.out.println("\n--- ALL VEHICLES ---");

        List<Vehicle> vehicles = vehicleService.getAll();

        if (vehicles.isEmpty()) {
            System.out.println("No vehicles found.");
            return;
        }

        System.out.println("\n" + "=".repeat(100));
        System.out.printf("%-5s %-12s %-20s %-20s %-6s %-15s %-12s%n",
                "ID", "PLATE", "BRAND", "MODEL", "YEAR", "CHASSIS", "INSURANCE");
        System.out.println("=".repeat(100));

        for (Vehicle v : vehicles) {
            String hasInsurance = v.getInsurance() != null ? "Yes (ID: " + v.getInsurance().getId() + ")"  : "No";
            System.out.printf("%-5d %-12s %-20s %-20s %-6d %-15s %-12s%n",
                    v.getVehicleId(), v.getDomain(), v.getBrand(), v.getModel(),
                    v.getYear(), v.getChassisNumber() != null ? v.getChassisNumber() : "N/A",
                    hasInsurance);
        }

        System.out.println("=".repeat(100));
        System.out.println("Total vehicles: " + vehicles.size());
    }

    private void viewVehicleById() throws Exception {
        System.out.println("\n--- VIEW VEHICLE BY ID ---");

        System.out.print("Enter Vehicle ID: ");
        Long id = parseLong(scanner.nextLine().trim());

        if (id == null) {
            System.out.println("Invalid ID.");
            return;
        }

        Vehicle vehicle = vehicleService.getById(id);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("VEHICLE DETAILS");
        System.out.println("=".repeat(60));
        System.out.println("ID:             " + vehicle.getVehicleId());
        System.out.println("Domain:          " + vehicle.getDomain());
        System.out.println("Brand:          " + vehicle.getBrand());
        System.out.println("Model:          " + vehicle.getModel());
        System.out.println("Year:           " + vehicle.getYear());
        System.out.println("Chassis Number: " + (vehicle.getChassisNumber() != null ? vehicle.getChassisNumber() : "N/A"));
        System.out.println("Active:         " + vehicle.getIsActive());

        if (vehicle.getInsurance() != null) {
            InsuranceVehicle ins = vehicle.getInsurance();
            System.out.println("\n--- INSURANCE INFORMATION ---");
            System.out.println("Insurance ID:     " + ins.getId());
            System.out.println("Insurance Name:   " + ins.getInsuranceName());
            System.out.println("Policy Number:    " + ins.getPolicyNumber());
            System.out.println("Cover:            " + ins.getCover() + " - " + ins.getCover().getDescription());
            System.out.println("Expiration Date:  " + ins.getExpirationDate());
        } else {
            System.out.println("\nNo insurance assigned to this vehicle.");
        }

        System.out.println("=".repeat(60));
    }

    private void updateVehicle() throws Exception {
        System.out.println("\n--- UPDATE VEHICLE ---");

        System.out.print("Enter Vehicle ID to update: ");
        Long id = parseLong(scanner.nextLine().trim());

        if (id == null) {
            System.out.println("Invalid ID.");
            return;
        }

        Vehicle vehicle = vehicleService.getById(id);

        System.out.println("\nCurrent data:");
        System.out.println("Domain: " + vehicle.getDomain());
        System.out.println("Brand: " + vehicle.getBrand());
        System.out.println("Model: " + vehicle.getModel());
        System.out.println("Year: " + vehicle.getYear());
        System.out.println("Chassis: " + vehicle.getChassisNumber());

        System.out.println("\nEnter new data (leave blank to keep current value):");

        System.out.print("New Domain: ");
        String domain = scanner.nextLine().trim().toUpperCase();
        if (!domain.isEmpty()) {
            vehicle.setDomain(domain);
        }

        System.out.print("New Brand: ");
        String brand = scanner.nextLine().trim();
        if (!brand.isEmpty()) {
            vehicle.setBrand(brand);
        }

        System.out.print("New Model: ");
        String model = scanner.nextLine().trim();
        if (!model.isEmpty()) {
            vehicle.setModel(model);
        }

        System.out.print("New Year: ");
        String yearStr = scanner.nextLine().trim();
        if (!yearStr.isEmpty()) {
            Integer year = parseInteger(yearStr);
            if (year != null) {
                vehicle.setYear(year);
            }
        }

        System.out.print("New Chassis Number: ");
        String chassis = scanner.nextLine().trim();
        if (!chassis.isEmpty()) {
            vehicle.setChassisNumber(chassis);
        }

        vehicleService.update(vehicle);
    }

    private void deleteVehicle() throws Exception {
        System.out.println("\n--- DELETE VEHICLE (LOGICAL) ---");

        System.out.print("Enter Vehicle ID to delete: ");
        Long id = parseLong(scanner.nextLine().trim());

        if (id == null) {
            System.out.println("Invalid ID.");
            return;
        }

        Vehicle vehicle = vehicleService.getById(id);
        System.out.println("\nVehicle to delete: " + vehicle.getDomain() + " - " + vehicle.getBrand() + " " + vehicle.getModel());

        System.out.print("Are you sure? (Y/N): ");
        String confirm = scanner.nextLine().trim().toUpperCase();

        if (confirm.equals("Y")) {
            vehicleService.delete(id);
        } else {
            System.out.println("Operation cancelled.");
        }
    }

    // ==================== INSURANCE MENU ====================

    private void insuranceMenu() {
        boolean back = false;

        while (!back) {
            printInsuranceMenu();
            String option = scanner.nextLine().trim();

            try {
                switch (option) {
                    case "1":
                        createInsurance();
                        break;
                    case "2":
                        listAllInsurances();
                        break;
                    case "3":
                        viewInsuranceById();
                        break;
                    case "4":
                        updateInsurance();
                        break;
                    case "5":
                        deleteInsurance();
                        break;
                    case "0":
                        back = true;
                        break;
                    default:
                        System.out.println("\nInvalid option. Please try again.");
                }
            } catch (Exception e) {
                System.err.println("\n✗ Error: " + e.getMessage());
            }

            if (!back) {
                System.out.println("\nPress ENTER to continue...");
                scanner.nextLine();
            }
        }
    }

    private void printInsuranceMenu() {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("         INSURANCE MANAGEMENT");
        System.out.println("-".repeat(50));
        System.out.println("1. Create Insurance");
        System.out.println("2. List All Insurances");
        System.out.println("3. View Insurance by ID");
        System.out.println("4. Update Insurance");
        System.out.println("5. Delete Insurance (Logical)");
        System.out.println("0. Back to Main Menu");
        System.out.println("-".repeat(50));
        System.out.print("Select an option: ");
    }

    private void createInsurance() throws Exception {
        System.out.println("\n--- CREATE NEW INSURANCE ---");

        System.out.print("Vehicle ID: ");
        Long vehicleId = parseLong(scanner.nextLine().trim());

        if (vehicleId == null) {
            System.out.println("Invalid Vehicle ID.");
            return;
        }

        // Verificar que el vehículo existe
        Vehicle vehicle = vehicleService.getById(vehicleId);
        System.out.println("Vehicle: " + vehicle.getDomain() + " - " + vehicle.getBrand() + " " + vehicle.getModel());

        InsuranceVehicle insurance = createInsuranceData();
        insurance.setVehicleId(vehicleId);

        insuranceService.insert(insurance);
    }

    private InsuranceVehicle createInsuranceData() {
        System.out.print("Insurance Name (max 80 chars): ");
        String insuranceName = scanner.nextLine().trim();

        System.out.print("Policy Number (max 50 chars): ");
        String policyNumber = scanner.nextLine().trim();

        System.out.println("\nCover Types:");
        System.out.println("1. RC - Responsabilidad Civil");
        System.out.println("2. TERCEROS - Terceros Completos");
        System.out.println("3. TODO_RIESGO - Todo Riesgo");
        System.out.print("Select cover type (1-3): ");
        String coverOption = scanner.nextLine().trim();

        CoverType cover;
        switch (coverOption) {
            case "1":
                cover = CoverType.RC;
                break;
            case "2":
                cover = CoverType.TERCEROS;
                break;
            case "3":
                cover = CoverType.TODO_RIESGO;
                break;
            default:
                System.out.println("Invalid option. Setting to RC by default.");
                cover = CoverType.RC;
        }

        System.out.print("Expiration Date (YYYY-MM-DD): ");
        LocalDate expirationDate = null;
        try {
            expirationDate = LocalDate.parse(scanner.nextLine().trim(), dateFormatter);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Using current date + 1 year.");
            expirationDate = LocalDate.now().plusYears(1);
        }

        return new InsuranceVehicle(null, insuranceName, policyNumber, cover, expirationDate);
    }

    private void listAllInsurances() throws Exception {
        System.out.println("\n--- ALL INSURANCES ---");

        List<InsuranceVehicle> insurances = insuranceService.getAll();

        if (insurances.isEmpty()) {
            System.out.println("No insurances found.");
            return;
        }

        System.out.println("\n" + "=".repeat(110));
        System.out.printf("%-5s %-12s %-25s %-20s %-15s %-15s%n",
                "ID", "IS_ACTIVE", "INSURANCE NAME", "POLICY NUMBER", "COVER", "EXPIRATION");
        System.out.println("=".repeat(110));

        for (InsuranceVehicle ins : insurances) {
            String isActive = ins.getIsActive() ? "Active" : "Inactive";
            System.out.printf("%-5d %-12s %-25s %-20s %-15s %-15s%n",
                    ins.getId(), isActive, ins.getInsuranceName(),
                    ins.getPolicyNumber(), ins.getCover(), ins.getExpirationDate());
        }

        System.out.println("=".repeat(110));
        System.out.println("Total insurances: " + insurances.size());
    }

    private void viewInsuranceById() throws Exception {
        System.out.println("\n--- VIEW INSURANCE BY ID ---");

        System.out.print("Enter Insurance ID: ");
        Long id = parseLong(scanner.nextLine().trim());

        if (id == null) {
            System.out.println("Invalid ID.");
            return;
        }

        InsuranceVehicle insurance = insuranceService.getById(id);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("INSURANCE DETAILS");
        System.out.println("=".repeat(60));
        System.out.println("ID:              " + insurance.getId());
        System.out.println("Vehicle ID:      " + insurance.getVehicleId());
        System.out.println("Insurance Name:  " + insurance.getInsuranceName());
        System.out.println("Policy Number:   " + insurance.getPolicyNumber());
        System.out.println("Cover:           " + insurance.getCover() + " - " + insurance.getCover().getDescription());
        System.out.println("Expiration Date: " + insurance.getExpirationDate());
        System.out.println("Active:          " + insurance.getIsActive());
        System.out.println("=".repeat(60));
    }

    private void updateInsurance() throws Exception {
        System.out.println("\n--- UPDATE INSURANCE ---");

        System.out.print("Enter Insurance ID to update: ");
        Long id = parseLong(scanner.nextLine().trim());

        if (id == null) {
            System.out.println("Invalid ID.");
            return;
        }

        InsuranceVehicle insurance = insuranceService.getById(id);

        System.out.println("\nCurrent data:");
        System.out.println("Insurance Name: " + insurance.getInsuranceName());
        System.out.println("Policy Number: " + insurance.getPolicyNumber());
        System.out.println("Cover: " + insurance.getCover());
        System.out.println("Expiration Date: " + insurance.getExpirationDate());

        System.out.println("\nEnter new data (leave blank to keep current value):");

        System.out.print("New Insurance Name: ");
        String insuranceName = scanner.nextLine().trim();
        if (!insuranceName.isEmpty()) {
            insurance.setInsuranceName(insuranceName);
        }

        System.out.print("New Policy Number: ");
        String policyNumber = scanner.nextLine().trim();
        if (!policyNumber.isEmpty()) {
            insurance.setPolicyNumber(policyNumber);
        }

        System.out.println("\nCover Types:");
        System.out.println("1. RC - Responsabilidad Civil");
        System.out.println("2. TERCEROS - Terceros Completos");
        System.out.println("3. TODO_RIESGO - Todo Riesgo");
        System.out.print("New Cover Type (1-3, blank to keep current): ");
        String coverOption = scanner.nextLine().trim();

        if (!coverOption.isEmpty()) {
            switch (coverOption) {
                case "1":
                    insurance.setCover(CoverType.RC);
                    break;
                case "2":
                    insurance.setCover(CoverType.TERCEROS);
                    break;
                case "3":
                    insurance.setCover(CoverType.TODO_RIESGO);
                    break;
            }
        }

        System.out.print("New Expiration Date (YYYY-MM-DD, blank to keep current): ");
        String dateStr = scanner.nextLine().trim();
        if (!dateStr.isEmpty()) {
            try {
                LocalDate newDate = LocalDate.parse(dateStr, dateFormatter);
                insurance.setExpirationDate(newDate);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Keeping current value.");
            }
        }

        insuranceService.update(insurance);
    }

    private void deleteInsurance() throws Exception {
        System.out.println("\n--- DELETE INSURANCE (LOGICAL) ---");

        System.out.print("Enter Insurance ID to delete: ");
        Long id = parseLong(scanner.nextLine().trim());

        if (id == null) {
            System.out.println("Invalid ID.");
            return;
        }

        InsuranceVehicle insurance = insuranceService.getById(id);
        System.out.println("\nInsurance to delete: " + insurance.getInsuranceName() + " - " + insurance.getPolicyNumber());

        System.out.print("Are you sure? (Y/N): ");
        String confirm = scanner.nextLine().trim().toUpperCase();

        if (confirm.equals("Y")) {
            insuranceService.delete(id);
        } else {
            System.out.println("Operation cancelled.");
        }
    }

    // ==================== SEARCH MENU ====================

    private void searchMenu() {
        boolean back = false;

        while (!back) {
            printSearchMenu();
            String option = scanner.nextLine().trim();

            try {
                switch (option) {
                    case "1":
                        searchVehicleByDomain();
                        break;
                    case "2":
                        searchInsuranceByPolicyNumber();
                        break;
                    case "3":
                        searchInsuranceByVehicleId();
                        break;
                    case "0":
                        back = true;
                        break;
                    default:
                        System.out.println("\nInvalid option. Please try again.");
                }
            } catch (Exception e) {
                System.err.println("\n✗ Error: " + e.getMessage());
            }

            if (!back) {
                System.out.println("\nPress ENTER to continue...");
                scanner.nextLine();
            }
        }
    }

    private void printSearchMenu() {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("              SEARCH");
        System.out.println("-".repeat(50));
        System.out.println("1. Search Vehicle by Domain");
        System.out.println("2. Search Insurance by Policy Number");
        System.out.println("3. Search Insurance by Vehicle ID");
        System.out.println("0. Back to Main Menu");
        System.out.println("-".repeat(50));
        System.out.print("Select an option: ");
    }

    private void searchVehicleByDomain() throws Exception {
        System.out.println("\n--- SEARCH VEHICLE BY PLATE ---");

        System.out.print("Enter Domain: ");
        String domain = scanner.nextLine().trim().toUpperCase();

        Vehicle vehicle = vehicleService.findByDomain(domain);

        if (vehicle == null) {
            System.out.println("\nNo vehicle found with domain: " + domain);
            return;
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("VEHICLE FOUND");
        System.out.println("=".repeat(60));
        System.out.println("ID:             " + vehicle.getVehicleId());
        System.out.println("Domain:          " + vehicle.getDomain());
        System.out.println("Brand:          " + vehicle.getBrand());
        System.out.println("Model:          " + vehicle.getModel());
        System.out.println("Year:           " + vehicle.getYear());
        System.out.println("Chassis Number: " + (vehicle.getChassisNumber() != null ? vehicle.getChassisNumber() : "N/A"));

        if (vehicle.getInsurance() != null) {
            InsuranceVehicle ins = vehicle.getInsurance();
            System.out.println("\n--- INSURANCE INFORMATION ---");
            System.out.println("Insurance Name:   " + ins.getInsuranceName());
            System.out.println("Policy Number:    " + ins.getPolicyNumber());
            System.out.println("Cover:            " + ins.getCover() + " - " + ins.getCover().getDescription());
            System.out.println("Expiration Date:  " + ins.getExpirationDate());
        } else {
            System.out.println("\nNo insurance assigned.");
        }

        System.out.println("=".repeat(60));
    }

    private void searchInsuranceByPolicyNumber() throws Exception {
        System.out.println("\n--- SEARCH INSURANCE BY POLICY NUMBER ---");

        System.out.print("Enter Policy Number: ");
        String policyNumber = scanner.nextLine().trim();

        InsuranceVehicle insurance = insuranceService.findByPolicyNumber(policyNumber);

        if (insurance == null) {
            System.out.println("\nNo insurance found with policy number: " + policyNumber);
            return;
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("INSURANCE FOUND");
        System.out.println("=".repeat(60));
        System.out.println("ID:              " + insurance.getId());
        System.out.println("Vehicle ID:      " + insurance.getVehicleId());
        System.out.println("Insurance Name:  " + insurance.getInsuranceName());
        System.out.println("Policy Number:   " + insurance.getPolicyNumber());
        System.out.println("Cover:           " + insurance.getCover() + " - " + insurance.getCover().getDescription());
        System.out.println("Expiration Date: " + insurance.getExpirationDate());
        System.out.println("=".repeat(60));
    }

    private void searchInsuranceByVehicleId() throws Exception {
        System.out.println("\n--- SEARCH INSURANCE BY VEHICLE ID ---");

        System.out.print("Enter Vehicle ID: ");
        Long vehicleId = parseLong(scanner.nextLine().trim());

        if (vehicleId == null) {
            System.out.println("Invalid Vehicle ID.");
            return;
        }

        InsuranceVehicle insurance = insuranceService.findByVehicleId(vehicleId);

        if (insurance == null) {
            System.out.println("\nNo insurance found for vehicle ID: " + vehicleId);
            return;
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("INSURANCE FOUND");
        System.out.println("=".repeat(60));
        System.out.println("ID:              " + insurance.getId());
        System.out.println("Vehicle ID:      " + insurance.getVehicleId());
        System.out.println("Insurance Name:  " + insurance.getInsuranceName());
        System.out.println("Policy Number:   " + insurance.getPolicyNumber());
        System.out.println("Cover:           " + insurance.getCover() + " - " + insurance.getCover().getDescription());
        System.out.println("Expiration Date: " + insurance.getExpirationDate());
        System.out.println("=".repeat(60));
    }

    // ==================== HELPER METHODS ====================

    private Long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
