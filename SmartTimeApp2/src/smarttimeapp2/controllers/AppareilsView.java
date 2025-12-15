package smarttimeapp2.controllers;

import javafx.application.Platform;
import javafx. fxml.FXML;
import javafx.fxml. Initializable;
import javafx. geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import smarttimeapp2.model.*;
import java.net.URL;
import java.time.LocalDateTime;
import java. time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import smarttimeapp2.dao.AppareilDAO;
import java.sql.*;

public class AppareilsView implements Initializable {

    @FXML
    private Label labelTotalAppareils;
    
    @FXML
    private GridPane gridAppareils;
    
    @FXML
    private Button btnAjouter;
    
    @FXML
    private Button btnSupprimer;
    
    @FXML
    private Button btnModifier;
    
    private Map<String, Appareil> appareils;
    private Appareil appareilSelectionne;
    
    private static final DateTimeFormatter FORMAT_DATETIME = 
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gridAppareils.setHgap(15);
        gridAppareils.setVgap(15);
        gridAppareils.setPadding(new Insets(20, 0, 0, 0));
    }
private AppareilDAO appareilDAO;

public void setAppareilDAO(AppareilDAO appareilDAO) {
    this.appareilDAO = appareilDAO;
}
    
@FXML
private void handleAjouter() {
    Dialog<Appareil> dialog = new Dialog<>();
    dialog.setTitle("Ajouter un appareil");
    dialog.setHeaderText("Entrez les informations du nouvel appareil");

    ButtonType btnValider = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(btnValider, ButtonType. CANCEL);

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid. setPadding(new Insets(20, 150, 10, 10));

    TextField txtNom = new TextField();
    txtNom.setPromptText("Nom");
    TextField txtModele = new TextField();
    txtModele.setPromptText("Modèle");
    
    ComboBox<String> cbType = new ComboBox<>();
    cbType.getItems(). addAll("Smartphone", "Tablette", "Ordinateur");
    cbType.setValue("Smartphone");
    
    ComboBox<Systeme> cbSysteme = new ComboBox<>();
    cbSysteme.getItems(). addAll(Systeme.values());
    cbSysteme.setValue(Systeme.ANDROID);
    
    Spinner<Integer> spinBatterie = new Spinner<>(0, 100, 100);

    grid.add(new Label("Type:"), 0, 0);
    grid.add(cbType, 1, 0);
    grid. add(new Label("Nom:"), 0, 1);
    grid.add(txtNom, 1, 1);
    grid.add(new Label("Modèle:"), 0, 2);
    grid. add(txtModele, 1, 2);
    grid. add(new Label("Système:"), 0, 3);
    grid.add(cbSysteme, 1, 3);
    grid.add(new Label("Batterie (%):"), 0, 4);
    grid.add(spinBatterie, 1, 4);

    dialog.getDialogPane().setContent(grid);

    dialog.setResultConverter(dialogButton -> {
        if (dialogButton == btnValider) {
            String nom = txtNom.getText();
            String modele = txtModele.getText();
            Systeme systeme = cbSysteme.getValue();
            int batterie = spinBatterie.getValue();
            LocalDateTime maintenant = LocalDateTime.now();

            if (nom.isBlank() || modele.isBlank()) {
                Platform.runLater(() -> 
                    showAlert(Alert. AlertType.ERROR, "Erreur", "Le nom et le modèle sont obligatoires!")
                );
                return null;
            }

            String typeChoisi = cbType.getValue();
            if ("Smartphone".equals(typeChoisi)) {
                return new Smartphone(nom, modele, systeme, batterie, maintenant, "SN-" + System.currentTimeMillis());
            } else if ("Tablette".equals(typeChoisi)) {
                return new Tablette(nom, modele, systeme, batterie, maintenant);
            } else if ("Ordinateur". equals(typeChoisi)) {
                return new Ordinateur(nom, modele, systeme, batterie, maintenant);
            }
        }
        return null;
    });

    Optional<Appareil> result = dialog.showAndWait();
    result. ifPresent(appareil -> {
        appareils.put(appareil.nom(), appareil);
        
        if (appareilDAO != null) {
            try {
                appareilDAO.save(appareil);
                System.out. println("✓ Appareil saved to database: " + appareil.nom());
                showAlert(Alert.AlertType. INFORMATION, "Succès", 
                         "Appareil ajouté avec succès!\n" + appareil.designationComplete());
            } catch (SQLException e) {
                System.err. println("❌ Failed to save appareil: " + e.getMessage());
                e.printStackTrace();
                showAlert(Alert.AlertType. ERROR, "Erreur de sauvegarde", 
                         "L'appareil a été ajouté en mémoire mais n'a pas pu être sauvegardé dans la base de données.\n" + 
                         "Erreur: " + e.getMessage());
            }
        } else {
            System.err.println("⚠️ AppareilDAO is null - appareil not saved to database");
            showAlert(Alert.AlertType. WARNING, "Avertissement", 
                     "Appareil ajouté en mémoire uniquement. La connexion à la base de données n'est pas disponible.");
        }
        
        loadAppareils();
    });
}

    @FXML
