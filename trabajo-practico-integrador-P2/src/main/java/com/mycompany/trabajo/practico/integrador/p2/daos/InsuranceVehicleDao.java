package com.mycompany.trabajo.practico.integrador.p2.daos;

import com.mycompany.trabajo.practico.integrador.p2.entities.InsuranceVehicle;
import com.mycompany.trabajo.practico.integrador.p2.entities.enums.CoverType;
import com.mycompany.trabajo.practico.integrador.p2.exceptions.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InsuranceVehicleDao implements GenericDao<InsuranceVehicle> {

    @Override
    public void create(InsuranceVehicle insurance, Connection conn) throws Exception {
        String sql = "INSERT INTO insurance_vehicle (isActive, insurance_name, " +
                "policy_number, cover, expire_date) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setBoolean(1, insurance.getIsActive());
            stmt.setString(2, insurance.getInsuranceName());
            stmt.setString(3, insurance.getPolicyNumber());
            stmt.setString(4, insurance.getCover().getDescription());
            stmt.setDate(5, Date.valueOf(insurance.getExpirationDate()));

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DatabaseException("Creating insurance failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    insurance.setId(generatedKeys.getLong(1));
                } else {
                    throw new DatabaseException("Creating insurance failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new DatabaseException("Vehicle already has an insurance policy assigned.");
            }
            throw new DatabaseException("Error creating insurance: " + e.getMessage(), e);
        }
    }

    @Override
    public InsuranceVehicle findVehicleById(Long id, Connection conn) throws Exception {
        String sql = "SELECT * FROM insurance_vehicle WHERE insurance_vehicle_id = ? AND isActive = true";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToInsurance(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error reading insurance: " + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public List<InsuranceVehicle> readAll(Connection conn) throws Exception {
        List<InsuranceVehicle> insurances = new ArrayList<>();
        String sql = "SELECT * FROM insurance_vehicle WHERE isActive = true ORDER BY insurance_vehicle_id ";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                insurances.add(mapResultSetToInsurance(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error reading all insurances: " + e.getMessage(), e);
        }

        return insurances;
    }

    @Override
    public void update(InsuranceVehicle insurance, Connection conn) throws Exception {
        String sql = "UPDATE insurance_vehicle SET insurance_name = ?, policy_number = ?, " +
                "cover = ?, expire_date = ? WHERE id = ? AND isActive = true";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, insurance.getInsuranceName());
            stmt.setString(2, insurance.getPolicyNumber());
            stmt.setString(4, insurance.getCover().getDescription());
            stmt.setDate(4, Date.valueOf(insurance.getExpirationDate()));
            stmt.setLong(5, insurance.getId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DatabaseException("Updating insurance failed, insurance not found or already deleted.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error updating insurance: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Long id, Connection conn) throws Exception {
        String sql = "UPDATE insurance_vehicle SET isActive = false WHERE insurance_vehicle_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DatabaseException("Deleting insurance failed, insurance not found.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting insurance: " + e.getMessage(), e);
        }
    }

    public InsuranceVehicle findByVehicleId(Long vehicleId, Connection conn) throws Exception {
        String sql = " SELECT * FROM vehicle v " +
                     " JOIN insurance_vehicle iv on v.insurance_vehicle_id = iv.insurance_vehicle_id " +
                     " WHERE v.vehicle_id = ? AND iv.isActive = true ";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, vehicleId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToInsurance(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding insurance by vehicle: " + e.getMessage(), e);
        }

        return null;
    }

    public InsuranceVehicle findByPolicyNumber(String policyNumber, Connection conn) throws Exception {
        String sql = "SELECT * FROM insurance_vehicle WHERE policy_number = ? AND isActive = true";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, policyNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToInsurance(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding insurance by policy number: " + e.getMessage(), e);
        }

        return null;
    }

    private InsuranceVehicle mapResultSetToInsurance(ResultSet rs) throws SQLException {
        InsuranceVehicle insurance = new InsuranceVehicle();
        insurance.setId(rs.getLong("insurance_vehicle_id"));
        insurance.setIsActive(rs.getBoolean("isActive"));
        insurance.setInsuranceName(rs.getString("insurance_name"));
        insurance.setPolicyNumber(rs.getString("policy_number"));
        CoverType cover = getCoverType(rs);
        insurance.setCover(cover);
        insurance.setExpirationDate(rs.getDate("expire_date").toLocalDate());
        return insurance;
    }

    private static CoverType getCoverType(ResultSet rs) throws SQLException {
        String coverStr = rs.getString("cover");
        CoverType cover = Arrays.stream(CoverType.values())
                .filter(c -> c.getDescription().equalsIgnoreCase(coverStr))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Cobertura inválida: " + coverStr)
                );
        return cover;
    }
}