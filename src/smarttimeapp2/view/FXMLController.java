/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package smarttimeapp2.view;

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

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

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
        // TODO: Implement appareils view
        showPlaceholderView("📱 Mes appareils", "La gestion des appareils sera disponible prochainement.");
    }
    
    @FXML
    private void afficherSessions() {
        setActiveButton(btnSessions);
        // TODO: Implement sessions view
        showPlaceholderView("⏱ Mes sessions", "L'historique des sessions sera disponible prochainement.");
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
}