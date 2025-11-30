package smarttimeapp2.model;

import java.time.LocalDateTime;

public final class Tablette extends Appareil {
    
    public Tablette(
        String nom,
        String modele,
        Systeme systeme,
        int niveauBatterie,
        LocalDateTime derniereUtilisation
    ) {
        super(nom, modele, systeme, niveauBatterie, derniereUtilisation);
    }
    
    @Override
    public Tablette withDerniereUtilisation(LocalDateTime nouvelle) {
        return new Tablette(nom(), modele(), systeme(), niveauBatterie(), nouvelle);
    }
    
    public static Tablette creer(String nom, String modele, Systeme systeme) {
        return new Tablette(nom, modele, systeme, 100, LocalDateTime.now());
    }
}