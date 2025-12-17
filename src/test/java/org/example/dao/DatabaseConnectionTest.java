package org.example.dao;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionTest {

    @Test
    void testGetConnection() throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
        assertNotNull(connection);
        assertFalse(connection.isClosed());

        connection.close();
        assertTrue(connection.isClosed());
    }

    @Test
    void testSingletonConnection() throws SQLException {
        Connection conn1 = DatabaseConnection.getConnection();
        Connection conn2 = DatabaseConnection.getConnection();

        assertEquals(conn1, conn2);

        conn1.close();
        assertTrue(conn1.isClosed());
    }

    @Test
    void testDatabaseInitialization() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();

        var stmt = conn.createStatement();
        var rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='entities'");

        assertTrue(rs.next());
        assertEquals("entities", rs.getString("name"));

        rs.close();
        stmt.close();
    }
}