package smarttimeapp2.dao;

import smarttimeapp2.model.*;
import java.sql.*;
import java.time. LocalDateTime;
import java.util.UUID;

public class HistoriqueDAO {
    
    private Connection connection;
    private AppareilDAO appareilDAO;
    
    public HistoriqueDAO(Connection connection, AppareilDAO appareilDAO) {
        this. connection = connection;
        this. appareilDAO = appareilDAO;
    }
    
    public void saveSession(Session session) throws SQLException {
        String sql = """
            INSERT INTO sessions (id, debut, fin, appareil_nom, application) 
            VALUES (?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE 
                debut = VALUES(debut),
                fin = VALUES(fin),
                appareil_nom = VALUES(appareil_nom),
                application = VALUES(application)
        """;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, session. id(). toString());
            stmt.setTimestamp(2, Timestamp.valueOf(session.debut()));
            stmt.setTimestamp(3, Timestamp.valueOf(session.fin()));
            stmt.setString(4, session.appareil().nom());
            stmt.setString(5, session.application());
            stmt.executeUpdate();
        }
    }
    
    public Historique loadHistorique(java.util.Map<String, Appareil> appareils) throws SQLException {
        Historique historique = new Historique();
        String sql = "SELECT * FROM sessions ORDER BY debut";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                UUID id = UUID.fromString(rs.getString("id"));
                LocalDateTime debut = rs.getTimestamp("debut").toLocalDateTime();
                LocalDateTime fin = rs.getTimestamp("fin").toLocalDateTime();
                String appareilNom = rs.getString("appareil_nom");
                String application = rs.getString("application");
                
                Appareil appareil = appareils.get(appareilNom);
                if (appareil != null) {
                    Session session = new Session(id, debut, fin, appareil, application);
                    historique.ajouter(session);
                }
            }
        }
        
        return historique;
    }
    
    public void deleteSession(UUID sessionId) throws SQLException {
        String sql = "DELETE FROM sessions WHERE id = ?";
        try (PreparedStatement stmt = connection. prepareStatement(sql)) {
            stmt.setString(1, sessionId.toString());
            stmt.executeUpdate();
        }
    }
    
    public void deleteAll() throws SQLException {
        String sql = "DELETE FROM sessions";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }
}