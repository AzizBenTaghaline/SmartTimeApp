
package smarttimeapp2.model;
import java.time.LocalDateTime;

public final class Ordinateur extends Appareil {
    
    public enum TypeOrdinateur {
        PORTABLE("Portable"),
        BUREAU("Bureau");
        
        private final String nom;
        
        TypeOrdinateur(String nom) { this.nom = nom; }
        public String nom() { return nom; }
    }
    
    private final int ramGo;
    private final TypeOrdinateur type;
    
    public Ordinateur(
        String nom,
        String modele,
        Systeme systeme,
        int niveauBatterie,
        LocalDateTime derniereUtilisation,
        int ramGo,
        TypeOrdinateur type
    ) {
        super(nom, modele, systeme, niveauBatterie, derniereUtilisation);
        this.ramGo = Math.max(0, ramGo);
        this.type = type != null ? type : TypeOrdinateur.PORTABLE;
    }
    
    public Ordinateur(
        String nom,
        String modele,
        Systeme systeme,
        int niveauBatterie,
        LocalDateTime derniereUtilisation
    ) {
        this(nom, modele, systeme, niveauBatterie, derniereUtilisation, 8, TypeOrdinateur.PORTABLE);
    }
    
    public int ramGo() { return ramGo; }
    public TypeOrdinateur type() { return type; }
    
    @Override
    public Ordinateur withDerniereUtilisation(LocalDateTime nouvelle) {
        return new Ordinateur(
            nom(), modele(), systeme(), niveauBatterie(), nouvelle,
            ramGo, type
        );
    }
    
    public static Ordinateur creer(String nom, String modele, Systeme systeme) {
        return new Ordinateur(nom, modele, systeme, 100, LocalDateTime.now());
    }
}
