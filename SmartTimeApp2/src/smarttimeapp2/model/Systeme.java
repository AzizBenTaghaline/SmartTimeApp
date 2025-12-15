
package smarttimeapp2.model;

public enum Systeme {
    ANDROID("Android"),
    IOS("iOS"),
    IPADOS("iPadOS"),
    WINDOWS("Windows"),
    MACOS("macOS"),
    LINUX("Linux"),
    AUTRE("Autre");
    
    private final String nom;
    
    Systeme(String nom) {
        this.nom = nom;
    }
    
    public String nom() {
        return nom;
    }
    
    @Override
    public String toString() {
        return nom;
    }
}