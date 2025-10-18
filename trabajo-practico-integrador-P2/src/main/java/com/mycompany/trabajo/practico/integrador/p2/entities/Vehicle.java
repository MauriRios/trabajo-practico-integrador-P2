package com.mycompany.trabajo.practico.integrador.p2.entities;

public class Vehicle {

    private Long vehicleId;
    private Boolean isActive;
    private String domain;
    private String brand;
    private String model;
    private Integer year;
    private String chassisNumber;
    private InsuranceVehicle insurance; // Referencia 1→1

    // Constructor vacío
    public Vehicle() {
        this.isActive = true;
    }

    // Constructor completo
    public Vehicle(Long vehicleId, Boolean isActive, String domain, String brand,
                   String model, Integer year, String chassisNumber, InsuranceVehicle insurance) {
        this.vehicleId = vehicleId;
        this.isActive = isActive != null ? isActive : true;
        this.domain = domain;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.chassisNumber = chassisNumber;
        this.insurance = insurance;
    }

    // Constructor sin ID (para crear)
    public Vehicle(String domain, String brand, String model, Integer year, String chassisNumber) {
        this.isActive = true;
        this.domain = domain;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.chassisNumber = chassisNumber;
    }

    // Getters y Setters
    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public String getChassisNumber() { return chassisNumber; }
    public void setChassisNumber(String chassisNumber) { this.chassisNumber = chassisNumber; }

    public InsuranceVehicle getInsurance() { return insurance; }
    public void setInsurance(InsuranceVehicle insurance) { this.insurance = insurance; }

    @Override
    public String toString() {
        String insuranceInfo = insurance != null
                ? String.format("InsuranceVehicle{id=%d, name='%s'}", insurance.getId(), insurance.getInsuranceName())
                : "null";

        return String.format("Vehicle{id=%d, domain='%s', brand='%s', model='%s', year=%d, " +
                        "chassisNumber='%s', insurance=%s, isActive=%s}",
                vehicleId, domain, brand, model, year, chassisNumber,
                insuranceInfo, isActive);
    }

}