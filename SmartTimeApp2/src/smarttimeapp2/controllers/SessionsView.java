package smarttimeapp2.controllers;

import javafx.application.Platform;
import javafx. beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation. SortedList;
import javafx.fxml.FXML;
import javafx.fxml. Initializable;
import javafx. geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import smarttimeapp2.model.*;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time. LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util. List;
import java.util. Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class SessionsView implements Initializable {

    @FXML
    private TableView<Session> tableSessions;
    
    @FXML
    private TableColumn<Session, String> colDate;
    
    @FXML
    private TableColumn<Session, String> colDebut;
    
    @FXML
    private TableColumn<Session, String> colFin;
    
    @FXML
    private TableColumn<Session, String> colApplication;
    
    @FXML
    private TableColumn<Session, String> colAppareil;
    
    @FXML
    private TableColumn<Session, String> colDuree;
    
    @FXML
    private ComboBox<String> comboPeriode;
    
    @FXML
    private TextField txtRecherche;
    
    @FXML
    private Label labelNbSessions;
    
    @FXML
    private Label labelTempsTotal;
    
    @FXML
    private Label labelMoyenne;
    
    @FXML
    private Button btnAjouter;
    
    @FXML
    private Button btnModifier;
    
    @FXML
    private Button btnSupprimer;
    
    private Historique historique;
    private Map<String, Appareil> appareils;
    private ObservableList<Session> sessionsList;
    private FilteredList<Session> filteredSessions;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        
        comboPeriode.setItems(FXCollections.observableArrayList(
            "Aujourd'hui",
            "Cette semaine",
            "Ce mois",
            "Toutes"
        ));
        comboPeriode.setValue("Toutes");
        
        comboPeriode.setOnAction(e -> applyFilters());
        txtRecherche.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }
    
    private void setupTableColumns() {
        colDate.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().debut().format(DATE_FORMATTER))
        );
        
        colDebut.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue(). debut().format(TIME_FORMATTER))
        );
        
        colFin.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().fin().format(TIME_FORMATTER))
        );
        
        colApplication.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().application())
        );
        
        colAppareil.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().appareil().designationComplete())
        );
        
        colDuree.setCellValueFactory(cellData -> 
            new SimpleStringProperty(formatDuration(cellData.getValue().dureeMinutes()))
        );
        
        colDuree.setCellFactory(column -> new TableCell<Session, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    
                    Session session = getTableView().getItems().get(getIndex());
                    long minutes = session.dureeMinutes();
                    
                    if (minutes < 30) {
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    } else if (minutes < 120) {
                        setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;"); 
                    } else {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;"); 
                    }
                }
            }
        });
    }
    
    @FXML
