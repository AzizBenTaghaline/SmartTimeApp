package smarttimeapp2.model; 

import java.time.Duration;
import java.time.LocalDate;
import java.util.function.Predicate;

public record Objectif(
    String description,
    Duration limite,
    Predicate<Session> critere
) {
    public Objectif {
        if (limite.isNegative() || limite.isZero()) {
            throw new IllegalArgumentException(
                "La limite doit être strictement positive (reçu : " + limite + ")"
            );
        }
    }
    
    public boolean estDepasse(Historique historique, LocalDate jour) {
        Duration total = historique.sessionsParJour(jour).stream()
            .filter(critere)
            .map(Session::duree)
            .reduce(Duration.ZERO, Duration::plus);
        
        return total.compareTo(limite) > 0;
    }

    public double progression(Historique historique, LocalDate jour) {
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
        Duration total = historique.sessionsParJour(jour).stream()
            .filter(critere)
            .map(Session::duree)
            .reduce(Duration.ZERO, Duration::plus);
        
        return limite.minus(total);
    }
   
    public static Objectif pourAppareil(Appareil appareil, Duration limite) {
        return new Objectif(
            "Limiter " + appareil.nom() + " à " + formatterDuree(limite),
            limite,
            s -> s.appareil().equals(appareil)
        );
    }
    
    public static Objectif pourTous(Duration limite) {
        return new Objectif(
            "Limiter utilisation totale à " + formatterDuree(limite),
            limite,
            s -> true
        );
    }
    
    public static Objectif pourApplication(String nomApplication, Duration limite) {
        if (nomApplication.isBlank()) {
            throw new IllegalArgumentException("Le nom de l'application ne doit pas être vide");
        }
        return new Objectif(
            "Limiter " + nomApplication + " à " + formatterDuree(limite),
            limite,
            s -> s.application().equalsIgnoreCase(nomApplication)
        );
    }
    public static Objectif personnalise(
        String description,
        Duration limite,
        Predicate<Session> critere
    ) {
        return new Objectif(description, limite, critere);
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