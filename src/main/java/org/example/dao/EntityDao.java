package org.example.dao;

import org.example.model.Entity;
import org.example.model.EntityStatus;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EntityDao {

    public void save(Entity entity) throws SQLException {
        String sql = "INSERT INTO entities (id, name, description, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, entity.getId().toString());
            pstmt.setString(2, entity.getName());
            pstmt.setString(3, entity.getDescription());
            pstmt.setString(4, entity.getStatus().name());
            pstmt.setLong(5, entity.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            pstmt.setLong(6, entity.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

            pstmt.executeUpdate();
        }
    }

    public Entity findById(UUID id) throws SQLException {
        String sql = "SELECT * FROM entities WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id.toString());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToEntity(rs);
            }
        }
        return null;
    }

    public List<Entity> findAll() throws SQLException {
        return findAll(null, null, null, 0, Integer.MAX_VALUE);
    }

    public List<Entity> findAll(String search, EntityStatus status, String sortBy, int offset, int limit) throws SQLException {
        List<Entity> entities = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM entities WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (name LIKE ? OR description LIKE ?)");
            String searchPattern = "%" + search + "%";
            params.add(searchPattern);
            params.add(searchPattern);
        }

        if (status != null) {
            sql.append(" AND status = ?");
            params.add(status.name());
        }

        if (sortBy != null) {
            switch (sortBy) {
                case "name":
                    sql.append(" ORDER BY name");
                    break;
                case "createdAt":
                    sql.append(" ORDER BY created_at");
                    break;
                case "updatedAt":
                    sql.append(" ORDER BY updated_at DESC");
                    break;
                default:
                    sql.append(" ORDER BY created_at DESC");
            }
        } else {
            sql.append(" ORDER BY created_at DESC");
        }

        sql.append(" LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                entities.add(mapResultSetToEntity(rs));
            }
        }
        return entities;
    }

    public int count(String search, EntityStatus status) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM entities WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (name LIKE ? OR description LIKE ?)");
            String searchPattern = "%" + search + "%";
            params.add(searchPattern);
            params.add(searchPattern);
        }

        if (status != null) {
            sql.append(" AND status = ?");
            params.add(status.name());
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public void update(Entity entity) throws SQLException {
        String sql = "UPDATE entities SET name = ?, description = ?, status = ?, updated_at = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            entity.setUpdatedAt(LocalDateTime.now());

            pstmt.setString(1, entity.getName());
            pstmt.setString(2, entity.getDescription());
            pstmt.setString(3, entity.getStatus().name());
            pstmt.setLong(4, entity.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            pstmt.setString(5, entity.getId().toString());

            pstmt.executeUpdate();
        }
    }

    public void delete(UUID id) throws SQLException {
        String sql = "DELETE FROM entities WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id.toString());
            pstmt.executeUpdate();
        }
    }

    private Entity mapResultSetToEntity(ResultSet rs) throws SQLException {
        Entity entity = new Entity();

        String idStr = rs.getString("id");
        entity.setId(UUID.fromString(idStr));

        entity.setName(rs.getString("name"));
        entity.setDescription(rs.getString("description"));

        String statusStr = rs.getString("status");
        entity.setStatus(EntityStatus.valueOf(statusStr));

        long createdAtMillis = rs.getLong("created_at");
        entity.setCreatedAt(LocalDateTime.ofInstant(
                Instant.ofEpochMilli(createdAtMillis),
                ZoneId.systemDefault()
        ));

        long updatedAtMillis = rs.getLong("updated_at");
        entity.setUpdatedAt(LocalDateTime.ofInstant(
                Instant.ofEpochMilli(updatedAtMillis),
                ZoneId.systemDefault()
        ));

        return entity;
    }
}