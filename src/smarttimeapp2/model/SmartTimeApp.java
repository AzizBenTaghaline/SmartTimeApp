 package smarttimeapp2.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class SmartTimeApp {
    
    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMAT_DATETIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    private static final Historique historique = new Historique();
    private static final Map<String, Appareil> appareils = new HashMap<>();
    private static final Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        Adulte user = new Adulte("Hazem", 25, "hazem@gmail.com",1);
        user.ajouterAppareil("Samsung phone");
        user.ajouterAppareil("Laptop");
        user.ajouterAppareil("Ipad");

        List<Activite> activites = List.of(
            new Productivite("Travail", "Développement projet", 120, 20),
            new ReseauxSociaux("Instagram", "RéseauxSociaux", 45, 10, 300, 5),
            new Productivite("Lecture", "Lecture technique", 60,85.2 ),
            new ReseauxSociaux("TikTok", "Scrolling", 80, 50, 2000, 10)
        );

        ActiviteFilter filtreProductivite = a -> a instanceof Productivite;
        ActiviteFilter filtreLongues = a -> a.getDuree() > 60;
        ActiviteFilter filtreReseauxSociaux = a -> a instanceof ReseauxSociaux && a.getDuree() > 30;

        System.out.println("=== Activités productives ===");
        activites.stream()
        .filter(a -> filtreProductivite.filtrer(a))
        .forEach(a -> System.out.println(a));


        System.out.println("\n=== Activités longues (>60 min) ===");
        activites.stream()
                .filter(a -> filtreLongues.filtrer(a))
                .forEach(a->System.out.println(a));

        System.out.println("\n=== Réseaux sociaux actifs (>30 min) ===");
        activites.stream()
                .filter(a -> filtreReseauxSociaux.filtrer(a))
                .forEach(a->System.out.println(a));

        System.out.println("\n=== Activités de plus de 1h triées par durée ===");
        activites.stream()
                .filter(a -> a.getDuree() > 60)
                .sorted((a1, a2) -> Integer.compare(a2.getDuree(), a1.getDuree()))
                .forEach(a -> System.out.println(a.getNom() + " (" + a.getDuree() + " min)"));
        initialiserDonneesDemo();
        
        boolean continuer = true;
        
        while (continuer) {
            afficherMenu();
            int choix = lireChoix();
            
            switch (choix) {
                case 1 -> afficherAppareils();
                case 2 -> afficherSessions();
                case 3 -> ajouterSession();
                case 4 -> afficherStatistiques();
                case 5 -> afficherObjectifs();
                case 0 -> continuer = false;
                default -> System.out.println("Choix invalide. Veuillez reessayer.");
            }
            
            if (continuer) {
                System.out.println("\nAppuyez sur Entree pour continuer...");
                scanner.nextLine();
            }
        }
        
        afficherAuRevoir();
        scanner.close();
    }
    
    private static void afficherMenu() {
        System.out.println("\n┌──────────────────────────────────────────────────────────────┐");
        System.out.println("│                      MENU PRINCIPAL                          │");
        System.out.println("├──────────────────────────────────────────────────────────────┤");
        System.out.println("│  1️⃣  Afficher mes appareils                                  │");
        System.out.println("│  2️⃣  Afficher mes sessions                                   │");
        System.out.println("│  3️⃣  Ajouter une session                                     │");
        System.out.println("│  4️⃣  Afficher les statistiques                               │");
        System.out.println("│  5️⃣  Afficher les objectifs                                  │");
        System.out.println("│  0️⃣  Quitter                                                 │");
        System.out.println("└──────────────────────────────────────────────────────────────┘");
        System.out.print("\n Votre choix : ");
    }
    
    private static void afficherAuRevoir() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                                  ║");
        System.out.println("║                  Merci d'avoir utilise SmartTimeApp !            ║");
        System.out.println("║                                                                  ║");
        System.out.println("║              Prenez soin de votre temps numerique                ║");
        System.out.println("║                                                                  ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════╝\n");
    }
    
    private static void afficherSeparateur() {
        System.out.println("──────────────────────────────────────────────────────────────");
    }
    
