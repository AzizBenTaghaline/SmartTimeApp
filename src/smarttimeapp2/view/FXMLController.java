/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */

package smarttimeapp2.view;

import smarttimeapp2.model.*;
import java.time.Duration;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXMLLoader;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import smarttimeapp2.model.Appareil;
import smarttimeapp2.model.Ordinateur;
import smarttimeapp2.model.Smartphone;
import smarttimeapp2.model.Systeme;
import smarttimeapp2.model.Tablette;

// Add these as class fields near the top with other fields


public class FXMLController implements Initializable {
    
    // Header elements
    @FXML
    private Label labelDateHeure;
    
    // Navigation buttons
    @FXML
    private Button btnDashboard;
    @FXML
    private Button btnAppareils;
    @FXML
    private Button btnSessions;
    @FXML
    private Button btnStatistiques;
    @FXML
    private Button btnObjectifs;
    @FXML
    private Button btnAjouterSession;
    
    // Content pane
    @FXML
    private StackPane contentPane;
    
    // Dashboard view
    @FXML
    private VBox dashboardView;
    
    // Dashboard cards
    @FXML
    private Label labelCardNbSessions;
    @FXML
    private Label labelCardTempsTotal;
    @FXML
    private Label labelCardMoyenne;
    
    // Chart
    @FXML
    private PieChart pieChartAppareils;
    
    // Objectifs container
    @FXML
    private VBox vboxObjectifs;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize date/time display
        updateDateTime();
        startDateTimeUpdater();
        
        // Initialize dashboard with sample data
        initializeDashboard();
        
        // Set dashboard as active view
        setActiveButton(btnDashboard);
    }
    
    private static Historique sharedHistorique = null;
    
    /**
     * Update the date/time label
     */
    private void updateDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("📅 dd/MM/yyyy HH:mm");
        labelDateHeure.setText(formatter.format(now));
    }
    
    /**
     * Start a background thread to update date/time every second
     */
    private void startDateTimeUpdater() {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    Platform.runLater(this::updateDateTime);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
    
    /**
     * Initialize dashboard with sample data
     */
    private void initializeDashboard() {
        // Set card values
        labelCardNbSessions.setText("4");
        labelCardTempsTotal.setText("6h15");
        labelCardMoyenne.setText("93");
        
        // Initialize pie chart
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("Ordinateur", 45),
            new PieChart.Data("Smartphone", 30),
            new PieChart.Data("Tablette", 25)
        );
        pieChartAppareils.setData(pieChartData);
        pieChartAppareils.setLegendVisible(true);
        
        // Initialize objectives
        initializeObjectives();
    }
    
    /**
     * Initialize objectives section
     */
    private void initializeObjectives() {
        vboxObjectifs.getChildren().clear();
        
        // Sample objectives
        addObjectiveItem("Limiter le temps d'écran à 6h", 75);
        addObjectiveItem("Faire 5 pauses actives", 60);
        addObjectiveItem("Temps de sommeil: 8h minimum", 100);
    }
    
    /**
     * Add an objective item with progress
     */
    private void addObjectiveItem(String text, int progress) {
        VBox objectiveBox = new VBox(5);
        objectiveBox.setStyle("-fx-padding: 10; -fx-background-color: #f5f5f5; -fx-background-radius: 5;");
        
        Label objectiveLabel = new Label(text);
        objectiveLabel.setStyle("-fx-font-size: 14;");
        
        Label progressLabel = new Label(progress + "% complété");
        progressLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #666;");
        
        objectiveBox.getChildren().addAll(objectiveLabel, progressLabel);
        vboxObjectifs.getChildren().add(objectiveBox);
    }
    
    /**
     * Set active navigation button
     */
    private void setActiveButton(Button activeButton) {
        // Remove active class from all buttons
        btnDashboard.getStyleClass().remove("nav-button-active");
        btnAppareils.getStyleClass().remove("nav-button-active");
        btnSessions.getStyleClass().remove("nav-button-active");
        btnStatistiques.getStyleClass().remove("nav-button-active");
        btnObjectifs.getStyleClass().remove("nav-button-active");
        
        // Add active class to selected button
        if (!activeButton.getStyleClass().contains("nav-button-active")) {
            activeButton.getStyleClass().add("nav-button-active");
        }
    }
    
    // Navigation actions
    @FXML
    private void afficherDashboard() {
        setActiveButton(btnDashboard);
        contentPane.getChildren().clear();
        contentPane.getChildren().add(dashboardView);
    }
    
@FXML
private void afficherAppareils() {
    setActiveButton(btnAppareils);
    
    // Get appareils from your data source
    Map<String, Appareil> appareils = getAppareils(); // You'll need to implement this
    
    AppareilsView appareilsView = new AppareilsView(appareils);
    contentPane.getChildren().clear();
    contentPane.getChildren().add(appareilsView);
}

