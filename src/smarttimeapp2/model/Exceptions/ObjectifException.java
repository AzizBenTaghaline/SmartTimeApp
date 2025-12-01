
package smarttimeapp2.model.Exceptions;

import smarttimeapp2.model.Objectif;
import java.time.Duration;

public class ObjectifException extends SmartTimeException {
    
    private final Objectif objectif;
    
    public ObjectifException(String message) {
        super(message, "ERR_OBJECTIF");
        this.objectif = null;
    }
    
    public ObjectifException(String message, Objectif objectif) {
        super(message, "ERR_OBJECTIF");
        this.objectif = objectif;
    }
    
    public ObjectifException(String message, String codeErreur, Objectif objectif) {
        super(message, codeErreur);
        this.objectif = objectif;
    }
    
    public Objectif getObjectif() {
        return objectif;
    }
    
    public static ObjectifException limiteInvalide(Duration limite) {
        return new ObjectifException(
            String.format("Limite invalide : %s. La limite doit être strictement positive.", limite),
            "ERR_LIMITE_INVALIDE",
            null
        );
    }
    
    public static ObjectifException descriptionVide() {
        return new ObjectifException(
            "La description de l'objectif ne peut pas être vide.",
            "ERR_DESCRIPTION_VIDE",
            null
        );
    }
    
    public static ObjectifException objectifDepasse(Objectif objectif, long depassementMinutes) {
        return new ObjectifException(
            String.format("Objectif dépassé : %s (dépassement de %d minutes)", 
                objectif.description(), depassementMinutes),
            "ERR_OBJECTIF_DEPASSE",
            objectif
        );
    }
    
    public static ObjectifException critereInvalide() {
        return new ObjectifException(
            "Le critère de filtrage ne peut pas être null.",
            "ERR_CRITERE_INVALIDE",
            null
        );
    }
}