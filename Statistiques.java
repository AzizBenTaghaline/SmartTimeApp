package smarttimeapp;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public final class Statistiques {

    private Statistiques() {
        throw new UnsupportedOperationException("Classe utilitaire : ne pas instancier");
    }
  
    public static Duration dureeTotale(List<Session> sessions) {
        Objects.requireNonNull(sessions, "sessions ne doit pas être null");
        
        return sessions.stream()
            .map(Session::duree)
            .reduce(Duration.ZERO, Duration::plus);
    }

    public static long dureeTotaleMinutes(List<Session> sessions) {
        return dureeTotale(sessions).toMinutes();
    }
    
    public static double moyenneDureeMinutes(List<Session> sessions) {
        Objects.requireNonNull(sessions, "sessions ne doit pas être null");
        
        if (sessions.isEmpty()) {
            return 0.0;
        }
        
        return sessions.stream()
            .mapToLong(Session::dureeMinutes)
            .average()
            .orElse(0.0);
    }
 
    public static Map<Appareil, Duration> dureeParAppareil(List<Session> sessions) {
        Objects.requireNonNull(sessions, "sessions ne doit pas être null");
        
        return sessions.stream()
            .collect(Collectors.groupingBy(
                Session::appareil,
                Collectors.reducing(
                    Duration.ZERO,
                    Session::duree,
                    Duration::plus
                )
            ));
    }
    
     // Calcule le pourcentage de temps par appareil.

    public static Map<Appareil, Double> pourcentageParAppareil(List<Session> sessions) {
        Objects.requireNonNull(sessions, "sessions ne doit pas être null");
        
        long totalMinutes = dureeTotaleMinutes(sessions);
        
        if (totalMinutes == 0) {
            return Map.of();
        }
        
        Map<Appareil, Duration> dureeParAppareil = dureeParAppareil(sessions);
        
        return dureeParAppareil.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> 100.0 * entry.getValue().toMinutes() / totalMinutes
            ));
    }
     //* Calcule la durée totale par jour.

    public static Map<LocalDate, Duration> dureeParJour(List<Session> sessions) {
        Objects.requireNonNull(sessions, "sessions ne doit pas être null");
        
        return sessions.stream()
            .collect(Collectors.groupingBy(
                Session::jour,
                TreeMap::new, 
                Collectors.reducing(
                    Duration.ZERO,
                    Session::duree,
                    Duration::plus
                )
            ));
    }
    
    public static Optional<LocalDate> jourLePlusCharge(List<Session> sessions) {
        Objects.requireNonNull(sessions, "sessions ne doit pas être null");
        
        return dureeParJour(sessions)
            .entrySet()
            .stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey);
    }
 
}