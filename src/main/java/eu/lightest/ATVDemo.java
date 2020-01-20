package eu.lightest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ATVDemo extends Application {
    
    public static void main(String[] args) {
        Application.launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/atv.fxml"));
        primaryStage.setTitle("LIGHTest Automatic Trust Verifier");
        primaryStage.setScene(new Scene(root));
        // primaryStage.setScene(new Scene(root, 590, 810));
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.getIcons().add(new Image("/LIGHTestLogo.png"));
    }
}
