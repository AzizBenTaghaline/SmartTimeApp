package smarttimeapp2. model;

import java.time.LocalDate;
import java.time. LocalDateTime;
import java.util.*;
import java.util.function.Function;

public final class Historique {
    
    private final List<Session> sessions;
    
    private java.util.function.Consumer<Session> onSessionAdded;
private java.util. function.Consumer<Session> onSessionRemoved;

    public Historique() {
        this.sessions = new ArrayList<>();
    }
    
    public void ajouter(Session session) {    
    boolean chevauchement = sessions. stream()
        .anyMatch(s -> chevauche(s, session));
    
    if (chevauchement) {
        throw new IllegalStateException(
            "Chevauchement détecté pour l'appareil " + session.appareil().nom() +
            " entre " + session.debut() + " et " + session.fin()
        );
    }
    
    sessions.add(session);
    
    if (onSessionAdded != null) {
        onSessionAdded.accept(session);
    }
}
    
   public boolean supprimer(Session session) {
    boolean removed = sessions.remove(session);
    
    if (removed && onSessionRemoved != null) {
        onSessionRemoved.accept(session);
    }
    
    return removed;
}
    
    public void modifier(Session ancienne, Session nouvelle) {
        if (! sessions.contains(ancienne)) {
            throw new IllegalArgumentException("La session à modifier n'existe pas dans l'historique");
        }
        
        boolean chevauchement = sessions.stream()
            .filter(s -> !s.equals(ancienne))
            .anyMatch(s -> chevauche(s, nouvelle));
        
        if (chevauchement) {
            throw new IllegalStateException(
                "Chevauchement détecté pour l'appareil " + nouvelle.appareil().nom() +
                " entre " + nouvelle.debut() + " et " + nouvelle.fin()
            );
        }
        
        sessions. remove(ancienne);
        sessions.add(nouvelle);
    }
    
    public void setOnSessionAdded(java.util.function.Consumer<Session> callback) {
    this.onSessionAdded = callback;
}

public void setOnSessionRemoved(java.util.function.Consumer<Session> callback) {
    this.onSessionRemoved = callback;
}
 
    private boolean chevauche(Session s1, Session s2) {
        if (! s1.appareil().equals(s2.appareil())) {
            return false;
        }
        return !(s1.fin().isBefore(s2. debut()) || s1.debut().isAfter(s2.fin()));
    }
    
    public List<Session> sessions() {
        return Collections.unmodifiableList(sessions);
    }
    
    public List<Session> sessionsParJour(LocalDate jour) {
        Objects.requireNonNull(jour, "jour ne doit pas être null");
        
        return sessions.stream()
            .filter(s -> s.appartientAuJour(jour))
            .toList();
    }

    public int nombreSessions() {
        return sessions.size();
    }

    public boolean estVide() {
        return sessions.isEmpty();
    }

    public void effacer() {
        sessions. clear();
    }
}