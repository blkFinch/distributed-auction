/**
 * Ryan Cooper
 * rycooper
 */
package Auction;

import Auction.backend.AuctionGUI;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Auction House");

        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("Auction/resources/auctionGUI.fxml"));

        AnchorPane root = loader.load();

        Scene scene = new Scene(root);

        AuctionGUI auctionGUI = loader.getController();
        auctionGUI.setScene(scene);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
