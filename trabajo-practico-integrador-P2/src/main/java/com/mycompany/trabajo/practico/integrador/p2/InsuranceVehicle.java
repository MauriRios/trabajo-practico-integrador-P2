package com.mycompany.trabajo.practico.integrador.p2;

import java.time.LocalDate;
import java.util.List;

public class InsuranceVehicle {

    private Long insurance_vehicle_id;
    private boolean isActive;
    private String insurance_name;
    private String policy_number;
    private Enum cover;
    private LocalDate expire_date;
    private Vehicle insurance_vehicle; // Relación 1 a 1

    public void create(InsuranceVehicle insurance) {
        // Método a resolver...
    }

    public InsuranceVehicle getById(Long id) {
        // Método a resolver...
        return null;
    }

    public List<InsuranceVehicle> getAll() {
        // Método a resolver...
        return null;
    }

    public void update(InsuranceVehicle insurance) {
        // Método a resolver...
    }

    public void delete(Long id) {
        // Método a resolver...
    }

}