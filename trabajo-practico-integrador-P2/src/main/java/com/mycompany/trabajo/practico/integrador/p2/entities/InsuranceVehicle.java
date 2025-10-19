package com.mycompany.trabajo.practico.integrador.p2.entities;

import com.mycompany.trabajo.practico.integrador.p2.entities.enums.CoverType;

import java.time.LocalDate;

public class InsuranceVehicle {
    private Long id;
    private Boolean isActive;
    private Long vehicleId;
    private String insuranceName;
    private String policyNumber;
    private CoverType cover;
    private LocalDate expirationDate;

    // Constructor vacío
    public InsuranceVehicle() {
        this.isActive = true;
    }

    // Constructor completo
    public InsuranceVehicle(Long id, Boolean isActive, Long vehicleId, String insuranceName,
                            String policyNumber, CoverType cover, LocalDate expirationDate) {
        this.id = id;
        this.isActive = isActive != null ? isActive : true;
        this.vehicleId = vehicleId;
        this.insuranceName = insuranceName;
        this.policyNumber = policyNumber;
        this.cover = cover;
        this.expirationDate = expirationDate;
    }

    // Constructor sin ID (para crear)
    public InsuranceVehicle(Long vehicleId, String insuranceName, String policyNumber,
                            CoverType cover, LocalDate expirationDate) {
        this.isActive = true;
        this.vehicleId = vehicleId;
        this.insuranceName = insuranceName;
        this.policyNumber = policyNumber;
        this.cover = cover;
        this.expirationDate = expirationDate;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }

    public String getInsuranceName() { return insuranceName; }
    public void setInsuranceName(String insuranceName) { this.insuranceName = insuranceName; }

    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }

    public CoverType getCover() { return cover; }
    public void setCover(CoverType cover) { this.cover = cover; }

    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }

    @Override
    public String toString() {
        return String.format("InsuranceVehicle{id=%d, insuranceName='%s', policyNumber='%s', " +
                        "cover=%s, expirationDate=%s, isActive=%s}",
                id, insuranceName, policyNumber, cover, expirationDate, isActive);
    }
}