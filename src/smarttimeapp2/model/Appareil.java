
package smarttimeapp2.model;

import java.time.LocalDateTime;
import java.util.Objects;



public sealed abstract class Appareil permits Smartphone, Tablette, Ordinateur {
    
    private final String nom;
    private final String modele;
    private final Systeme systeme;
    private final int niveauBatterie;
    private final LocalDateTime derniereUtilisation;

    protected Appareil(
        String nom,
        String modele,
        Systeme systeme,
        int niveauBatterie,
        LocalDateTime derniereUtilisation
    ) {
        this.nom =nom;
        this.modele =modele;
        this.systeme =systeme;
        this.niveauBatterie = niveauBatterie;
        this.derniereUtilisation =derniereUtilisation;
    }
    
  /*  private static String validerNonVide(String valeur, String nomChamp) {
        Objects.requireNonNull(valeur, nomChamp + " ne doit pas être null");
        if (valeur.isBlank()) {
            throw new IllegalArgumentException(nomChamp + " ne doit pas être vide");
        }
        return valeur.trim();
    }
    
    private static int validerBatterie(int niveau) {
        if (niveau < 0 || niveau > 100) {
            throw new IllegalArgumentException(
                "Niveau de batterie doit être entre 0 et 100 (reçu : " + niveau + ")"
            );
        }
        return niveau;
    }
    */
    public String nom() { return nom; }
    public String modele() { return modele; }
    public Systeme systeme() { return systeme; }
    public int niveauBatterie() { return niveauBatterie; }
    public LocalDateTime derniereUtilisation() { return derniereUtilisation; }
    
    public String designationComplete() {
        return String.format("%s %s (%s)", nom, modele, systeme.nom());
    }
    
    public abstract Appareil withDerniereUtilisation(LocalDateTime nouvelle);
    
    @Override
    public String toString() {
        return String.format(
            "%s{nom='%s', modele='%s', os=%s, batterie=%d%%}",
            getClass().getSimpleName(), nom, modele, systeme, niveauBatterie
        );
    }
}