private void handleModifier() {
    if (appareils == null || appareils. isEmpty()) {
        showAlert(Alert. AlertType.WARNING, "Aucun appareil", "Il n'y a aucun appareil à modifier.");
        return;
    }

    ChoiceDialog<String> choixDialog = new ChoiceDialog<>(
        appareils.keySet().iterator().next(), 
        appareils.keySet()
    );
    choixDialog.setTitle("Modifier un appareil");
    choixDialog.setHeaderText("Sélectionnez l'appareil à modifier");
    choixDialog. setContentText("Appareil:");

    Optional<String> choix = choixDialog.showAndWait();
    choix.ifPresent(nom -> {
        Appareil appareil = appareils.get(nom);
        
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Modifier la batterie");
        dialog.setHeaderText("Modifier le niveau de batterie de: " + appareil.nom());

        ButtonType btnValider = new ButtonType("Modifier", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane(). getButtonTypes().addAll(btnValider, ButtonType. CANCEL);

        Spinner<Integer> spinBatterie = new Spinner<>(0, 100, appareil.niveauBatterie());
        VBox content = new VBox(10);
        content.getChildren().addAll(new Label("Nouveau niveau de batterie (%):"), spinBatterie);
        content.setPadding(new Insets(20));
        
        dialog. getDialogPane().setContent(content);
        dialog.setResultConverter(btn -> btn == btnValider ? spinBatterie.getValue() : null);

        Optional<Integer> result = dialog.showAndWait();
        result.ifPresent(nouveauNiveau -> {
            Appareil nouveauAppareil = null;
            
            if (appareil instanceof Smartphone) {
                Smartphone s = (Smartphone) appareil;
                nouveauAppareil = new Smartphone(s. nom(), s.modele(), s.systeme(), 
                    nouveauNiveau, s.derniereUtilisation(), s. numeroSerie());
            } else if (appareil instanceof Tablette) {
                Tablette t = (Tablette) appareil;
                nouveauAppareil = new Tablette(t. nom(), t.modele(), t.systeme(), 
                    nouveauNiveau, t.derniereUtilisation());
            } else if (appareil instanceof Ordinateur) {
                Ordinateur o = (Ordinateur) appareil;
                nouveauAppareil = new Ordinateur(o.nom(), o.modele(), o.systeme(), 
                    nouveauNiveau, o.derniereUtilisation(), o.ramGo(), o. type());
            }
            
            if (nouveauAppareil != null) {
                appareils.put(nom, nouveauAppareil);
                
                if (appareilDAO != null) {
                    try {
                        appareilDAO.save(nouveauAppareil);
                        System.out.println("✓ Appareil updated in database: " + nom);
                        showAlert(Alert.AlertType.INFORMATION, "Succès", 
                                 "Appareil modifié avec succès!\n" +
                                 "Nouveau niveau de batterie: " + nouveauNiveau + "%");
                    } catch (SQLException e) {
                        System.err.println("❌ Failed to update appareil: " + e.getMessage());
                        e.printStackTrace();
                        
                        appareils.put(nom, appareil);
                        
                        showAlert(Alert.AlertType.ERROR, "Erreur de modification", 
                                 "Impossible de sauvegarder les modifications dans la base de données.\n" + 
                                 "Erreur: " + e.getMessage());
                        return; 
                    }
                } else {
                    System.err.println("⚠️ AppareilDAO is null - appareil only modified in memory");
                    showAlert(Alert.AlertType.WARNING, "Avertissement", 
                             "Appareil modifié en mémoire uniquement.\n" +
                             "La connexion à la base de données n'est pas disponible.");
                }
                
                loadAppareils();
            }
        });
    });
}
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert. showAndWait();
    }
    