// You'll need to add a method to access your appareils data
private Map<String, Appareil> getAppareils() {
    // This should retrieve appareils from your SmartTimeApp data
    // You might need to refactor SmartTimeApp to make appareils accessible
    Map<String, Appareil> appareils = new HashMap<>();
    
    // Sample data (replace with actual data loading)
    appareils.put("iphone", Smartphone.creer("iPhone", "14 Pro", Systeme.IOS));
    appareils. put("laptop", Ordinateur.creer("MacBook", "Pro M2", Systeme.IOS));
    appareils. put("tablet", Tablette. creer("iPad", "Air", Systeme.IOS));
    
    return appareils;
}
    
     @FXML
    private void afficherSessions() {
    setActiveButton(btnSessions);
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SessionsView.fxml"));
        VBox sessionsView = loader.load();
        
        SessionsView controller = loader.getController();
        controller.setHistorique(getHistorique());
        controller. loadSessions();
        
        contentPane.getChildren().clear();
        contentPane.getChildren().add(sessionsView);
    } catch (IOException e) {
        e.printStackTrace();
        showPlaceholderView("⏱ Mes sessions", "Erreur lors du chargement de la vue des sessions.");
    }
}
    
    @FXML
    private void afficherStatistiques() {
        setActiveButton(btnStatistiques);
        // TODO: Implement statistiques view
        showPlaceholderView("📈 Statistiques", "Les statistiques détaillées seront disponibles prochainement.");
    }
    
    @FXML
    private void afficherObjectifs() {
        setActiveButton(btnObjectifs);
        // TODO: Implement objectifs view
        showPlaceholderView("🎯 Objectifs", "La gestion des objectifs sera disponible prochainement.");
    }
    
    @FXML
    private void ajouterSession() {
        // TODO: Implement add session dialog
        System.out.println("Ajouter une nouvelle session");
    }
    
    @FXML
    private void quitter() {
        Platform.exit();
    }
    
    /**
     * Show a placeholder view for unimplemented features
     */
    private void showPlaceholderView(String title, String message) {
        VBox placeholderView = new VBox(20);
        placeholderView.setStyle("-fx-alignment: center; -fx-padding: 50;");
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");
        
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #666;");
        
        placeholderView.getChildren().addAll(titleLabel, messageLabel);
        
        contentPane.getChildren().clear();
        contentPane.getChildren().add(placeholderView);
    }
    /**
 * Get or create shared Historique instance with demo data
 */
private Historique getHistorique() {
    if (sharedHistorique == null) {
        sharedHistorique = new Historique();
        initializeDemoSessions();
    }
    return sharedHistorique;
}

/**
 * Initialize demo session data
 */
private void initializeDemoSessions() {
    try {
        // Create demo devices
        Smartphone iphone = Smartphone.creer("iPhone", "14 Pro", Systeme.IOS);
        Tablette ipad = Tablette.creer("iPad", "Air", Systeme. IPADOS);
        Ordinateur macbook = Ordinateur.creer("MacBook", "Pro M2", Systeme.MACOS);
        
        LocalDateTime now = LocalDateTime.now();
        
        // Today's sessions
        sharedHistorique.ajouter(Session.creerAvecDuree(
            now.minusHours(2), Duration.ofMinutes(45), iphone, "Instagram"
        ));
        sharedHistorique.ajouter(Session.creerAvecDuree(
            now.minusHours(4), Duration.ofMinutes(120), macbook, "VSCode"
        ));
        sharedHistorique.ajouter(Session.creerAvecDuree(
            now. minusHours(5), Duration.ofMinutes(30), ipad, "YouTube"
        ));
        
        // Yesterday's sessions
        LocalDateTime yesterday = now.minusDays(1);
        sharedHistorique.ajouter(Session.creerAvecDuree(
            yesterday. minusHours(3), Duration.ofMinutes(90), iphone, "TikTok"
        ));
        sharedHistorique.ajouter(Session.creerAvecDuree(
            yesterday. minusHours(6), Duration.ofMinutes(60), macbook, "Chrome"
        ));
        
        // Last week's sessions
        LocalDateTime lastWeek = now. minusDays(5);
        sharedHistorique.ajouter(Session.creerAvecDuree(
            lastWeek.minusHours(2), Duration.ofMinutes(25), iphone, "WhatsApp"
        ));
        sharedHistorique.ajouter(Session.creerAvecDuree(
            lastWeek.minusHours(4), Duration.ofMinutes(180), macbook, "IntelliJ IDEA"
        ));
        sharedHistorique.ajouter(Session.creerAvecDuree(
            lastWeek.minusHours(7), Duration.ofMinutes(50), ipad, "Netflix"
        ));
        
    } catch (Exception e) {
        System.err.println("Erreur lors de l'initialisation des sessions démo: " + e.getMessage());
    }
}
}


