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
import java.util.List;

public class VehicleService implements GenericService<Vehicle> {

    private VehicleDao vehicleDao = new VehicleDao();
    private InsuranceVehicleDao insuranceDao = new InsuranceVehicleDao();

    @Override
    public void insert(Vehicle vehicle) throws Exception {
        validateVehicle(vehicle);

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Verificar dominio duplicado
            Vehicle existing = vehicleDao.findByDomain(vehicle.getDomain(), conn);
            if (existing != null) {
                throw new ValidationException("A vehicle with domain '" + vehicle.getDomain() + "' already exists.");
            }

            // Crear el vehículo primero (sin seguro)
            InsuranceVehicle tempInsurance = vehicle.getInsurance();
            vehicle.setInsurance(null);

            vehicleDao.create(vehicle, conn);
            System.out.println("Vehicle created successfully with ID: " + vehicle.getVehicleId());

            // Si tenía seguro, delegar su creación al InsuranceVehicleService
            if (tempInsurance != null) {
                tempInsurance.setVehicleId(vehicle.getVehicleId());

                InsuranceVehicleService insuranceService = new InsuranceVehicleService();
                insuranceService.insertWithConnection(tempInsurance, conn);

                // Actualizar el vehículo con la FK del seguro
                vehicle.setInsurance(tempInsurance);
                vehicleDao.update(vehicle, conn);
                System.out.println("Vehicle linked to insurance.");
            }

            conn.commit();

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
    public Vehicle getById(Long id) throws Exception {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid vehicle ID.");
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            Vehicle vehicle = vehicleDao.findVehicleById(id, conn);

            if (vehicle == null) {
                throw new DatabaseException("Vehicle with ID " + id + " not found.");
            }

            return vehicle;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    @Override
    public List<Vehicle> getAll() throws Exception {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            return vehicleDao.readAll(conn);
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    @Override
    public void update(Vehicle vehicle) throws Exception {
        validateVehicle(vehicle);

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Verificar que el vehículo existe
            Vehicle existing = vehicleDao.findVehicleById(vehicle.getVehicleId(), conn);
            if (existing == null) {
                throw new DatabaseException("Vehicle with ID " + vehicle.getVehicleId() + " not found.");
            }

            // Verificar que no haya otra patente duplicada
            Vehicle duplicateDomain = vehicleDao.findByDomain(vehicle.getDomain(), conn);
            if (duplicateDomain != null && !duplicateDomain.getVehicleId().equals(vehicle.getVehicleId())) {
                throw new ValidationException("Another vehicle with domain '" + vehicle.getDomain() + "' already exists.");
            }

            vehicleDao.update(vehicle, conn);

            conn.commit();
            System.out.println("Vehicle updated successfully.");

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
            throw new ValidationException("Invalid vehicle ID.");
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Verificar que el vehículo existe
            Vehicle existing = vehicleDao.findVehicleById(id, conn);
            if (existing == null) {
                throw new DatabaseException("Vehicle with ID " + id + " not found.");
            }

            // Eliminar el seguro asociado si existe (baja lógica)
            InsuranceVehicle insurance = insuranceDao.findByVehicleId(id, conn);
            if (insurance != null) {
                insuranceDao.delete(insurance.getId(), conn);
            }

            // Eliminar el vehículo (baja lógica)
            vehicleDao.delete(id, conn);

            conn.commit();
            System.out.println("Vehicle deleted successfully (logical deletion).");

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

    public Vehicle findByDomain(String domain) throws Exception {
        if (domain == null || domain.trim().isEmpty()) {
            throw new ValidationException("Domain cannot be empty.");
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            return vehicleDao.findByDomain(domain.toUpperCase(), conn);
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    private void validateVehicle(Vehicle vehicle) throws ValidationException {
        if (vehicle.getVehicleId() == null) {
            throw new ValidationException("Vehicle ID is required for update.");
        }

        if (vehicle == null) {
            throw new ValidationException("Vehicle cannot be null.");
        }

        if (vehicle.getDomain() == null || vehicle.getDomain().trim().isEmpty()) {
            throw new ValidationException("Domain is required.");
        }

        if (vehicle.getDomain().length() > 10) {
            throw new ValidationException("Domain cannot exceed 10 characters.");
        }

        if (vehicle.getBrand() == null || vehicle.getBrand().trim().isEmpty()) {
            throw new ValidationException("Brand is required.");
        }

        if (vehicle.getBrand().length() > 50) {
            throw new ValidationException("Brand cannot exceed 50 characters.");
        }

        if (vehicle.getModel() == null || vehicle.getModel().trim().isEmpty()) {
            throw new ValidationException("Model is required.");
        }

        if (vehicle.getModel().length() > 50) {
            throw new ValidationException("Model cannot exceed 50 characters.");
        }

        if (vehicle.getYear() != null) {
            int currentYear = java.time.Year.now().getValue();
            if (vehicle.getYear() < 1900 || vehicle.getYear() > currentYear + 1) {
                throw new ValidationException("Year must be between 1900 and " + (currentYear + 1) + ".");
            }
        }

        if (vehicle.getChassisNumber() != null && vehicle.getChassisNumber().length() > 50) {
            throw new ValidationException("Chassis number cannot exceed 50 characters.");
        }
    }
}
