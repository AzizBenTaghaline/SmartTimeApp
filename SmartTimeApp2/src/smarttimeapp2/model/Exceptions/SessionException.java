
package smarttimeapp2.model.Exceptions;
import smarttimeapp2.model.Session;
import java.time.LocalDateTime;


public class SessionException extends SmartTimeException {
    
    private final Session session;
    public SessionException(String message) {
        super(message, "ERR_SESSION");
        this.session = null;
    }
  
    public SessionException(String message, Session session) {
        super(message, "ERR_SESSION");
        this.session = session;
    }
    
    public SessionException(String message, String codeErreur, Session session) {
        super(message, codeErreur);
        this.session = session;
    }
    
    public Session getSession() {
        return session;
    }
    
    public static SessionException datesIncoherentes(LocalDateTime debut, LocalDateTime fin) {
        return new SessionException(
            String.format("Dates incohérentes : la fin (%s) doit être après le début (%s)", 
                fin, debut),
            "ERR_DATES_INCOHERENTES",
            null
        );
    }
    
    public static SessionException dureeInvalide(long dureeMinutes) {
        return new SessionException(
            String.format("Durée invalide : %d minutes. La durée doit être positive.", dureeMinutes),
            "ERR_DUREE_INVALIDE",
            null
        );
    }
    
    public static SessionException applicationInvalide(String application) {
        return new SessionException(
            String.format("Nom d'application invalide : '%s'. Le nom ne doit pas être vide.", application),
            "ERR_APPLICATION_INVALIDE",
            null
        );
    }
    
    public static SessionException sessionTropLongue(Session session, long maxMinutes) {
        return new SessionException(
            String.format("Session trop longue : %d minutes (max : %d minutes)", 
                session.dureeMinutes(), maxMinutes),
            "ERR_SESSION_TROP_LONGUE",
            session
        );
    }
    
    public static SessionException sessionFuture(LocalDateTime debut) {
        return new SessionException(
            String.format("La session ne peut pas commencer dans le futur : %s", debut),
            "ERR_SESSION_FUTURE",
            null
        );
    }
}