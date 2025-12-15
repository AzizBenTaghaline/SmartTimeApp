
package smarttimeapp2.model.Exceptions;
public class SmartTimeException extends RuntimeException {
    
    private final String codeErreur;
    
    public SmartTimeException(String message) {
        super(message);
        this.codeErreur = "ERR_GENERAL";
    }
    
    public SmartTimeException(String message, String codeErreur) {
        super(message);
        this.codeErreur = codeErreur;
    }
 
    public SmartTimeException(String message, Throwable cause) {
        super(message, cause);
        this.codeErreur = "ERR_GENERAL";
    }
    
    public SmartTimeException(String message, String codeErreur, Throwable cause) {
        super(message, cause);
        this.codeErreur = codeErreur;
    }

    public String getCodeErreur() {
        return codeErreur;
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s", codeErreur, getMessage());
    }
}