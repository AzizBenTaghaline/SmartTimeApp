package smarttimeapp; 

import java.time.Duration;
import java.time.LocalDate;
import java.util.Objects;
import java.util.function.Predicate;

public record Objectif(
    String description,
    Duration limite,
    Predicate<Session> critere
) {
    
    public Objectif {
 
        Objects.requireNonNull(description, "La description ne doit pas être null");
        Objects.requireNonNull(limite, "La limite ne doit pas être null");
        Objects.requireNonNull(critere, "Le critère ne doit pas être null");

        if (description.isBlank()) {
            throw new IllegalArgumentException("La description ne doit pas être vide");
        }
       
        if (limite.isNegative() || limite.isZero()) {
            throw new IllegalArgumentException(
                "La limite doit être strictement positive (reçu : " + limite + ")"
            );
        }
    }
    
    public boolean estDepasse(Historique historique, LocalDate jour) {
        Objects.requireNonNull(historique, "L'historique ne doit pas être null");
        Objects.requireNonNull(jour, "Le jour ne doit pas être null");
                Duration total = historique.sessionsParJour(jour).stream()
            .filter(critere)                    // Filtre selon le critère
            .map(Session::duree)                // Extrait les durées
            .reduce(Duration.ZERO, Duration::plus);  // Somme
        return total.compareTo(limite) > 0;
    }

    public double progression(Historique historique, LocalDate jour) {
        Objects.requireNonNull(historique, "L'historique ne doit pas être null");
        Objects.requireNonNull(jour, "Le jour ne doit pas être null");
        
        Duration total = historique.sessionsParJour(jour).stream()
            .filter(critere)
            .map(Session::duree)
            .reduce(Duration.ZERO, Duration::plus);
        
        long totalMinutes = total.toMinutes();
        long limiteMinutes = limite.toMinutes();
        
        if (limiteMinutes == 0) {
            return 0.0;
        }
        
        return (100.0 * totalMinutes) / limiteMinutes;
    }
   
    public Duration tempsRestant(Historique historique, LocalDate jour) {
        Objects.requireNonNull(historique, "L'historique ne doit pas être null");
        Objects.requireNonNull(jour, "Le jour ne doit pas être null");
        
        Duration total = historique.sessionsParJour(jour).stream()
            .filter(critere)
            .map(Session::duree)
            .reduce(Duration.ZERO, Duration::plus);
        
        return limite.minus(total);
    }
    
    public static Objectif pourTous(Duration limite) {
        return new Objectif(
            "Limiter utilisation totale à " + formatterDuree(limite),
            limite,
            s -> true  // Accepte toutes les sessions
        );
    }
    
    public static Objectif pourApplication(String nomApplication, Duration limite) {
        Objects.requireNonNull(nomApplication, "Le nom de l'application ne doit pas être null");
        
        if (nomApplication.isBlank()) {
            throw new IllegalArgumentException("Le nom de l'application ne doit pas être vide");
        }
        
        return new Objectif(
            "Limiter " + nomApplication + " à " + formatterDuree(limite),
            limite,
            s -> s.application().equalsIgnoreCase(nomApplication)
        );
    }
   
    private static String formatterDuree(Duration duree) {
        long heures = duree.toHours();
        
        if (heures > 0) {
            long minutes = duree.toMinutesPart();
            return minutes > 0 
                ? heures + "h" + minutes + "min"
                : heures + "h";
        } else {
            return duree.toMinutes() + "min";
        }
    }
}