package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import main.backend.Home;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Auction House");

        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("Auction/resources/a.fxml"));

        GridPane root = loader.load();

        Scene scene = new Scene(root);

        Home home = loader.getController();
        home.setScene(scene);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
