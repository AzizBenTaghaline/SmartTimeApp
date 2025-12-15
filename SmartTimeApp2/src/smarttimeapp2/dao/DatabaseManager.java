package smarttimeapp2.dao;

import java.sql.*;

public class DatabaseManager {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/smarttime_db";
    private static final String DB_USER = "root";  
    private static final String DB_PASSWORD = "123456";
    
    private Connection connection;
    
    public DatabaseManager() throws SQLException {
        connect();
        createTables();
    }
    
    private void connect() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("✓ Connected to MySQL database: smarttime_db");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }
    
    private void createTables() throws SQLException {
        String createAppareilsTable = """
            CREATE TABLE IF NOT EXISTS appareils (
                nom VARCHAR(255) PRIMARY KEY,
                modele VARCHAR(255) NOT NULL,
                systeme VARCHAR(50) NOT NULL,
                niveau_batterie INT NOT NULL,
                derniere_utilisation DATETIME NOT NULL,
                type VARCHAR(50) NOT NULL,
                numero_serie VARCHAR(255),
                ram_go INT,
                type_ordinateur VARCHAR(50)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
        """;
        
        String createSessionsTable = """
            CREATE TABLE IF NOT EXISTS sessions (
                id VARCHAR(36) PRIMARY KEY,
                debut DATETIME NOT NULL,
                fin DATETIME NOT NULL,
                appareil_nom VARCHAR(255) NOT NULL,
                application VARCHAR(255) NOT NULL,
                FOREIGN KEY (appareil_nom) REFERENCES appareils(nom) ON DELETE CASCADE,
                INDEX idx_debut (debut),
                INDEX idx_appareil (appareil_nom)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createAppareilsTable);
            stmt.execute(createSessionsTable);
            System.out. println("MySQL database tables created/verified");
        }
    }
    
    public Connection getConnection() {
        return connection;
    }
    
    public void close() throws SQLException {
        if (connection != null && !connection. isClosed()) {
            connection.close();
            System.out.println("✓ Database connection closed");
        }
    }
}