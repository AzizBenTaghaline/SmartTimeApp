package smarttimeapp2.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public record Session(
    UUID id,
    LocalDateTime debut,
    LocalDateTime fin,
    Appareil appareil,
    String application
) {
    public Session {
        if (!fin.isAfter(debut)) {
            throw new IllegalArgumentException(
                "La fin (" + fin + ") doit être après le début (" + debut + ")"
            );
        }
        if (application.isBlank()) {
            throw new IllegalArgumentException("Le nom de l'application ne doit pas être vide");
        }
    }
    public Duration duree() {
        return Duration.between(debut, fin);
    }
    
    public long dureeMinutes() {
        return duree().toMinutes();
    }
  
    public double dureeHeures() {
        return duree().toMinutes() / 60.0;
    }

    public LocalDate jour() {
        return debut.toLocalDate();
    }
    
    public boolean appartientAuJour(LocalDate jour) {
        Objects.requireNonNull(jour, "jour ne doit pas être null");
        return debut.toLocalDate().equals(jour);
    }
    
    public boolean appartientAPeriode(LocalDateTime debutPeriode, LocalDateTime finPeriode) {
        Objects.requireNonNull(debutPeriode, "debutPeriode ne doit pas être null");
        Objects.requireNonNull(finPeriode, "finPeriode ne doit pas être null");
        return !(fin.isBefore(debutPeriode) || debut.isAfter(finPeriode));
    }
  
    public static Session creer(
        LocalDateTime debut,
        LocalDateTime fin,
        Appareil appareil,
        String application
    ) {
        return new Session(UUID.randomUUID(), debut, fin, appareil, application);
    }
    
    public static Session creerAvecDuree(
        LocalDateTime debut,
        Duration duree,
        Appareil appareil,
        String application
    ) {
        Objects.requireNonNull(debut, "debut ne doit pas être null");
        Objects.requireNonNull(duree, "duree ne doit pas être null");
        
        return new Session(
            UUID.randomUUID(),
            debut,
            debut.plus(duree), 
            appareil,
            application
        );
    }
}