package org.example.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:crud_app.db";
    private static Connection connection;

    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL);
            initializeDatabase();
        }
        return connection;
    }

    private static void initializeDatabase() {
        try {
            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS entities (
                    id TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    description TEXT,
                    status TEXT NOT NULL,
                    created_at INTEGER NOT NULL,
                    updated_at INTEGER NOT NULL,
                    CONSTRAINT name_length CHECK (length(name) >= 3 AND length(name) <= 50),
                    CONSTRAINT description_length CHECK (length(description) <= 255)
                )
                """;

            String index1SQL = "CREATE INDEX IF NOT EXISTS idx_name ON entities(name)";
            String index2SQL = "CREATE INDEX IF NOT EXISTS idx_status ON entities(status)";
            String index3SQL = "CREATE INDEX IF NOT EXISTS idx_created_at ON entities(created_at)";

            try (Statement stmt = connection.createStatement()) {
                stmt.execute(createTableSQL);
                stmt.execute(index1SQL);
                stmt.execute(index2SQL);
                stmt.execute(index3SQL);

                String countSQL = "SELECT COUNT(*) FROM entities";
                var rs = stmt.executeQuery(countSQL);
                if (rs.next() && rs.getInt(1) == 0) {
                    long now = System.currentTimeMillis();

                    String insertTestDataSQL = String.format("""
                        INSERT INTO entities (id, name, description, status, created_at, updated_at) 
                        VALUES 
                            ('%s', 'Первая задача', 'Описание первой задачи', 'ACTIVE', %d, %d),
                            ('%s', 'Вторая задача', 'Описание второй задачи', 'ACTIVE', %d, %d),
                            ('%s', 'Третья задача', 'Описание третьей задачи', 'INACTIVE', %d, %d)
                        """,
                            UUID.randomUUID().toString(), now - 86400000, now - 86400000,
                            UUID.randomUUID().toString(), now - 43200000, now - 43200000,
                            UUID.randomUUID().toString(), now - 21600000, now - 21600000
                    );

                    stmt.execute(insertTestDataSQL);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}