package smarttimeapp2. view;

import javafx.application.Platform;
import javafx. fxml.FXML;
import javafx.fxml. Initializable;
import javafx. geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene. layout.*;
import smarttimeapp2. model.*;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for the Appareils (Devices) View
 */
public class AppareilsView implements Initializable {

    @FXML
    private Label labelTotalAppareils;
    
    @FXML
    private GridPane gridAppareils;
    
    private Map<String, Appareil> appareils;
    
    private static final DateTimeFormatter FORMAT_DATETIME = 
        DateTimeFormatter. ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize grid properties
        gridAppareils.setHgap(15);
        gridAppareils.setVgap(15);
        gridAppareils.setPadding(new Insets(20, 0, 0, 0));
    }
    
    /**
     * Set the appareils data source
     */
    public void setAppareils(Map<String, Appareil> appareils) {
        this.appareils = appareils;
    }
    
    /**
     * Load and display appareils
     */
    public void loadAppareils() {
        if (appareils == null) {
            return;
        }
        
        Platform.runLater(() -> {
            // Update total count
            labelTotalAppareils.setText("Total : " + appareils. size() + " appareil(s)");
            
            // Clear existing cards
            gridAppareils.getChildren().clear();
            
            // Add device cards to grid
            int col = 0;
            int row = 0;
            
            for (Appareil appareil : appareils.values()) {
                VBox card = createAppareilCard(appareil);
                gridAppareils.add(card, col, row);
                
                col++;
                if (col > 2) { // 3 columns
                    col = 0;
                    row++;
                }
            }
        });
    }
    
    /**
     * Create a device card
     */
    private VBox createAppareilCard(Appareil appareil) {
        VBox card = new VBox(10);
        card.setStyle(
            "-fx-background-color: #f8f9fa; " +
            "-fx-background-radius: 10; " +
            "-fx-padding: 20; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        card.setPrefWidth(280);
        
        // Device icon based on type
        String icon = getDeviceIcon(appareil);
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 48;");
        
        // Device name and model
        Label nameLabel = new Label(appareil. designationComplete());
        nameLabel. setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
        nameLabel.setWrapText(true);
        
        // Battery level with visual indicator
        HBox batteryBox = createBatteryIndicator(appareil. niveauBatterie());
        
        // Last usage
        Label lastUsageLabel = new Label(
            "Dernière utilisation:\n" + 
            appareil.derniereUtilisation(). format(FORMAT_DATETIME)
        );
        lastUsageLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #666;");
        
        card.getChildren().addAll(iconLabel, nameLabel, batteryBox, lastUsageLabel);
        
        // Add specific details based on device type
        if (appareil instanceof Smartphone s) {
            Label serialLabel = new Label("N° série: " + s.numeroSerie());
            serialLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #888;");
            card. getChildren().add(serialLabel);
        } else if (appareil instanceof Ordinateur o) {
            Label ramLabel = new Label("RAM: " + o.ramGo() + " Go");
            Label typeLabel = new Label("Type: " + o.type().nom());
            ramLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #888;");
            typeLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #888;");
            card.getChildren(). addAll(ramLabel, typeLabel);
        }
        
        return card;
    }
    
    /**
     * Get device icon based on type
     */
    private String getDeviceIcon(Appareil appareil) {
        if (appareil instanceof Smartphone) return "📱";
        if (appareil instanceof Tablette) return "📲";
        if (appareil instanceof Ordinateur) return "💻";
        return "📱";
    }
    
    /**
     * Create battery indicator
     */
    private HBox createBatteryIndicator(int batteryLevel) {
        HBox box = new HBox(5);
        box. setAlignment(Pos.CENTER_LEFT);
        
        String batteryIcon = batteryLevel > 75 ? "🔋" :
                           batteryLevel > 25 ? "🔋" : "🪫";
        
        Label icon = new Label(batteryIcon);
        icon.setStyle("-fx-font-size: 16;");
        
        Label levelLabel = new Label(batteryLevel + "%");
        levelLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
        
        box. getChildren().addAll(icon, levelLabel);
        return box;
    }
    
    /**
     * Refresh button action
     */
    @FXML
    private void rafraichir() {
        loadAppareils();
    }
}