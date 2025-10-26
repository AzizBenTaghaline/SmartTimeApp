
package smarttimeapp;

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
        Objects.requireNonNull(id, "L'identifiant ne doit pas être null");
        Objects.requireNonNull(debut, "La date de début ne doit pas être null");
        Objects.requireNonNull(fin, "La date de fin ne doit pas être null");
        Objects.requireNonNull(appareil, "L'appareil ne doit pas être null");
        Objects.requireNonNull(application, "Le nom de l'application ne doit pas être null");
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
}