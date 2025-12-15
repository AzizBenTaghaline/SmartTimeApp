package smarttimeapp2.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import smarttimeapp2.model.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class StatisticsView implements Initializable {

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> periodComboBox;
    @FXML private Button refreshButton;
    
    @FXML private Label totalTimeLabel;
    @FXML private Label avgDailyTimeLabel;
    @FXML private Label mostProductiveDayLabel;
    @FXML private Label tasksCompletedLabel;
    
    @FXML private PieChart categoryPieChart;
    @FXML private LineChart dailyTimeChart;
    @FXML private BarChart weeklyProductivityChart;
    
    @FXML private ComboBox<String> reportTypeComboBox;
    @FXML private Button generateReportButton;
    @FXML private ScrollPane reportScrollPane;
    @FXML private VBox reportContainer;

    private Historique historique;
    
    private Map<LocalDate, Double> dailyTimeData = new HashMap<>();
    private Map<String, Double> categoryTimeData = new HashMap<>();
    private int totalTasksCompleted = 0;

    private static final DateTimeFormatter FORMAT_DATE = 
        DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    private static final DecimalFormat DECIMAL_FORMAT;
    
    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DECIMAL_FORMAT = new DecimalFormat("0.0", symbols);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupComboBoxes();
        setupDatePickers();
        setupCharts();
        
        // Load some initial data for testing
        loadDataFromHistorique();
        refreshStatistics();
    }

    private void setupCharts() {
        // Configure daily time chart
        if (dailyTimeChart != null) {
            dailyTimeChart.setTitle("Évolution du Temps Quotidien");
            dailyTimeChart.setAnimated(false); // Disable animation initially for better loading
            dailyTimeChart.setLegendVisible(true);
            dailyTimeChart.setCreateSymbols(true);
        }
        
        // Configure weekly productivity chart
        if (weeklyProductivityChart != null) {
            weeklyProductivityChart.setTitle("Productivité Hebdomadaire");
            weeklyProductivityChart.setAnimated(false); // Disable animation initially
            weeklyProductivityChart.setLegendVisible(true);
            weeklyProductivityChart.setCategoryGap(10.0);
        }
        
        // Configure pie chart
        if (categoryPieChart != null) {
            categoryPieChart.setTitle("Répartition du Temps par Catégorie");
            categoryPieChart.setAnimated(false); // Disable animation initially
            categoryPieChart.setLegendVisible(true);
            categoryPieChart.setStartAngle(90.0);
        }
    }

    public void setHistorique(Historique historique) {
        this.historique = historique;
    }

    public void loadStatistics() {
        if (historique == null) {
            return;
        }
        
        Platform.runLater(() -> {
            loadDataFromHistorique();
            refreshStatistics();
        });
    }

    private void setupComboBoxes() {
        // Configuration du combo box de période
        periodComboBox.setItems(FXCollections.observableArrayList(
            "7 derniers jours",
            "30 derniers jours",
            "3 derniers mois",
            "6 derniers mois",
            "Dernière année",
            "Tout"
        ));
        
        periodComboBox.setOnAction(e -> {
            String selected = periodComboBox.getValue();
            if (selected != null) {
                setDateRangeFromSelection(selected);
            }
        });

        // Configuration du combo box de rapports
        reportTypeComboBox.setItems(FXCollections.observableArrayList(
            "Résumé quotidien",
            "Résumé hebdomadaire", 
            "Répartition par catégorie",
            "Tendances de productivité",
            "Rapport de tâches"
        ));
    }

    private void setupDatePickers() {
        LocalDate today = LocalDate.now();
        startDatePicker.setValue(today.minusDays(30));
        endDatePicker.setValue(today);
        
        // Ajout des listeners sur les date pickers
        startDatePicker.setOnAction(e -> refreshStatistics());
        endDatePicker.setOnAction(e -> refreshStatistics());
    }

    private void setDateRangeFromSelection(String period) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today;

        switch (period) {
            case "7 derniers jours":
                startDate = today.minusDays(7);
                break;
            case "30 derniers jours":
                startDate = today.minusDays(30);
                break;
            case "3 derniers mois":
                startDate = today.minusMonths(3);
                break;
            case "6 derniers mois":
                startDate = today.minusMonths(6);
                break;
            case "Dernière année":
                startDate = today.minusYears(1);
                break;
            case "Tout":
                startDate = LocalDate.of(2020, 1, 1);
                break;
        }

        startDatePicker.setValue(startDate);
        endDatePicker.setValue(today);
        refreshStatistics();
    }

    private void loadDataFromHistorique() {
        // Clear existing data
        dailyTimeData.clear();
        categoryTimeData.clear();
        totalTasksCompleted = 0;
        
        // If no historique, return early
        if (historique == null || historique.estVide()) {
            return;
        }
        
        // Get date range from pickers
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        
        if (startDate == null || endDate == null) {
            return;
        }
        
        // Get all sessions from historique
        List<Session> allSessions = historique.sessions();
        
        // Filter sessions by date range
        List<Session> filteredSessions = allSessions.stream()
            .filter(s -> !s.jour().isBefore(startDate) && !s.jour().isAfter(endDate))
            .collect(Collectors.toList());
        
        // Calculate daily time data using Statistiques utility
        Map<LocalDate, java.time.Duration> dureeParJourMap = Statistiques.dureeParJour(filteredSessions);
        
        // Convert Duration to hours (Double) for dailyTimeData
        for (Map.Entry<LocalDate, java.time.Duration> entry : dureeParJourMap.entrySet()) {
            double hours = entry.getValue().toMinutes() / 60.0;
            dailyTimeData.put(entry.getKey(), Math.round(hours * 10.0) / 10.0);
        }
        
        // Calculate category time data from session applications
        Map<String, Double> appTimeMap = new HashMap<>();
        for (Session session : filteredSessions) {
            String application = session.application();
            double hours = session.dureeMinutes() / 60.0;
            appTimeMap.merge(application, hours, Double::sum);
        }
        
        // Round and store application times
        for (Map.Entry<String, Double> entry : appTimeMap.entrySet()) {
            categoryTimeData.put(entry.getKey(), Math.round(entry.getValue() * 10.0) / 10.0);
        }
        
        // Calculate total tasks completed (using number of sessions as proxy)
        totalTasksCompleted = filteredSessions.size();
    }

    @FXML
    private void rafraichir() {
        refreshStatistics();
    }

    private void refreshStatistics() {
        updateSummaryLabels();
        updateCharts();
        
        // Re-enable animations after initial load
        Platform.runLater(() -> {
            if (dailyTimeChart != null) dailyTimeChart.setAnimated(true);
            if (weeklyProductivityChart != null) weeklyProductivityChart.setAnimated(true);
            if (categoryPieChart != null) categoryPieChart.setAnimated(true);
        });
    }

    private void updateSummaryLabels() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        
        if (startDate == null || endDate == null) return;

        // Calcul du temps total pour la période sélectionnée
        double totalHours = dailyTimeData.entrySet().stream()
            .filter(entry -> ! entry.getKey().isBefore(startDate) && !entry.getKey().isAfter(endDate))
            .mapToDouble(Map.Entry::getValue)
            .sum();

        // Calcul de la moyenne quotidienne
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        double avgDaily = daysBetween > 0 ?  totalHours / daysBetween : 0;

        // Recherche du jour le plus productif
        String mostProductiveDay = dailyTimeData.entrySet().stream()
            .filter(entry -> !entry.getKey().isBefore(startDate) && !entry.getKey().isAfter(endDate))
            .max(Map.Entry.comparingByValue())
            .map(entry -> entry.getKey().format(FORMAT_DATE))
            .orElse("N/A");

        // Mise à jour des labels using DecimalFormat instead of String.format
        Platform.runLater(() -> {
            totalTimeLabel.setText(DECIMAL_FORMAT.format(totalHours) + "h");
            avgDailyTimeLabel.setText(DECIMAL_FORMAT.format(avgDaily) + "h");
            mostProductiveDayLabel.setText(mostProductiveDay);
            tasksCompletedLabel.setText(String.valueOf(totalTasksCompleted));
        });
    }

    private void updateCharts() {
        Platform.runLater(() -> {
            updatePieChart();
            updateLineChart();
            updateBarChart();
        });
    }

    private void updatePieChart() {
        if (categoryPieChart == null) return;
        
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        
        for (Map.Entry<String, Double> entry : categoryTimeData.entrySet()) {
            if (entry.getValue() > 0) { // Only add non-zero values
                pieData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
            }
        }
        
        categoryPieChart.setData(pieData);
    }

    @SuppressWarnings("unchecked")
    private void updateLineChart() {
        if (dailyTimeChart == null) return;
        
        dailyTimeChart.getData().clear();
        
        XYChart.Series series = new XYChart.Series();
        series.setName("Heures travaillées");

        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        
        if (startDate == null || endDate == null) return;

        // Collect and sort data points
        Map<LocalDate, Double> filteredData = dailyTimeData.entrySet().stream()
            .filter(entry -> !entry.getKey().isBefore(startDate) && !entry.getKey().isAfter(endDate))
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue
            ));

        // Add data points in chronological order
        filteredData.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> {
                String dateLabel = entry.getKey().format(DateTimeFormatter.ofPattern("dd/MM"));
                series.getData().add(new XYChart.Data(dateLabel, entry.getValue()));
            });

        if (! series.getData().isEmpty()) {
            dailyTimeChart.getData().add(series);
        }
    }

    @SuppressWarnings("unchecked")
    private void updateBarChart() {
        if (weeklyProductivityChart == null) return;
        
        weeklyProductivityChart.getData().clear();
        
        XYChart.Series series = new XYChart.Series();
        series.setName("Heures par semaine");

        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        
        if (startDate == null || endDate == null) return;

        // Calculer les données hebdomadaires à partir des données quotidiennes
        Map<String, Double> weeklyData = new HashMap<>();
        
        dailyTimeData.entrySet().stream()
            .filter(entry -> !entry.getKey().isBefore(startDate) && ! entry.getKey().isAfter(endDate))
            .forEach(entry -> {
                String weekKey = "S" + entry.getKey().format(DateTimeFormatter.ofPattern("ww"));
                weeklyData.merge(weekKey, entry.getValue(), Double::sum);
            });

        // Add data to chart in sorted order
        weeklyData.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> {
                series.getData().add(new XYChart.Data(entry.getKey(), Math.round(entry.getValue() * 10.0) / 10.0));
            });

        if (!series.getData().isEmpty()) {
            weeklyProductivityChart.getData().add(series);
        }
    }

    @FXML
    private void genererRapport(ActionEvent event) {
        String reportType = reportTypeComboBox.getValue();
        if (reportType == null) return;

        reportContainer.getChildren().clear();
        
        Label reportTitle = new Label("Rapport: " + reportType);
        reportTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        reportContainer.getChildren().add(reportTitle);

        generateReportContent(reportType);
    }

    private void generateReportContent(String reportType) {
        Platform.runLater(() -> {
            switch (reportType) {
                case "Résumé quotidien":
                    generateDailySummaryReport();
                    break;
                case "Résumé hebdomadaire":
                    generateWeeklySummaryReport();
                    break;
                case "Répartition par catégorie":
                    generateCategoryBreakdownReport();
                    break;
                case "Tendances de productivité":
                    generateProductivityTrendsReport();
                    break;
                case "Rapport de tâches":
                    generateTaskCompletionReport();
                    break;
            }
        });
    }

    private void generateDailySummaryReport() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        
        if (startDate == null || endDate == null) return;

        dailyTimeData.entrySet().stream()
            .filter(entry -> !entry.getKey().isBefore(startDate) && ! entry.getKey().isAfter(endDate))
            .sorted(Map.Entry.<LocalDate, Double>comparingByKey().reversed())
            .forEach(entry -> {
                Label dayLabel = new Label(entry.getKey().format(FORMAT_DATE) + ": " + 
                    DECIMAL_FORMAT.format(entry.getValue()) + " heures");
                dayLabel.setStyle("-fx-padding: 5;");
                reportContainer.getChildren().add(dayLabel);
            });
    }

    private void generateWeeklySummaryReport() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        
        if (startDate == null || endDate == null) return;

        Map<String, Double> weeklyData = new HashMap<>();
        
        dailyTimeData.entrySet().stream()
            .filter(entry -> !entry.getKey().isBefore(startDate) && !entry.getKey().isAfter(endDate))
            .forEach(entry -> {
                String weekKey = "Semaine " + entry.getKey().format(DateTimeFormatter.ofPattern("ww/yyyy"));
                weeklyData.merge(weekKey, entry.getValue(), Double::sum);
            });

        weeklyData.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> {
                Label weekLabel = new Label(entry.getKey() + ": " + 
                    DECIMAL_FORMAT.format(entry.getValue()) + " heures");
                weekLabel.setStyle("-fx-padding: 5;");
                reportContainer.getChildren().add(weekLabel);
            });
    }

    private void generateCategoryBreakdownReport() {
        double total = categoryTimeData.values().stream().mapToDouble(Double::doubleValue).sum();
        
        categoryTimeData.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .forEach(entry -> {
                double percentage = total > 0 ? (entry.getValue() / total) * 100 : 0;
                Label categoryLabel = new Label(entry.getKey() + ": " + 
                    DECIMAL_FORMAT.format(entry.getValue()) + " heures (" + 
                    DECIMAL_FORMAT.format(percentage) + "%)");
                categoryLabel.setStyle("-fx-padding: 5;");
                reportContainer.getChildren().add(categoryLabel);
            });
    }

    private void generateProductivityTrendsReport() {
        Label content = new Label("Analyse des tendances de productivité:");
        content.setStyle("-fx-padding: 10; -fx-font-weight: bold;");
        reportContainer.getChildren().add(content);
        
        // Calculer quelques statistiques de tendance
        double avgWeekday = dailyTimeData.entrySet().stream()
            .filter(entry -> entry.getKey().getDayOfWeek().getValue() < 6)
            .mapToDouble(Map.Entry::getValue)
            .average()
            .orElse(0.0);
            
        double avgWeekend = dailyTimeData.entrySet().stream()
            .filter(entry -> entry.getKey().getDayOfWeek().getValue() >= 6)
            .mapToDouble(Map.Entry::getValue)
            .average()
            .orElse(0.0);
        
        Label weekdayLabel = new Label("Moyenne en semaine: " + DECIMAL_FORMAT.format(avgWeekday) + " heures");
        Label weekendLabel = new Label("Moyenne le weekend: " + DECIMAL_FORMAT.format(avgWeekend) + " heures");
        
        weekdayLabel.setStyle("-fx-padding: 5;");
        weekendLabel.setStyle("-fx-padding: 5;");
        
        reportContainer.getChildren().addAll(weekdayLabel, weekendLabel);
        
        // Trend analysis
        LocalDate today = LocalDate.now();
        double lastWeekAvg = dailyTimeData.entrySet().stream()
            .filter(entry -> !entry.getKey().isBefore(today.minusDays(7)) && entry.getKey().isBefore(today))
            .mapToDouble(Map.Entry::getValue)
            .average()
            .orElse(0.0);
            
        double previousWeekAvg = dailyTimeData.entrySet().stream()
            .filter(entry -> ! entry.getKey().isBefore(today.minusDays(14)) && entry.getKey().isBefore(today.minusDays(7)))
            .mapToDouble(Map.Entry::getValue)
            .average()
            .orElse(0.0);
            
        if (previousWeekAvg > 0) {
            double change = ((lastWeekAvg - previousWeekAvg) / previousWeekAvg) * 100;
            String trend = change > 0 ? "↗ Augmentation" : "↘ Diminution";
            Label trendLabel = new Label("Tendance: " + trend + " de " + 
                DECIMAL_FORMAT.format(Math.abs(change)) + "%");
            trendLabel.setStyle("-fx-padding: 5; -fx-text-fill: " + (change > 0 ? "green" : "red") + ";");
            reportContainer.getChildren().add(trendLabel);
        }
    }

    private void generateTaskCompletionReport() {
        Label content = new Label("Rapport des tâches accomplies: " + totalTasksCompleted);
        content.setStyle("-fx-padding: 10; -fx-font-weight: bold;");
        reportContainer.getChildren().add(content);
        
        // Ajouter quelques statistiques supplémentaires
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        
        if (startDate != null && endDate != null) {
            long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
            double tasksPerDay = days > 0 ? (double) totalTasksCompleted / days : 0;
            
            Label rateLabel = new Label("Taux moyen: " + DECIMAL_FORMAT.format(tasksPerDay) + " tâches par jour");
            rateLabel.setStyle("-fx-padding: 5;");
            reportContainer.getChildren().add(rateLabel);
            
            double totalHours = dailyTimeData.entrySet().stream()
                .filter(entry -> !entry.getKey().isBefore(startDate) && !entry.getKey().isAfter(endDate))
                .mapToDouble(Map.Entry::getValue)
                .sum();
                
            if (totalHours > 0) {
                double tasksPerHour = totalTasksCompleted / totalHours;
                Label efficiencyLabel = new Label("Efficacité: " + 
                    String.format(Locale.US, "%.2f", tasksPerHour) + " tâches par heure");
                efficiencyLabel.setStyle("-fx-padding: 5;");
                reportContainer.getChildren().add(efficiencyLabel);
            }
        }
    }
}