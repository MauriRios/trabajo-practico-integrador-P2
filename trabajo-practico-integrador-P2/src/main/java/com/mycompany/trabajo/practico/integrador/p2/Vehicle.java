package com.mycompany.trabajo.practico.integrador.p2;

import java.util.List;

public class Vehicle {

    private Long vehicle_id;
    private boolean isActive;
    private String domain;
    private String brand;
    private String model;
    private int year;
    private String chasis_number;
    private InsuranceVehicle insurance_vehicle_id; // Relación 1 a 1

    public void create(Vehicle vehicle) {
        // Método a resolver...
    }

    public Vehicle getById(Long id) {
        // Método a resolver...
        return null;
    }

    public InsuranceVehicle getByDomain(String string) {
        // Método a resolver...
        return null;
    }

    public List<Vehicle> getAll() {
        // Método a resolver...
        return null;
    }

    public void update(Vehicle vehicle) {
        // Método a resolver...
    }

    public void delete(Long id) {
        // Método a resolver...
    }

}