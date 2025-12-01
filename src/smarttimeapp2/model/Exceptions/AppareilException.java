
package smarttimeapp2.model.Exceptions;
import smarttimeapp2.model.Appareil;

public class AppareilException extends SmartTimeException {
    
    private final Appareil appareil;

    public AppareilException(String message) {
        super(message, "ERR_APPAREIL");
        this.appareil = null;
    }
    
    public AppareilException(String message, Appareil appareil) {
        super(message, "ERR_APPAREIL");
        this.appareil = appareil;
    }
   
    public AppareilException(String message, String codeErreur, Appareil appareil) {
        super(message, codeErreur);
        this.appareil = appareil;
    }
    
    public Appareil getAppareil() {
        return appareil;
    }
    

    
    public static AppareilException batterieInvalide(int niveau) {
        return new AppareilException(
            String.format("Niveau de batterie invalide : %d%%. Doit être entre 0 et 100.", niveau),
            "ERR_BATTERIE_INVALIDE",
            null
        );
    }
    
    public static AppareilException batterieCritique(Appareil appareil) {
        return new AppareilException(
            String.format("Batterie critique pour %s : %d%%", 
                appareil.nom(), appareil.niveauBatterie()),
            "ERR_BATTERIE_CRITIQUE",
            appareil
        );
    }
    
    public static AppareilException nomInvalide(String nom) {
        return new AppareilException(
            String.format("Nom d'appareil invalide : '%s'. Le nom ne doit pas être vide.", nom),
            "ERR_NOM_INVALIDE",
            null
        );
    }
    
    public static AppareilException appareilIntrouvable(String nom) {
        return new AppareilException(
            String.format("Aucun appareil trouvé avec le nom : '%s'", nom),
            "ERR_APPAREIL_INTROUVABLE",
            null
        );
    }
}