private static void initialiserDonneesDemo() {
    Smartphone iphone = Smartphone.creer("iPhone", "15 Pro", Systeme.IOS);
    Tablette ipad = Tablette.creer("iPad", "Air", Systeme.IPADOS);
    Ordinateur macbook = Ordinateur.creer("MacBook", "Pro M3", Systeme.MACOS);
    
    appareils.put("iPhone", iphone);
    appareils.put("iPad", ipad);
    appareils.put("MacBook", macbook);
    
    System.out.println("  3 appareils crees");
    
    LocalDateTime maintenant = LocalDateTime.now();
    
    historique.ajouter(Session.creerAvecDuree(
        maintenant.minusHours(8),
        Duration.ofHours(2),
        iphone,
        "Instagram"
    ));
    
    historique.ajouter(Session.creerAvecDuree(
        maintenant.minusHours(5),
        Duration.ofMinutes(45),
        iphone,
        "WhatsApp"
    ));

    historique.ajouter(Session.creerAvecDuree(
        maintenant.minusHours(3), 
        Duration.ofHours(2),
        macbook,
        "VS Code"
    ));
    
    historique.ajouter(Session.creerAvecDuree(
        maintenant.minusMinutes(30), 
        Duration.ofMinutes(30),
        ipad,
        "Netflix"
    ));
    
    System.out.println(" 4 sessions enregistrees");
    System.out.println(" Donnees de demonstration chargees avec succes !\n");
}
    
    // ═══════════════════════════════════════════════════════════════════════════
    // FONCTIONNALITÉ 1 : AFFICHER LES APPAREILS
    // ═══════════════════════════════════════════════════════════════════════════
    
    private static void afficherAppareils() {
        System.out.println("\n MES APPAREILS");
        afficherSeparateur();
        
        if (appareils.isEmpty()) {
            System.out.println("Aucun appareil enregistre.");
            return;
        }
        
        appareils.values().forEach(appareil -> {
            System.out.println();
            System.out.println("  " +appareil.designationComplete());
            System.out.println("      └─ Batterie : " + " " + appareil.niveauBatterie() + "%");
            System.out.println("      └─ Derniere utilisation : " + 
                             appareil.derniereUtilisation().format(FORMAT_DATETIME));
            
            if (appareil instanceof Smartphone s) {
                System.out.println("      └─ N serie : " + s.numeroSerie());
            } else if (appareil instanceof Ordinateur o) {
                System.out.println("      └─ RAM : " + o.ramGo() + " Go");
                System.out.println("      └─ Type : " + o.type().nom());
            }
        });
        
        System.out.println();
        afficherSeparateur();
        System.out.println("Total : " + appareils.size() + " appareil(s)");
    }
    // ═══════════════════════════════════════════════════════════════════════════
    // FONCTIONNALITe 2 : AFFICHER LES SESSIONS
    // ═══════════════════════════════════════════════════════════════════════════
    
    private static void afficherSessions() {
        System.out.println("\n MES SESSIONS");
        afficherSeparateur();
        
        if (historique.estVide()) {
            System.out.println("Aucune session enregistree.");
            return;
        }
        
        var sessionsAujourdhui = historique.sessionsParJour(LocalDate.now());
        
        if (sessionsAujourdhui.isEmpty()) {
            System.out.println("Aucune session aujourd'hui.");
        } else {
            System.out.println("\n Sessions d'aujourd'hui (" + LocalDate.now().format(FORMAT_DATE) + ") :\n");
            
            sessionsAujourdhui.forEach(session -> {
                System.out.println("  • " + session.application() + 
                                 " sur " + session.appareil().nom());
                System.out.println("    ️  " + session.debut().toLocalTime() + 
                                 " → " + session.fin().toLocalTime() + 
                                 " (" + session.dureeMinutes() + " min)");
                System.out.println();
            });
        }
        
        afficherSeparateur();
        System.out.println("Total aujourd'hui : " + sessionsAujourdhui.size() + " session(s)");
        System.out.println("Total general : " + historique.nombreSessions() + " session(s)");
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // FONCTIONNALITÉ 3 : AJOUTER UNE SESSION
    // ═══════════════════════════════════════════════════════════════════════════
    
    private static void ajouterSession() {
        System.out.println("\n AJOUTER UNE SESSION");
        afficherSeparateur();
        
        if (appareils.isEmpty()) {
            System.out.println(" Aucun appareil disponible. Impossible d'ajouter une session.");
            return;
        }
        
        System.out.println("\nAppareils disponibles :");
        int index = 1;
        var appareilsList = appareils.values().stream().toList();
        for (Appareil appareil : appareilsList) {
            System.out.println("  " + index++ + ". " + appareil.designationComplete());
        }
        
        System.out.print("\nChoisissez un appareil (1-" + appareilsList.size() + ") : ");
        int choixAppareil = lireChoix() - 1;
        
        if (choixAppareil < 0 || choixAppareil >= appareilsList.size()) {
            System.out.println(" Choix invalide.");
            return;
        }
        
        Appareil appareil = appareilsList.get(choixAppareil);
        
        System.out.print("Nom de l'application : ");
        String application = scanner.nextLine();
        
        if (application.isBlank()) {
            System.out.println(" Le nom de l'application ne peut pas etre vide.");
            return;
        }
        
        System.out.print("Duree en minutes : ");
        int minutes = lireChoix();
        
        if (minutes <= 0) {
            System.out.println(" La duree doit etre positive.");
            return;
        }
        
        try {
            Session session = Session.creerAvecDuree(
                LocalDateTime.now().minusMinutes(minutes),
                Duration.ofMinutes(minutes),
                appareil,
                application
            );
            
            historique.ajouter(session);
            
            System.out.println("\n Session ajoutee avec succes !");
            System.out.println("   " + application + " sur " + appareil.nom() + 
                             " - " + minutes + " min");
            
        } catch (Exception e) {
            System.out.println("\n Erreur lors de l'ajout : " + e.getMessage());
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // FONCTIONNALITÉ 4 : AFFICHER LES STATISTIQUES
    // ═══════════════════════════════════════════════════════════════════════════
    
    private static void afficherStatistiques() {
        System.out.println("\n STATISTIQUES");
        afficherSeparateur();
        
        if (historique.estVide()) {
            System.out.println("Aucune session enregistree. Impossible de calculer les statistiques.");
            return;
        }
        
        var sessions = historique.sessions();
        var sessionsAujourdhui = historique.sessionsParJour(LocalDate.now());
        
        System.out.println("\n  Aujourd'hui (" + LocalDate.now().format(FORMAT_DATE) + ") :");
        System.out.println();
        
        if (sessionsAujourdhui.isEmpty()) {
            System.out.println("  Aucune session aujourd'hui.");
        } else {
            long totalMin = Statistiques.dureeTotaleMinutes(sessionsAujourdhui);
            double moyenneMin = Statistiques.moyenneDureeMinutes(sessionsAujourdhui);
            
            System.out.println("  ️  Temps total : " + formatterDuree(totalMin));
            System.out.println("   Moyenne par session : " + String.format("%.0f min", moyenneMin));
            System.out.println("   Nombre de sessions : " + sessionsAujourdhui.size());
            
            System.out.println("\n    Repartition par appareil :");
            var pourcentages = Statistiques.pourcentageParAppareil(sessionsAujourdhui);
            pourcentages.forEach((appareil, pct) -> 
                System.out.println("      • " + appareil.nom() + " : " + 
                                 String.format("%.1f%%", pct))
            );
        }
        
        System.out.println("\n  Global (toutes les sessions) :");
        System.out.println();
        
        long totalMinGlobal = Statistiques.dureeTotaleMinutes(sessions);
        System.out.println("  ️  Temps total : " + formatterDuree(totalMinGlobal));
        System.out.println("    Nombre total de sessions : " + sessions.size());
        
        var jourMax = Statistiques.jourLePlusCharge(sessions);
        jourMax.ifPresent(jour -> 
            System.out.println("\n    Jour le plus charge : " + jour.format(FORMAT_DATE))
        );
    }
    
    private static String formatterDuree(long minutes) {
        long heures = minutes / 60;
        long min = minutes % 60;
        
        if (heures > 0) {
            return heures + "h" + (min > 0 ? String.format("%02d", min) : "");
        } else {
            return min + " min";
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // FONCTIONNALITÉ 5 : AFFICHER LES OBJECTIFS
    // ═══════════════════════════════════════════════════════════════════════════
    
    private static void afficherObjectifs() {
        System.out.println("\n  MES OBJECTIFS");
        afficherSeparateur();
        
        if (historique.estVide()) {
            System.out.println("Aucune session enregistree. Impossible d'evaluer les objectifs.");
            return;
        }
        
        LocalDate aujourdhui = LocalDate.now();
        
        Objectif objectifGlobal = Objectif.pourTous(Duration.ofHours(4));
        evaluerObjectif(objectifGlobal, aujourdhui);
        
        Objectif objectifInstagram = Objectif.pourApplication("Instagram", Duration.ofMinutes(30));
        evaluerObjectif(objectifInstagram, aujourdhui);
        
        Objectif objectifReseauxSociaux = Objectif.personnalise(
            "Limiter reseaux sociaux à 1h",
            Duration.ofHours(1),
            s -> {
                String app = s.application().toLowerCase();
                return app.contains("instagram") || 
                       app.contains("facebook") || 
                       app.contains("whatsapp") ||
                       app.contains("twitter");
            }
        );
        evaluerObjectif(objectifReseauxSociaux, aujourdhui);
    }
    
    private static void evaluerObjectif(Objectif objectif, LocalDate jour) {
        System.out.println("\n " + objectif.description());
        
        boolean depasse = objectif.estDepasse(historique, jour);
        double progression = objectif.progression(historique, jour);
        Duration restant = objectif.tempsRestant(historique, jour);
        
        String statut = depasse ? " DEPASSE" : " RESPECTE";
        System.out.println("   Statut : " + statut);
        System.out.println("   Progression : " + getBarreProgression(progression) + 
                         " " + String.format("%.0f%%", progression));
        
        if (depasse) {
            System.out.println("   Depassement : " + Math.abs(restant.toMinutes()) + " min");
        } else {
            System.out.println("   Temps restant : " + restant.toMinutes() + " min");
        }
    }
    
    private static String getBarreProgression(double pct) {
        int barres = (int) (pct / 10);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < 10; i++) {
            if (i < barres) {
                sb.append(barres > 10 ? "🔴" : "🟢");
            } else {
                sb.append("⚪");
            }
        }
        sb.append("]");
        return sb.toString();
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // UTILITAIRES
    // ═══════════════════════════════════════════════════════════════════════════

    private static int lireChoix() {
        try {
            String input = scanner.nextLine();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}