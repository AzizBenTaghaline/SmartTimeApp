package smarttimeapp2.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene. layout.*;
import smarttimeapp2. model.*;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.util. ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the Objectifs (Goals) View
 * Displays goal cards with different strategies and descriptions
 */
public class ObjectifsView implements Initializable {

    @FXML
    private FlowPane cardsContainer;
    
    @FXML
    private Label labelObjectifsActifs;
    
    @FXML
    private Label labelObjectifsRespetes;
    
    @FXML
    private Label labelObjectifsDepasses;
    
    private Historique historique;
    private List<Objectif> objectifs;
    private LocalDate jourActuel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        jourActuel = LocalDate.now();
        objectifs = new ArrayList<>();
    }
    
    /**
     * Set the historique data source
     */
    public void setHistorique(Historique historique) {
        this.historique = historique;
    }
    
    /**
     * Load and display objectifs
     */
    public void loadObjectifs() {
        if (historique == null) {
            return;
        }
        
        // Create different goal strategies
        createObjectifs();
        
        // Display all goals as cards
        displayObjectifsCards();
        
        // Update statistics
        updateStatistics();
    }
    
    /**
     * Create different goal strategies
     */
    private void createObjectifs() {
        objectifs.clear();
        
        // Strategy 1: Global usage limit
        objectifs.add(Objectif.pourTous(Duration.ofHours(4)));
        
        // Strategy 2: Instagram limit
        objectifs.add(Objectif.pourApplication("Instagram", Duration.ofMinutes(30)));
        
        // Strategy 3: Social media combined limit
        objectifs.add(Objectif.personnalise(
            "Limiter réseaux sociaux à 1h",
            Duration.ofHours(1),
            s -> {
                String app = s.application().toLowerCase();
                return app. contains("instagram") || 
                       app.contains("facebook") || 
                       app. contains("whatsapp") ||
                       app.contains("twitter") ||
                       app.contains("tiktok");
            }
        ));
        
        // Strategy 4: Work/Productivity focus (allow more time)
        objectifs.add(Objectif.personnalise(
            "Encourager productivité (min 2h)",
            Duration.ofHours(2),
            s -> {
                String app = s. application().toLowerCase();
                return app.contains("vscode") || 
                       app.contains("intellij") ||
                       app. contains("eclipse") ||
                       app.contains("chrome") ||
                       app.contains("word");
            }
        ));
        
        // Strategy 5: Entertainment limit
        objectifs.add(Objectif.personnalise(
            "Limiter divertissement à 1h30",
            Duration.ofMinutes(90),
            s -> {
                String app = s.application().toLowerCase();
                return app.contains("youtube") || 
                       app.contains("netflix") ||
                       app.contains("spotify") ||
                       app.contains("games");
            }
        ));
        
        // Strategy 6: Evening screen time limit
        objectifs.add(Objectif.personnalise(
            "Réduire usage nocturne (après 22h)",
            Duration.ofMinutes(45),
            s -> s.debut(). toLocalTime().isAfter(java.time.LocalTime.of(22, 0))
        ));
    }
    
    /**
     * Display objectifs as cards
     */
    private void displayObjectifsCards() {
        cardsContainer.getChildren().clear();
        
        for (Objectif objectif : objectifs) {
            VBox card = createObjectifCard(objectif);
            cardsContainer.getChildren().add(card);
        }
    }
    
    /**
     * Create a single objectif card
     */
    private VBox createObjectifCard(Objectif objectif) {
        VBox card = new VBox(15);
        card.setPrefWidth(350);
        card.setMinHeight(200);
        
        // Calculate progress
        double progression = objectif.progression(historique, jourActuel);
        boolean depasse = objectif.estDepasse(historique, jourActuel);
        Duration tempsRestant = objectif.tempsRestant(historique, jourActuel);
        
        // Determine card color based on status
        String cardColor;
        String statusIcon;
        String statusText;
        
        if (depasse) {
            cardColor = "#ffebee"; // Light red
            statusIcon = "🚫";
            statusText = "DÉPASSÉ";
        } else if (progression >= 80) {
            cardColor = "#fff3e0"; // Light orange (warning)
            statusIcon = "⚠️";
            statusText = "ATTENTION";
        } else if (progression >= 50) {
            cardColor = "#e3f2fd"; // Light blue
            statusIcon = "📊";
            statusText = "EN COURS";
        } else {
            cardColor = "#e8f5e9"; // Light green
            statusIcon = "✅";
            statusText = "RESPECTÉ";
        }
        
        card.setStyle(
            "-fx-background-color: " + cardColor + ";" +
            "-fx-background-radius: 15;" +
            "-fx-padding: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 3);" +
            "-fx-border-color: " + (depasse ? "#e74c3c" : "#bdc3c7") + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 15;"
        );
        
        // Header with status
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label iconLabel = new Label(statusIcon);
        iconLabel.setStyle("-fx-font-size: 24;");
        
        Label statusLabel = new Label(statusText);
        statusLabel.setStyle(
            "-fx-font-size: 12;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + (depasse ? "#c0392b" : "#27ae60") + ";" +
            "-fx-background-color: white;" +
            "-fx-padding: 5 10;" +
            "-fx-background-radius: 10;"
        );
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        header.getChildren(). addAll(iconLabel, statusLabel);
        
        // Title (description)
        Label titleLabel = new Label(objectif.description());
        titleLabel.setWrapText(true);
        titleLabel.setStyle(
            "-fx-font-size: 16;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #2c3e50;"
        );
        
        // Strategy description (additional context)
        Label descriptionLabel = new Label(getStrategyDescription(objectif));
        descriptionLabel.setWrapText(true);
        descriptionLabel.setStyle(
            "-fx-font-size: 12;" +
            "-fx-text-fill: #7f8c8d;" +
            "-fx-font-style: italic;"
        );
        
        // Progress bar
        ProgressBar progressBar = new ProgressBar(Math.min(progression /100.0,1.0));
        progressBar.setPrefWidth(310);
        progressBar.setPrefHeight(20);
        progressBar.setStyle(
            "-fx-accent: " + (depasse ? "#e74c3c" : progression >= 80 ? "#f39c12" : "#27ae60") + ";"
        );
        
        // Progress percentage
        Label progressLabel = new Label(String.format("%.0f%%", progression));
        progressLabel.setStyle(
            "-fx-font-size: 18;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + (depasse ? "#c0392b" : "#2c3e50") + ";"
        );
        
        // Time remaining/exceeded
        Label timeLabel;
        if (depasse) {
            long depassement = Math.abs(tempsRestant.toMinutes());
            timeLabel = new Label("⏰ Dépassé de " + formatDuration(depassement));
            timeLabel.setStyle("-fx-text-fill: #c0392b; -fx-font-weight: bold;");
        } else {
            timeLabel = new Label("⏰ Temps restant: " + formatDuration(tempsRestant.toMinutes()));
            timeLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        }
        timeLabel.setStyle(timeLabel.getStyle() + " -fx-font-size: 13;");
        
        // Limit info
        Label limitLabel = new Label("🎯 Limite: " + formatDuration(objectif.limite(). toMinutes()));
        limitLabel.setStyle("-fx-text-fill: #34495e; -fx-font-size: 12;");
        
        // Add all elements to card
        card.getChildren().addAll(
            header,
            titleLabel,
            descriptionLabel,
            new Separator(),
            progressBar,
            progressLabel,
            timeLabel,
            limitLabel
        );
        
        return card;
    }
    
    /**
     * Get strategy description based on objectif
     */
    private String getStrategyDescription(Objectif objectif) {
        String desc = objectif.description(). toLowerCase();
        
        if (desc.contains("utilisation totale")) {
            return "Stratégie: Limite votre temps d'écran total sur tous les appareils et applications. ";
        } else if (desc.contains("instagram")) {
            return "Stratégie: Contrôle spécifique pour Instagram afin de réduire la dépendance aux réseaux sociaux.";
        } else if (desc.contains("réseaux sociaux")) {
            return "Stratégie: Limite combinée pour toutes les plateformes sociales (Instagram, Facebook, WhatsApp, Twitter, TikTok).";
        } else if (desc.contains("productivité")) {
            return "Stratégie: Encourage l'utilisation d'outils de travail et de productivité (minimum recommandé). ";
        } else if (desc.contains("divertissement")) {
            return "Stratégie: Balance le temps passé sur les plateformes de divertissement (YouTube, Netflix, Spotify).";
        } else if (desc. contains("nocturne")) {
            return "Stratégie: Réduit l'exposition aux écrans le soir pour améliorer la qualité du sommeil.";
        } else {
            return "Stratégie: Objectif personnalisé pour mieux gérer votre temps numérique.";
        }
    }
    
    /**
     * Format duration in a human-readable format
     */
    private String formatDuration(long totalMinutes) {
        if (totalMinutes < 0) {
            totalMinutes = Math.abs(totalMinutes);
        }
        
        if (totalMinutes < 60) {
            return totalMinutes + " min";
        } else {
            long hours = totalMinutes / 60;
            long minutes = totalMinutes % 60;
            if (minutes == 0) {
                return hours + "h";
            }
            return hours + "h " + minutes + "min";
        }
    }
    
    /**
     * Update statistics labels
     */
    private void updateStatistics() {
        Platform.runLater(() -> {
            int nbObjectifs = objectifs.size();
            int nbRespetes = 0;
            int nbDepasses = 0;
            
            for (Objectif objectif : objectifs) {
                if (objectif.estDepasse(historique, jourActuel)) {
                    nbDepasses++;
                } else {
                    nbRespetes++;
                }
            }
            
            labelObjectifsActifs.setText(String.valueOf(nbObjectifs));
            labelObjectifsRespetes.setText(String.valueOf(nbRespetes));
            labelObjectifsDepasses.setText(String.valueOf(nbDepasses));
        });
    }
    
   
    @FXML
    private void rafraichir() {
        jourActuel = LocalDate.now();
        loadObjectifs();
    }
}