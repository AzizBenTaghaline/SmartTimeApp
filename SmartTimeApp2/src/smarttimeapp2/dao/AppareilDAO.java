package smarttimeapp2.dao;

import smarttimeapp2. model.*;
import java.sql.*;
import java.time.LocalDateTime;
import java. util.HashMap;
import java. util.Map;

public class AppareilDAO {
    
    private Connection connection;
    
    public AppareilDAO(Connection connection) {
        this.connection = connection;
    }
    
    public void save(Appareil appareil) throws SQLException {
        String sql = """
            INSERT INTO appareils (nom, modele, systeme, niveau_batterie, 
                                   derniere_utilisation, type, numero_serie, ram_go, type_ordinateur) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE 
                modele = VALUES(modele),
                systeme = VALUES(systeme),
                niveau_batterie = VALUES(niveau_batterie),
                derniere_utilisation = VALUES(derniere_utilisation),
                type = VALUES(type),
                numero_serie = VALUES(numero_serie),
                ram_go = VALUES(ram_go),
                type_ordinateur = VALUES(type_ordinateur)
        """;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, appareil.nom());
            stmt.setString(2, appareil.modele());
            stmt.setString(3, appareil.systeme(). name());
            stmt.setInt(4, appareil.niveauBatterie());
            stmt.setTimestamp(5, Timestamp.valueOf(appareil.derniereUtilisation()));
            
            if (appareil instanceof Smartphone s) {
                stmt.setString(6, "SMARTPHONE");
                stmt.setString(7, s.numeroSerie());
                stmt.setNull(8, Types.INTEGER);
                stmt.setNull(9, Types.VARCHAR);
            } else if (appareil instanceof Tablette t) {
                stmt. setString(6, "TABLETTE");
                stmt.setNull(7, Types.VARCHAR);
                stmt.setNull(8, Types.INTEGER);
                stmt.setNull(9, Types.VARCHAR);
            } else if (appareil instanceof Ordinateur o) {
                stmt.setString(6, "ORDINATEUR");
                stmt.setNull(7, Types.VARCHAR);
                stmt.setInt(8, o.ramGo());
                stmt.setString(9, o.type(). name());
            }
            
            stmt.executeUpdate();
        }
    }
    
    public Map<String, Appareil> loadAll() throws SQLException {
        Map<String, Appareil> appareils = new HashMap<>();
        String sql = "SELECT * FROM appareils";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String nom = rs.getString("nom");
                String modele = rs.getString("modele");
                Systeme systeme = Systeme.valueOf(rs.getString("systeme"));
                int niveauBatterie = rs.getInt("niveau_batterie");
                LocalDateTime derniereUtilisation = rs.getTimestamp("derniere_utilisation").toLocalDateTime();
                String type = rs. getString("type");
                
                Appareil appareil = null;
                
                switch (type) {
                    case "SMARTPHONE" -> {
                        String numeroSerie = rs.getString("numero_serie");
                        appareil = new Smartphone(nom, modele, systeme, niveauBatterie, 
                                                  derniereUtilisation, numeroSerie);
                    }
                    case "TABLETTE" -> {
                        appareil = new Tablette(nom, modele, systeme, niveauBatterie, 
                                                derniereUtilisation);
                    }
                    case "ORDINATEUR" -> {
                        int ramGo = rs. getInt("ram_go");
                        String typeOrdi = rs.getString("type_ordinateur");
                        Ordinateur.TypeOrdinateur typeOrdinateur = 
                            Ordinateur.TypeOrdinateur.valueOf(typeOrdi);
                        appareil = new Ordinateur(nom, modele, systeme, niveauBatterie, 
                                                  derniereUtilisation, ramGo, typeOrdinateur);
                    }
                }
                
                if (appareil != null) {
                    appareils.put(nom, appareil);
                }
            }
        }
        
        return appareils;
    }
    
    public void delete(String nom) throws SQLException {
        String sql = "DELETE FROM appareils WHERE nom = ?";
        try (PreparedStatement stmt = connection. prepareStatement(sql)) {
            stmt.setString(1, nom);
            stmt.executeUpdate();
        }
    }
}