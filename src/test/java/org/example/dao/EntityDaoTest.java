package org.example.dao;

import org.example.model.Entity;
import org.example.model.EntityStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EntityDaoTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private EntityDao entityDao;

    @BeforeEach
    void setUp() throws SQLException {
        entityDao = new EntityDao();
        when(DatabaseConnection.getConnection()).thenReturn(connection);
    }

    @Test
    void testSave() throws SQLException {
        Entity entity = new Entity("Test", "Desc");

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        entityDao.save(entity);

        verify(preparedStatement).setString(1, entity.getId().toString());
        verify(preparedStatement).setString(2, "Test");
        verify(preparedStatement).setString(3, "Desc");
        verify(preparedStatement).setString(4, "ACTIVE");
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void testFindById() throws SQLException {
        UUID id = UUID.randomUUID();
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);

        when(resultSet.getString("id")).thenReturn(id.toString());
        when(resultSet.getString("name")).thenReturn("Test");
        when(resultSet.getString("description")).thenReturn("Desc");
        when(resultSet.getString("status")).thenReturn("ACTIVE");
        when(resultSet.getLong("created_at")).thenReturn(System.currentTimeMillis());
        when(resultSet.getLong("updated_at")).thenReturn(System.currentTimeMillis());

        Entity result = entityDao.findById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Test", result.getName());
    }

    @Test
    void testUpdate() throws SQLException {
        Entity entity = new Entity("Updated", "New Desc");
        UUID id = UUID.randomUUID();
        entity.setId(id);

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        entityDao.update(entity);

        verify(preparedStatement).setString(1, "Updated");
        verify(preparedStatement).setString(2, "New Desc");
        verify(preparedStatement).setString(3, "ACTIVE");
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void testDelete() throws SQLException {
        UUID id = UUID.randomUUID();
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        entityDao.delete(id);

        verify(preparedStatement).setString(1, id.toString());
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void testFindAllWithFilters() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);

        when(resultSet.getString("id")).thenReturn(UUID.randomUUID().toString());
        when(resultSet.getString("name")).thenReturn("Test");
        when(resultSet.getString("description")).thenReturn("Desc");
        when(resultSet.getString("status")).thenReturn("ACTIVE");
        when(resultSet.getLong("created_at")).thenReturn(System.currentTimeMillis());
        when(resultSet.getLong("updated_at")).thenReturn(System.currentTimeMillis());

        var result = entityDao.findAll("search", EntityStatus.ACTIVE, "name", 0, 10);

        assertEquals(2, result.size());
        verify(preparedStatement).setString(1, "%search%");
        verify(preparedStatement).setString(2, "%search%");
        verify(preparedStatement).setString(3, "ACTIVE");
        verify(preparedStatement).setInt(4, 10);
        verify(preparedStatement).setInt(5, 0);
    }

    @Test
    void testCount() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(5);

        int count = entityDao.count("search", EntityStatus.ACTIVE);

        assertEquals(5, count);
    }
}