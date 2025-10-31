package com.petstore.db;

import com.petstore.models.PetInfo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {

    private static final String DB_URL = "jdbc:sqlite:petstore.db";

    public DatabaseHelper() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS pets (
                    id INTEGER PRIMARY KEY,
                    name TEXT,
                    status TEXT,
                    category_name TEXT
                )
            """);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public void savePet(PetInfo pet) {
        String sql = "INSERT OR REPLACE INTO pets (id, name, status, category_name) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, pet.getId());
            ps.setString(2, pet.getName());
            ps.setString(3, pet.getStatus());
            String categoryName = (pet.getCategory() != null) ? pet.getCategory().getName() : null;
            ps.setString(4, categoryName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void savePets(List<PetInfo> pets) {
        try (Connection conn = connect()) {
            conn.setAutoCommit(false);
            String sql = "INSERT OR REPLACE INTO pets (id, name, status, category_name) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (PetInfo pet : pets) {
                    ps.setLong(1, pet.getId());
                    ps.setString(2, pet.getName());
                    ps.setString(3, pet.getStatus());
                    String categoryName = (pet.getCategory() != null) ? pet.getCategory().getName() : null;
                    ps.setString(4, categoryName);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<PetInfo> loadAllPets() {
        List<PetInfo> pets = new ArrayList<>();
        String sql = "SELECT id, name, status, category_name FROM pets ORDER BY id";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                PetInfo pet = new PetInfo();
                pet.setId(rs.getLong("id"));
                pet.setName(rs.getString("name"));
                pet.setStatus(rs.getString("status"));

                String categoryName = rs.getString("category_name");
                if (categoryName != null) {
                    PetInfo.Category cat = new PetInfo.Category();
                    cat.setName(categoryName);
                    pet.setCategory(cat);
                }
                pets.add(pet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pets;
    }
}