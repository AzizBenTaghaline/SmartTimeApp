package smarttimeapp2.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx. collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml. Initializable;
import javafx. scene.control.*;
import smarttimeapp2.model. Historique;
import smarttimeapp2.model.Session;

import java.net.URL;
import java.time.LocalDate;
import java. time.format.DateTimeFormatter;
import java.util.List;
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
    
    private Historique historique;
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
            new SimpleStringProperty(cellData. getValue().appareil(). designationComplete())
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
    
    public void loadSessions() {
        if (historique == null) {
            return;
        }
        
        sessionsList = FXCollections.observableArrayList(historique.sessions());
        filteredSessions = new FilteredList<>(sessionsList, p -> true);
        
        SortedList<Session> sortedSessions = new SortedList<>(filteredSessions);
        sortedSessions.comparatorProperty().bind(tableSessions.comparatorProperty());
        
        tableSessions.setItems(sortedSessions);
        
        updateStatistics(sessionsList);
    }

    private void applyFilters() {
        if (filteredSessions == null) {
            return;
        }
        
        filteredSessions.setPredicate(session -> {
            String periode = comboPeriode.getValue();
            boolean periodMatch = switch (periode) {
                case "Aujourd'hui" -> session.appartientAuJour(LocalDate.now());
                case "Cette semaine" -> {
                    LocalDate weekAgo = LocalDate.now().minusWeeks(1);
                    yield ! session.jour().isBefore(weekAgo);
                }
                case "Ce mois" -> {
                    LocalDate monthAgo = LocalDate.now().minusMonths(1);
                    yield ! session.jour().isBefore(monthAgo);
                }
                default -> true; 
            };
            
            if (!periodMatch) {
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
}