
package smarttimeapp2.model;
import java.time.LocalDateTime;


public final class Smartphone extends Appareil {
    
    private final String numeroSerie;
    
    public Smartphone(
        String nom,
        String modele,
        Systeme systeme,
        int niveauBatterie,
        LocalDateTime derniereUtilisation,
        String numeroSerie
    ) {
        super(nom, modele, systeme, niveauBatterie, derniereUtilisation);
        this.numeroSerie = numeroSerie != null ? numeroSerie : "N/A";
    }
    
    public Smartphone(
        String nom,
        String modele,
        Systeme systeme,
        int niveauBatterie,
        LocalDateTime derniereUtilisation
    ) {
        this(nom, modele, systeme, niveauBatterie, derniereUtilisation, "N/A");
    }
    
    public String numeroSerie() { return numeroSerie; }
    
    @Override
    public Smartphone withDerniereUtilisation(LocalDateTime nouvelle) {
        return new Smartphone(
            nom(), modele(), systeme(), niveauBatterie(), nouvelle,
            numeroSerie
        );
    }
    
    public static Smartphone creer(String nom, String modele, Systeme systeme) {
        return new Smartphone(nom, modele, systeme, 100, LocalDateTime.now());
    }
}