private void handleAjouter() {
    if (appareils == null) {
    appareils = new java.util.HashMap<>();
}

if (appareils. isEmpty()) {
    Appareil appareilDefaut = Smartphone.creer("Mon Smartphone", "Appareil par défaut", Systeme. ANDROID);
    appareils. put(appareilDefaut.nom(), appareilDefaut);
}
    
    Dialog<Session> dialog = new Dialog<>();
    dialog.setTitle("Ajouter une session");
    dialog.setHeaderText("Entrez les informations de la nouvelle session");

    ButtonType btnValider = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes(). addAll(btnValider, ButtonType.CANCEL);

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    ComboBox<Appareil> cbAppareil = new ComboBox<>();
    cbAppareil.getItems().addAll(appareils. values());
    cbAppareil.setPromptText("Sélectionner un appareil");
    
    cbAppareil.setCellFactory(param -> new ListCell<Appareil>() {
        @Override
        protected void updateItem(Appareil item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
            } else {
                setText(item.designationComplete());
            }
        }
    });
    cbAppareil.setButtonCell(new ListCell<Appareil>() {
        @Override
        protected void updateItem(Appareil item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
            } else {
                setText(item.designationComplete());
            }
        }
    });
    
    TextField txtApplication = new TextField();
    txtApplication.setPromptText("Ex: Chrome, Word, Netflix.. .");
    
    DatePicker dateDebut = new DatePicker(LocalDate.now());
    
    Spinner<Integer> spinHeureDebut = new Spinner<>(0, 23, LocalTime.now().getHour());
    spinHeureDebut.setEditable(true);
    
    Spinner<Integer> spinMinuteDebut = new Spinner<>(0, 59, LocalTime.now().getMinute());
    spinMinuteDebut. setEditable(true);
    
    Spinner<Integer> spinDureeHeures = new Spinner<>(0, 23, 0);
    spinDureeHeures.setEditable(true);
    
    Spinner<Integer> spinDureeMinutes = new Spinner<>(1, 59, 30);
    spinDureeMinutes.setEditable(true);

    grid.add(new Label("Appareil:"), 0, 0);
    grid.add(cbAppareil, 1, 0);
    grid. add(new Label("Application:"), 0, 1);
    grid.add(txtApplication, 1, 1);
    grid.add(new Label("Date:"), 0, 2);
    grid.add(dateDebut, 1, 2);
    grid.add(new Label("Heure de début (HH):"), 0, 3);
    grid.add(spinHeureDebut, 1, 3);
    grid.add(new Label("Minute de début (MM):"), 0, 4);
    grid.add(spinMinuteDebut, 1, 4);
    grid.add(new Label("Durée (Heures):"), 0, 5);
    grid.add(spinDureeHeures, 1, 5);
    grid.add(new Label("Durée (Minutes):"), 0, 6);
    grid.add(spinDureeMinutes, 1, 6);

    dialog.getDialogPane(). setContent(grid);

    javafx.scene.Node btnAjouterNode = dialog.getDialogPane(). lookupButton(btnValider);
    btnAjouterNode.setDisable(true);
    
    cbAppareil.valueProperty().addListener((obs, oldVal, newVal) -> {
        btnAjouterNode.setDisable(newVal == null || txtApplication.getText().trim().isEmpty());
    });
    
    txtApplication.textProperty().addListener((obs, oldVal, newVal) -> {
        btnAjouterNode.setDisable(cbAppareil.getValue() == null || newVal.trim().isEmpty());
    });

    dialog.setResultConverter(dialogButton -> {
        if (dialogButton == btnValider) {
            try {
                Appareil appareil = cbAppareil.getValue();
                String application = txtApplication.getText().trim();
                LocalDate date = dateDebut.getValue();
                int heureDebut = spinHeureDebut.getValue();
                int minuteDebut = spinMinuteDebut. getValue();
                int dureeHeures = spinDureeHeures.getValue();
                int dureeMinutes = spinDureeMinutes.getValue();
                
                if (appareil == null) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner un appareil.");
                    return null;
                }
                
                if (application.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer le nom de l'application.");
                    return null;
                }
                
                if (dureeHeures == 0 && dureeMinutes == 0) {
                    showAlert(Alert.AlertType. ERROR, "Erreur", "La durée doit être supérieure à 0.");
                    return null;
                }
                
                LocalDateTime debut = LocalDateTime.of(date, LocalTime.of(heureDebut, minuteDebut));
                Duration duree = Duration.ofHours(dureeHeures).plusMinutes(dureeMinutes);
                
                return Session.creerAvecDuree(debut, duree, appareil, application);
                
            } catch (Exception e) {
                showAlert(Alert. AlertType.ERROR, "Erreur", "Erreur lors de la création de la session: " + e.getMessage());
                return null;
            }
        }
        return null;
    });

    Optional<Session> result = dialog. showAndWait();
        if (result.isPresent()) {
        Session session = result.get();
        try {
            historique.ajouter(session);
            loadSessions();
            showAlert(Alert.AlertType. INFORMATION, "Succès", 
                "Session ajoutée avec succès!\n\n" +
                "Application: " + session.application() + "\n" +
                "Durée: " + formatDuration(session.dureeMinutes()));
        } catch (IllegalStateException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de chevauchement", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType. ERROR, "Erreur", "Erreur lors de l'ajout: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
    
    @FXML
    private void handleModifier() {
        Session sessionSelectionnee = tableSessions.getSelectionModel().getSelectedItem();
    
        if (sessionSelectionnee == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", 
            "Veuillez sélectionner une session dans le tableau pour la modifier.");
        return;
    }
    
    Dialog<Duration> dialog = new Dialog<>();
    dialog.setTitle("Modifier la session");
    dialog.setHeaderText("Modifier la durée de: " + sessionSelectionnee.application());

    ButtonType btnValider = new ButtonType("Modifier", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes(). addAll(btnValider, ButtonType.CANCEL);

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));
    
    long dureeActuelle = sessionSelectionnee.dureeMinutes();
    long heuresActuelles = dureeActuelle / 60;
    long minutesActuelles = dureeActuelle % 60;
    
    Spinner<Integer> spinHeures = new Spinner<>(0, 23, (int)heuresActuelles);
    Spinner<Integer> spinMinutes = new Spinner<>(0, 59, (int)minutesActuelles);

    grid.add(new Label("Nouvelle durée (heures):"), 0, 0);
    grid.add(spinHeures, 1, 0);
    grid.add(new Label("Minutes:"), 0, 1);
    grid.add(spinMinutes, 1, 1);
    
    dialog.getDialogPane().setContent(grid);
    dialog.setResultConverter(btn -> {
        if (btn == btnValider) {
            int heures = spinHeures.getValue();
            int minutes = spinMinutes.getValue();
            return Duration.ofHours(heures).plusMinutes(minutes);
        }
        return null;
    });

    Optional<Duration> result = dialog.showAndWait();
    result. ifPresent(nouvelleDuree -> {
        try {
            Session nouvelleSession = Session.creerAvecDuree(
                sessionSelectionnee. debut(),
                nouvelleDuree,
                sessionSelectionnee.appareil(),
                sessionSelectionnee.application()
            );
            
            historique.modifier(sessionSelectionnee, nouvelleSession);
            
            loadSessions();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Session modifiée avec succès!");
        } catch (IllegalStateException e) {
            showAlert(Alert. AlertType.ERROR, "Erreur", e.getMessage());
        }
    });
}

    @FXML
    private void handleSupprimer() {
        Session sessionSelectionnee = tableSessions.getSelectionModel(). getSelectedItem();
    
        if (sessionSelectionnee == null) {
         showAlert(Alert.AlertType.WARNING, "Aucune sélection", 
            "Veuillez sélectionner une session dans le tableau pour la supprimer.");
        return;
    }
    
    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
    confirmation. setTitle("Confirmation de suppression");
    confirmation.setHeaderText("Êtes-vous sûr de vouloir supprimer cette session?");
    confirmation. setContentText(
        "Application: " + sessionSelectionnee. application() + "\n" +
        "Appareil: " + sessionSelectionnee.appareil(). nom() + "\n" +
        "Date: " + sessionSelectionnee.debut().format(DATE_FORMATTER) + "\n" +
        "Durée: " + formatDuration(sessionSelectionnee.dureeMinutes())
    );

    Optional<ButtonType> result = confirmation.showAndWait();
    if (result.isPresent() && result. get() == ButtonType.OK) {
        boolean supprime = historique.supprimer(sessionSelectionnee);
        
        if (supprime) {
            loadSessions();
            showAlert(Alert.AlertType. INFORMATION, "Succès", 
                "La session a été supprimée avec succès!");
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                "Impossible de supprimer la session.");
        }
    }
}
    
    private String formatDuration(long totalMinutes) {
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
    
    public void setHistorique(Historique historique) {
        this.historique = historique;
    }
    
    public void setAppareils(Map<String, Appareil> appareils) {
        this.appareils = appareils;
    }
    
    public void loadSessions() {
     if (historique == null) {
        System.out.println("ERREUR: Historique est null!");
        return;
     }
    
     System.out.println("Chargement de " + historique.nombreSessions() + " sessions");
    
    sessionsList = FXCollections.observableArrayList(historique.sessions());
    filteredSessions = new FilteredList<>(sessionsList, p -> true);
    
    SortedList<Session> sortedSessions = new SortedList<>(filteredSessions);
    sortedSessions.comparatorProperty(). bind(tableSessions.comparatorProperty());
    
    tableSessions.setItems(sortedSessions);
    
    updateStatistics(sessionsList);
    
    System.out.println("Table mise à jour avec " + tableSessions.getItems().size() + " éléments");
}
    
    private void applyFilters() {
        if (filteredSessions == null) {
            return;
        }
        
        filteredSessions.setPredicate(session -> {
            String periode = comboPeriode.getValue();
            boolean periodMatch;
            
            if ("Aujourd'hui".equals(periode)) {
                periodMatch = session.appartientAuJour(LocalDate.now());
            } else if ("Cette semaine".equals(periode)) {
                LocalDate weekAgo = LocalDate.now().minusWeeks(1);
                periodMatch = ! session.jour().isBefore(weekAgo);
            } else if ("Ce mois". equals(periode)) {
                LocalDate monthAgo = LocalDate. now().minusMonths(1);
                periodMatch = !session.jour().isBefore(monthAgo);
            } else {
                periodMatch = true;
            }
            
            if (! periodMatch) {
                return false;
            }
            
            String searchText = txtRecherche.getText();
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }
            
            String lowerCaseFilter = searchText.toLowerCase();
            return session.application().toLowerCase().contains(lowerCaseFilter) ||
                   session.appareil(). nom().toLowerCase().contains(lowerCaseFilter) ||
                   session.appareil().modele().toLowerCase().contains(lowerCaseFilter);
        });
        
        updateStatistics(filteredSessions);
    }

    private void updateStatistics(List<Session> sessions) {
        Platform.runLater(() -> {
            int nbSessions = sessions.size();
            long totalMinutes = sessions.stream()
                .mapToLong(Session::dureeMinutes)
                .sum();
            
            labelNbSessions.setText(String.valueOf(nbSessions));
            labelTempsTotal.setText(formatDuration(totalMinutes));
            
            if (nbSessions > 0) {
                long avgMinutes = totalMinutes / nbSessions;
                labelMoyenne.setText(formatDuration(avgMinutes));
            } else {
                labelMoyenne.setText("0 min");
            }
        });
    }
    
    @FXML
    private void rafraichir() {
        loadSessions();
        applyFilters();
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert. setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}