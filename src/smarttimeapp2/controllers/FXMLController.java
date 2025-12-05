package smarttimeapp2.controllers;

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
import smarttimeapp2.controllers.AppareilsView;
import smarttimeapp2.controllers.ObjectifsView;
import smarttimeapp2.controllers.SessionsView;
import smarttimeapp2.model.Appareil;
import smarttimeapp2.model.Ordinateur;
import smarttimeapp2.model.Smartphone;
import smarttimeapp2.model.Systeme;
import smarttimeapp2.model.Tablette;


public class FXMLController implements Initializable {
    
    @FXML
    private Label labelDateHeure;
    
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
    
    @FXML
    private StackPane contentPane;
    
    @FXML
    private VBox dashboardView;
    
    @FXML
    private Label labelCardNbSessions;
    @FXML
    private Label labelCardTempsTotal;
    @FXML
    private Label labelCardMoyenne;
    
    @FXML
    private PieChart pieChartAppareils;
    
    @FXML
    private VBox vboxObjectifs;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        updateDateTime();
        startDateTimeUpdater();
        
        initializeDashboard();
        
        setActiveButton(btnDashboard);
    }
    
    private static Historique sharedHistorique = null;
    

    private void updateDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("📅 dd/MM/yyyy HH:mm");
        labelDateHeure.setText(formatter.format(now));
    }
    
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
    

    private void initializeDashboard() {
        labelCardNbSessions.setText("4");
        labelCardTempsTotal.setText("6h15");
        labelCardMoyenne.setText("93");
        
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("Ordinateur", 45),
            new PieChart.Data("Smartphone", 30),
            new PieChart.Data("Tablette", 25)
        );
        pieChartAppareils.setData(pieChartData);
        pieChartAppareils.setLegendVisible(true);
        
        initializeObjectives();
    }
    
    private void initializeObjectives() {
        vboxObjectifs.getChildren().clear();
        
        addObjectiveItem("Limiter le temps d'écran à 6h", 75);
        addObjectiveItem("Faire 5 pauses actives", 60);
        addObjectiveItem("Temps de sommeil: 8h minimum", 100);
    }
    

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
    
    private void setActiveButton(Button activeButton) {
        btnDashboard.getStyleClass().remove("nav-button-active");
        btnAppareils.getStyleClass().remove("nav-button-active");
        btnSessions.getStyleClass().remove("nav-button-active");
        btnStatistiques.getStyleClass().remove("nav-button-active");
        btnObjectifs.getStyleClass().remove("nav-button-active");
        
        if (!activeButton.getStyleClass().contains("nav-button-active")) {
            activeButton.getStyleClass().add("nav-button-active");
        }
    }
    
    @FXML
    private void afficherDashboard() {
        setActiveButton(btnDashboard);
        contentPane.getChildren().clear();
        contentPane.getChildren().add(dashboardView);
    }
    
@FXML
    private void afficherAppareils() {
        setActiveButton(btnAppareils);
    try {
        System.out.println("Loading AppareilsView. fxml...");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/smarttimeapp2/view/AppareilsView.fxml"));
        VBox appareilsView = loader.load();
        
        AppareilsView controller = loader. getController();
        controller.setAppareils(getAppareils());
        controller.loadAppareils();
        
        contentPane.getChildren().clear();
        contentPane.getChildren().add(appareilsView);
    } catch (IOException e) {
        System.err.println("ERROR loading appareils view:");
        e.printStackTrace();
        showPlaceholderView("📱 Mes appareils", "Erreur lors du chargement de la vue des appareils.");
    }
}

    
     @FXML
    private void afficherSessions() {
    setActiveButton(btnSessions);
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/smarttimeapp2/view/SessionsView.fxml"));
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
        showPlaceholderView("📈 Statistiques","Erreur");
    }
    
    @FXML
    private void afficherObjectifs() {
        setActiveButton(btnObjectifs);
    try {
        System.out.println("Loading ObjectifsView. fxml...");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/smarttimeapp2/view/ObjectifsView.fxml"));
        VBox objectifsView = loader.load();
        
        ObjectifsView controller = loader.getController();
        controller.setHistorique(getHistorique());
        controller. loadObjectifs();
        
        contentPane.getChildren().clear();
        contentPane.getChildren().add(objectifsView);
    } catch (IOException e) {
        System.err.println("ERROR loading objectifs view:");
        e.printStackTrace();
        showPlaceholderView("🎯 Objectifs", "Erreur lors du chargement de la vue des objectifs.");
    }
}
    
    @FXML
    private void ajouterSession() {
        System.out.println("Ajouter une nouvelle session");
    }
    
    @FXML
    private void quitter() {
        Platform.exit();
    }
    

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

private Historique getHistorique() {
    if (sharedHistorique == null) {
        sharedHistorique = new Historique();
        initializeDemoSessions();
    }
    return sharedHistorique;
}


private void initializeDemoSessions() {
    try {
        Smartphone iphone = Smartphone.creer("iPhone", "14 Pro", Systeme.IOS);
        Tablette ipad = Tablette.creer("iPad", "Air", Systeme. IPADOS);
        Ordinateur macbook = Ordinateur.creer("MacBook", "Pro M2", Systeme.MACOS);
        
        LocalDateTime now = LocalDateTime.now();
        
        sharedHistorique.ajouter(Session.creerAvecDuree(
            now.minusHours(2), Duration.ofMinutes(45), iphone, "Instagram"
        ));
        sharedHistorique.ajouter(Session.creerAvecDuree(
            now.minusHours(4), Duration.ofMinutes(120), macbook, "VSCode"
        ));
        sharedHistorique.ajouter(Session.creerAvecDuree(
            now. minusHours(5), Duration.ofMinutes(30), ipad, "YouTube"
        ));
        
        LocalDateTime yesterday = now.minusDays(1);
        sharedHistorique.ajouter(Session.creerAvecDuree(
            yesterday. minusHours(3), Duration.ofMinutes(90), iphone, "TikTok"
        ));
        sharedHistorique.ajouter(Session.creerAvecDuree(
            yesterday. minusHours(6), Duration.ofMinutes(60), macbook, "Chrome"
        ));
        
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
private Map<String, Appareil> getAppareils() {
    Map<String, Appareil> appareils = new HashMap<>();
    
    // Données de démonstration
    appareils. put("iPhone", Smartphone.creer("iPhone", "14 Pro", Systeme.IOS));
    appareils. put("MacBook", Ordinateur.creer("MacBook", "Pro M2", Systeme.MACOS));
    appareils.put("iPad", Tablette.creer("iPad", "Air", Systeme. IPADOS));
    
    return appareils;
}
}


