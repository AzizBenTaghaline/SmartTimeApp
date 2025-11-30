package smarttimeapp2.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control. Label;
import javafx.scene. layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import smarttimeapp2.model.*;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class AppareilsView extends VBox {
    
    private static final DateTimeFormatter FORMAT_DATETIME = 
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    public AppareilsView(Map<String, Appareil> appareils) {
        super(20);
        setStyle("-fx-padding: 20; -fx-background-color: white;");
        
        // Title
        Label titleLabel = new Label("📱 Mes Appareils");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");
        
        // Device count
        Label countLabel = new Label("Total : " + appareils.size() + " appareil(s)");
        countLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #666;");
        
        getChildren().addAll(titleLabel, countLabel);
        
        // Create grid for device cards
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid. setPadding(new Insets(20, 0, 0, 0));
        
        int col = 0;
        int row = 0;
        
        for (Appareil appareil : appareils.values()) {
            VBox card = createAppareilCard(appareil);
            grid.add(card, col, row);
            
            col++;
            if (col > 2) { // 3 columns
                col = 0;
                row++;
            }
        }
        
        getChildren().add(grid);
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
        
        // Device icon based on type
        String icon = getDeviceIcon(appareil);
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 48;");
        
        // Device name and model
        Label nameLabel = new Label(appareil.designationComplete());
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
            card.getChildren().add(serialLabel);
        } else if (appareil instanceof Ordinateur o) {
            Label ramLabel = new Label("RAM: " + o.ramGo() + " Go");
            Label typeLabel = new Label("Type: " + o.type().nom());
            ramLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #888;");
            typeLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #888;");
            card.getChildren().addAll(ramLabel, typeLabel);
        }
        
        return card;
    }
    
    private String getDeviceIcon(Appareil appareil) {
        if (appareil instanceof Smartphone) return "📱";
        if (appareil instanceof Tablette) return "📲";
        if (appareil instanceof Ordinateur) return "💻";
        return "📱";
    }
    
    private HBox createBatteryIndicator(int batteryLevel) {
        HBox box = new HBox(5);
        box.setAlignment(Pos.CENTER_LEFT);
        
        String batteryIcon = batteryLevel > 75 ? "🔋" :
                           batteryLevel > 25 ? "🔋" : "🪫";
        
        Label icon = new Label(batteryIcon);
        icon.setStyle("-fx-font-size: 16;");
        
        Label levelLabel = new Label(batteryLevel + "%");
        levelLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
        
        box.getChildren().addAll(icon, levelLabel);
        return box;
    }
}