package Agent;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AgentGUI extends Application{
    private Agent agent;
    private BorderPane chooseAuction;
    private BorderPane placeBid;

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Agent");

        Image mainStreet = new Image("file:Resources\\CartoonMainStreet.jpg");
        BackgroundFill imageSet = new BackgroundFill(new
                ImagePattern(mainStreet), CornerRadii.EMPTY, Insets.EMPTY);
        chooseAuction = new BorderPane();
        chooseAuction.setBackground(new Background(imageSet));

        Image frame = new Image("file:Resources\\GoldPictureFrame.jpg");
        BackgroundFill pictureFrame=new BackgroundFill(new ImagePattern(frame),
                CornerRadii.EMPTY, Insets.EMPTY);
        placeBid = new BorderPane();
        placeBid.setBackground(new Background(pictureFrame));

        Stage loginStage = new Stage();
        Scene loginScene;
        VBox loginVBox = new VBox(5);
        HBox loginNameBox = new HBox(5);
        HBox loginBalBox = new HBox(5);
        HBox loginIPBox = new HBox(5);
        HBox loginPortBox = new HBox(5);
        Label loginNameLabel = new Label("Username:");
        TextField loginNameField = new TextField();
        loginNameBox.getChildren().addAll(loginNameLabel, loginNameField);
        loginNameBox.setAlignment(Pos.CENTER);
        Label loginBalLabel = new Label("Initial Balance:");
        TextField loginBalField = new TextField();
        loginBalBox.getChildren().addAll(loginBalLabel, loginBalField);
        loginBalBox.setAlignment(Pos.CENTER);
        Label loginIPLabel = new Label("Bank IP:");
        TextField loginIPField = new TextField();
        loginIPBox.getChildren().addAll(loginIPLabel, loginIPField);
        loginIPBox.setAlignment(Pos.CENTER);
        Label loginPortLabel = new Label("Bank Port:");
        TextField loginPortField = new TextField();
        loginPortBox.getChildren().addAll(loginPortLabel, loginPortField);
        loginPortBox.setAlignment(Pos.CENTER);
        Button loginButton = new Button("Log In");
        loginVBox.getChildren().addAll(loginNameBox, loginBalBox, loginIPBox,
                loginPortBox, loginButton);
        loginVBox.setAlignment(Pos.CENTER);
        loginStage.initModality(Modality.APPLICATION_MODAL);
        loginStage.initOwner(primaryStage);
        loginStage.setAlwaysOnTop(true);
        loginStage.setTitle("Log In");
        loginScene = new Scene(loginVBox, 250, 180);
        loginStage.setScene(loginScene);
        loginStage.show();

        Scene scene = new Scene(chooseAuction, 612, 403);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