@FXML
private void handleSupprimer() {
    if (appareils == null || appareils. isEmpty()) {
        showAlert(Alert. AlertType.WARNING, "Aucun appareil", "Il n'y a aucun appareil à supprimer.");
        return;
    }

    ChoiceDialog<String> dialog = new ChoiceDialog<>(
        appareils.keySet().iterator().next(),
        appareils.keySet()
    );
    dialog.setTitle("Supprimer un appareil");
    dialog.setHeaderText("Sélectionnez l'appareil à supprimer");
    dialog.setContentText("Appareil:");

    Optional<String> result = dialog.showAndWait();
    result. ifPresent(nomAppareil -> {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation. setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer l'appareil");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer '" + nomAppareil + "' ?\nCette action est irréversible.");
        
        Optional<ButtonType> confirmResult = confirmation.showAndWait();
        if (confirmResult.isPresent() && confirmResult.get() == ButtonType.OK) {
            Appareil appareil = appareils.get(nomAppareil);
            appareils.remove(nomAppareil);
            
            if (appareilDAO != null) {
                try {
                    appareilDAO.delete(nomAppareil);
                    System. out.println("✓ Appareil deleted from database: " + nomAppareil);
                    showAlert(Alert.AlertType.INFORMATION, "Succès", 
                             "L'appareil '" + nomAppareil + "' a été supprimé avec succès!");
                } catch (SQLException e) {
                    System.err.println("❌ Failed to delete appareil: " + e.getMessage());
                    e.printStackTrace();
                    
                    appareils.put(nomAppareil, appareil);
                    
                    showAlert(Alert.AlertType.ERROR, "Erreur de suppression", 
                             "Impossible de supprimer l'appareil de la base de données.\n" + 
                             "Erreur: " + e.getMessage());
                    return; 
                }
            } else {
                System.err.println("⚠️ AppareilDAO is null - appareil only removed from memory");
                showAlert(Alert.AlertType.WARNING, "Avertissement", 
                         "L'appareil a été supprimé de la mémoire uniquement.\n" +
                         "La connexion à la base de données n'est pas disponible.");
            }
            
            loadAppareils();
        }
    });
}
    public void setAppareils(Map<String, Appareil> appareils) {
        this.appareils = appareils;
    }

    public void loadAppareils() {
        if (appareils == null) {
            return;
        }
        
        Platform.runLater(() -> {
            labelTotalAppareils. setText("Total : " + appareils.size() + " appareil(s)");
            
            gridAppareils.getChildren().clear();
            
            int col = 0;
            int row = 0;
            
            for (Appareil appareil : appareils.values()) {
                VBox card = createAppareilCard(appareil);
                gridAppareils.add(card, col, row);
                
                col++;
                if (col > 2) { 
                    col = 0;
                    row++;
                }
            }
        });
    }
    
    private VBox createAppareilCard(Appareil appareil) {
        VBox card = new VBox(10);
        card.setStyle(
            "-fx-background-color: #f8f9fa; " +
            "-fx-background-radius: 10; " +
            "-fx-padding: 20; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        card.setPrefWidth(280);
        
        String icon = getDeviceIcon(appareil);
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 48;");
        
        Label nameLabel = new Label(appareil. designationComplete());
        nameLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
        nameLabel.setWrapText(true);
        
        HBox batteryBox = createBatteryIndicator(appareil. niveauBatterie());
        
        Label lastUsageLabel = new Label(
            "Dernière utilisation:\n" + 
            appareil.derniereUtilisation(). format(FORMAT_DATETIME)
        );
        lastUsageLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #666;");
        
        card.getChildren().addAll(iconLabel, nameLabel, batteryBox, lastUsageLabel);
        
        return card;
    }
    
private String getDeviceIcon(Appareil appareil) {
    if (appareil instanceof Smartphone) {
        return "📱";
    } else if (appareil instanceof Tablette) {
        return "📱";
    } else if (appareil instanceof Ordinateur) {
        return "💻";
    }
    return "📱";
}
    
    private HBox createBatteryIndicator(int niveau) {
        HBox box = new HBox(5);
        box.setAlignment(Pos. CENTER_LEFT);
        
        ProgressBar progressBar = new ProgressBar(niveau / 100.0);
        progressBar.setPrefWidth(150);
        
        Label label = new Label(niveau + "%");
        label.setStyle("-fx-font-size: 12;");
        
        box.getChildren().addAll(progressBar, label);
        return box;
    }
}