package com.mycompany.trabajo.practico.integrador.p2.services;

import com.mycompany.trabajo.practico.integrador.p2.config.DatabaseConnection;
import com.mycompany.trabajo.practico.integrador.p2.daos.InsuranceVehicleDao;
import com.mycompany.trabajo.practico.integrador.p2.daos.VehicleDao;
import com.mycompany.trabajo.practico.integrador.p2.entities.InsuranceVehicle;
import com.mycompany.trabajo.practico.integrador.p2.entities.Vehicle;
import com.mycompany.trabajo.practico.integrador.p2.exceptions.DatabaseException;
import com.mycompany.trabajo.practico.integrador.p2.exceptions.ValidationException;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class InsuranceVehicleService implements GenericService<InsuranceVehicle> {

    private InsuranceVehicleDao insuranceDao = new InsuranceVehicleDao();
    private VehicleDao vehicleDao = new VehicleDao();


    public void insertWithConnection(InsuranceVehicle insurance, Connection conn) throws Exception {
        validateInsurance(insurance);

        // Verificar que el vehículo existe
        Vehicle vehicle = vehicleDao.findVehicleById(insurance.getVehicleId(), conn);
        if (vehicle == null) {
            throw new ValidationException("Vehicle with ID " + insurance.getVehicleId() + " does not exist.");
        }

        // Verificar que el vehículo no tenga ya un seguro (regla 1→1)
        InsuranceVehicle existing = insuranceDao.findByVehicleId(insurance.getVehicleId(), conn);
        if (existing != null) {
            throw new ValidationException("Vehicle already has an insurance policy assigned. Remove it first.");
        }

        // Verificar que no exista otra póliza con el mismo número
        InsuranceVehicle duplicatePolicy = insuranceDao.findByPolicyNumber(insurance.getPolicyNumber(), conn);
        if (duplicatePolicy != null) {
            throw new ValidationException("Policy number '" + insurance.getPolicyNumber() + "' already exists.");
        }

        insuranceDao.create(insurance, conn);
        System.out.println("Insurance created successfully with ID: " + insurance.getId());
    }

    @Override
    public void insert(InsuranceVehicle insurance) throws Exception {
        validateInsurance(insurance);

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Verificar que el vehículo existe
            Vehicle vehicle = vehicleDao.findVehicleById(insurance.getVehicleId(), conn);
            if (vehicle == null) {
                throw new ValidationException("Vehicle with ID " + insurance.getVehicleId() + " does not exist.");
            }

            // Verificar que el vehículo no tenga ya un seguro
            InsuranceVehicle existing = insuranceDao.findByVehicleId(insurance.getVehicleId(), conn);
            if (existing != null) {
                throw new ValidationException("Vehicle already has an insurance policy assigned. Remove it first.");
            }

            // Verificar que no exista otra póliza con el mismo número
            InsuranceVehicle duplicatePolicy = insuranceDao.findByPolicyNumber(insurance.getPolicyNumber(), conn);
            if (duplicatePolicy != null) {
                throw new ValidationException("Policy number '" + insurance.getPolicyNumber() + "' already exists.");
            }

            // Crear el seguro
            insuranceDao.create(insurance, conn);

            //Actualizar el vehículo con la FK del seguro
            vehicle.setInsurance(insurance);
            vehicleDao.update(vehicle, conn);

            conn.commit();
            System.out.println("Insurance created successfully with ID: " + insurance.getId());
            System.out.println("Vehicle updated with insurance reference.");

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Transaction rolled back due to error.");
                } catch (SQLException ex) {
                    System.err.println("Error during rollback: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    DatabaseConnection.closeConnection(conn);
                } catch (SQLException e) {
                    System.err.println("Error restoring autocommit: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public InsuranceVehicle getById(Long id) throws Exception {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid insurance ID.");
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            InsuranceVehicle insurance = insuranceDao.findVehicleById(id, conn);

            if (insurance == null) {
                throw new DatabaseException("Insurance with ID " + id + " not found.");
            }

            return insurance;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    @Override
    public List<InsuranceVehicle> getAll() throws Exception {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            return insuranceDao.readAll(conn);
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    @Override
    public void update(InsuranceVehicle insurance) throws Exception {
        validateInsurance(insurance);

        if (insurance.getId() == null) {
            throw new ValidationException("Insurance ID is required for update.");
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Verificar que el seguro existe
            InsuranceVehicle existing = insuranceDao.findVehicleById(insurance.getId(), conn);
            if (existing == null) {
                throw new DatabaseException("Insurance with ID " + insurance.getId() + " not found.");
            }

            // Verificar que no haya otra póliza con el mismo número
            InsuranceVehicle duplicatePolicy = insuranceDao.findByPolicyNumber(insurance.getPolicyNumber(), conn);
            if (duplicatePolicy != null && !duplicatePolicy.getId().equals(insurance.getId())) {
                throw new ValidationException("Another insurance with policy number '" + insurance.getPolicyNumber() + "' already exists.");
            }

            insuranceDao.update(insurance, conn);

            conn.commit();
            System.out.println("Insurance updated successfully.");

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Transaction rolled back due to error.");
                } catch (SQLException ex) {
                    System.err.println("Error during rollback: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    DatabaseConnection.closeConnection(conn);
                } catch (SQLException e) {
                    System.err.println("Error restoring autocommit: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void delete(Long id) throws Exception {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid insurance ID.");
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Verificar que el seguro existe
            InsuranceVehicle existing = insuranceDao.findVehicleById(id, conn);
            if (existing == null) {
                throw new DatabaseException("Insurance with ID " + id + " not found.");
            }

            insuranceDao.delete(id, conn);

            conn.commit();
            System.out.println("Insurance deleted successfully (logical deletion).");

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Transaction rolled back due to error.");
                } catch (SQLException ex) {
                    System.err.println("Error during rollback: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    DatabaseConnection.closeConnection(conn);
                } catch (SQLException e) {
                    System.err.println("Error restoring autocommit: " + e.getMessage());
                }
            }
        }
    }

    public InsuranceVehicle findByVehicleId(Long vehicleId) throws Exception {
        if (vehicleId == null || vehicleId <= 0) {
            throw new ValidationException("Invalid vehicle ID.");
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            return insuranceDao.findByVehicleId(vehicleId, conn);
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public InsuranceVehicle findByPolicyNumber(String policyNumber) throws Exception {
        if (policyNumber == null || policyNumber.trim().isEmpty()) {
            throw new ValidationException("Policy number cannot be empty.");
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            return insuranceDao.findByPolicyNumber(policyNumber, conn);
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    private void validateInsurance(InsuranceVehicle insurance) throws ValidationException {
        if (insurance == null) {
            throw new ValidationException("Insurance cannot be null.");
        }

        if (insurance.getVehicleId() == null) {
            throw new ValidationException("Vehicle ID is required.");
        }

        if (insurance.getInsuranceName() == null || insurance.getInsuranceName().trim().isEmpty()) {
            throw new ValidationException("Insurance name is required.");
        }

        if (insurance.getInsuranceName().length() > 80) {
            throw new ValidationException("Insurance name cannot exceed 80 characters.");
        }

        if (insurance.getPolicyNumber() == null || insurance.getPolicyNumber().trim().isEmpty()) {
            throw new ValidationException("Policy number is required.");
        }

        if (insurance.getPolicyNumber().length() > 50) {
            throw new ValidationException("Policy number cannot exceed 50 characters.");
        }

        if (insurance.getCover() == null) {
            throw new ValidationException("Cover type is required.");
        }

        if (insurance.getExpirationDate() == null) {
            throw new ValidationException("Expiration date is required.");
        }

        if (insurance.getExpirationDate().isBefore(LocalDate.now())) {
            throw new ValidationException("Expiration date cannot be in the past.");
        }
    }
}
