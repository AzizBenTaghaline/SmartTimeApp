package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HomePage extends Application {

    private static Stage primaryStage;
    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        showFXML();   
    }

    public static void showFXML() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                HomePage.class.getResource("/smarttimeapp2/view/FXML.fxml")
        );
        Scene scene = new Scene(loader.load ());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}