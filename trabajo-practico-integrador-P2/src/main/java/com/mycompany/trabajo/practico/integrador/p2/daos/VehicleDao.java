package com.mycompany.trabajo.practico.integrador.p2.daos;



import com.mycompany.trabajo.practico.integrador.p2.entities.InsuranceVehicle;
import com.mycompany.trabajo.practico.integrador.p2.entities.Vehicle;
import com.mycompany.trabajo.practico.integrador.p2.exceptions.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleDao implements GenericDao<Vehicle> {

    private InsuranceVehicleDao insuranceDao = new InsuranceVehicleDao();

    @Override
    public void create(Vehicle vehicle, Connection conn) throws Exception {
        String sql = "INSERT INTO Vehicle (isActive, domain, brand, model, year, chasis_number, insurance_vehicle_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setBoolean(1, vehicle.getIsActive());
            stmt.setString(2, vehicle.getDomain().toUpperCase());
            stmt.setString(3, vehicle.getBrand());
            stmt.setString(4, vehicle.getModel());
            stmt.setInt(5, vehicle.getYear());
            stmt.setString(6, vehicle.getChassisNumber());

            if (vehicle.getInsurance() != null && vehicle.getInsurance().getId() != null) {
                stmt.setLong(7, vehicle.getInsurance().getId());
            } else {
                stmt.setNull(7, Types.BIGINT);
            }

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DatabaseException("Creating vehicle failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    vehicle.setVehicleId(generatedKeys.getLong(1));
                } else {
                    throw new DatabaseException("Creating vehicle failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error creating vehicle: " + e.getMessage(), e);
        }
    }

    @Override
    public Vehicle read(Long id, Connection conn) throws Exception {
        String sql = "SELECT * FROM vehicle WHERE vehicle_id = ? AND isActive = true";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Vehicle vehicle = mapResultSetToVehicle(rs);

                    // Cargar el seguro asociado si existe
                    InsuranceVehicle insurance = insuranceDao.findByVehicleId(vehicle.getVehicleId(), conn);
                    vehicle.setInsurance(insurance);

                    return vehicle;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error reading vehicle: " + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public List<Vehicle> readAll(Connection conn) throws Exception {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicle WHERE isActive = true ORDER BY vehicle_id";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Vehicle vehicle = mapResultSetToVehicle(rs);

                // Cargar el seguro asociado si existe
                Long vehicleId = vehicle.getVehicleId();
                InsuranceVehicle insurance = insuranceDao.findByVehicleId(vehicleId, conn);
                vehicle.setInsurance(insurance);

                vehicles.add(vehicle);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error reading all vehicles: " + e.getMessage(), e);
        }

        return vehicles;
    }

    @Override
    public void update(Vehicle vehicle, Connection conn) throws Exception {
        String sql = "UPDATE Vehicle SET domain = ?, brand = ?, model = ?, year = ?, " +
                "chasis_number = ?, insurance_vehicle_id = ? WHERE vehicle_id = ? AND isActive = true";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, vehicle.getDomain().toUpperCase());
            stmt.setString(2, vehicle.getBrand());
            stmt.setString(3, vehicle.getModel());
            stmt.setInt(4, vehicle.getYear());
            stmt.setString(5, vehicle.getChassisNumber());

            // Actualizar la FK del seguro
            if (vehicle.getInsurance() != null && vehicle.getInsurance().getId() != null) {
                stmt.setLong(6, vehicle.getInsurance().getId());
            } else {
                stmt.setNull(6, Types.BIGINT);
            }

            stmt.setLong(7, vehicle.getVehicleId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DatabaseException("Updating vehicle failed, vehicle not found or already deleted.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error updating vehicle: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Long id, Connection conn) throws Exception {
        String sql = "UPDATE Vehicle SET isActive = false WHERE vehicle_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DatabaseException("Deleting vehicle failed, vehicle not found.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting vehicle: " + e.getMessage(), e);
        }
    }

    public Vehicle findByDomain(String domain, Connection conn) throws Exception {
        String sql = "SELECT * FROM vehicle WHERE domain = ? AND isActive = true";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, domain.toUpperCase());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Vehicle vehicle = mapResultSetToVehicle(rs);

                    // Cargar el seguro asociado si existe
                    InsuranceVehicle insurance = insuranceDao.findByVehicleId(vehicle.getVehicleId(), conn);
                    vehicle.setInsurance(insurance);

                    return vehicle;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding vehicle by domain: " + e.getMessage(), e);
        }

        return null;
    }

    private Vehicle mapResultSetToVehicle(ResultSet rs) throws SQLException {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleId(rs.getLong("vehicle_id"));
        vehicle.setIsActive(rs.getBoolean("isActive"));
        vehicle.setDomain(rs.getString("domain"));
        vehicle.setBrand(rs.getString("brand"));
        vehicle.setModel(rs.getString("model"));
        vehicle.setYear(rs.getInt("year"));
        vehicle.setChassisNumber(rs.getString("chasis_number"));
        return vehicle;
    }
}

