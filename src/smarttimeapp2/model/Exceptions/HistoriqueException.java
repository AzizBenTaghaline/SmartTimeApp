/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smarttimeapp2.model.Exceptions;
import smarttimeapp2.model.Session;
import smarttimeapp2.model.Appareil;
import java.time.LocalDateTime;

public class HistoriqueException extends SmartTimeException {
    
    public HistoriqueException(String message) {
        super(message, "ERR_HISTORIQUE");
    }
    
    public HistoriqueException(String message, String codeErreur) {
        super(message, codeErreur);
    }
    
    public HistoriqueException(String message, Throwable cause) {
        super(message, "ERR_HISTORIQUE", cause);
    }
    
    
    public static HistoriqueException chevauchementDetecte(
        Session session1, 
        Session session2, 
        Appareil appareil
    ) {
        return new HistoriqueException(
            String.format(
                "Chevauchement détecté pour l'appareil '%s' :\n" +
                "  Session 1 : %s → %s (%s)\n" +
                "  Session 2 : %s → %s (%s)",
                appareil.nom(),
                session1.debut(), session1.fin(), session1.application(),
                session2.debut(), session2.fin(), session2.application()
            ),
            "ERR_CHEVAUCHEMENT"
        );
    }

    public static HistoriqueException historiqueVide() {
        return new HistoriqueException(
            "L'historique est vide. Impossible d'effectuer cette opération.",
            "ERR_HISTORIQUE_VIDE"
        );
    }
    
    public static HistoriqueException sessionIntrouvable(String id) {
        return new HistoriqueException(
            String.format("Aucune session trouvée avec l'identifiant : %s", id),
            "ERR_SESSION_INTROUVABLE"
        );
    }
    
    public static HistoriqueException limiteAtteinte(int limite) {
        return new HistoriqueException(
            String.format("Limite de sessions atteinte : %d sessions maximum", limite),
            "ERR_LIMITE_ATTEINTE"
        );
    }
}