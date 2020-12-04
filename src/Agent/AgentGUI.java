package Agent;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class AgentGUI extends Application{
    private Scene scene;
    private Agent agent;
    private BorderPane chooseAuction;
    private BorderPane placeBid;
    private ScrollPane messageLog;
    private Button checkBalance;
    private VBox placeBidButtons;
    private boolean connected;

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Agent");

        connected = false;

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

        Label pickAuctionLabel = new Label("Pick an Auction House");
        pickAuctionLabel.setFont(new Font(30));
        chooseAuction.setTop(pickAuctionLabel);
        BorderPane.setAlignment(pickAuctionLabel, Pos.CENTER);

        messageLog = new ScrollPane();
        messageLog.setPrefViewportWidth(180);
        chooseAuction.setRight(messageLog);

        checkBalance = new Button("Check Balance");
        checkBalance.setFont(new Font(15));
        chooseAuction.setLeft(checkBalance);

        TextField depositFunds = new TextField();
        depositFunds.setFont(new Font(18));
        Button deposit = new Button("Deposit");
        deposit.setFont(new Font(18));
        HBox chooseAuctionBox = new HBox(10, depositFunds, deposit);
        chooseAuctionBox.setAlignment(Pos.CENTER);
        chooseAuction.setBottom(chooseAuctionBox);
        BorderPane.setAlignment(chooseAuctionBox, Pos.CENTER);

        Label placeBidLabel = new Label("Bid on An Item");
        placeBidLabel.setFont(new Font(30));
        placeBidLabel.setTextFill(Color.WHITE);
        placeBid.setTop(placeBidLabel);
        BorderPane.setAlignment(placeBidLabel, Pos.CENTER);

        Button backButton = new Button("Back");
        backButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            scene.setRoot(chooseAuction);
            chooseAuction.setLeft(checkBalance);
        });
        backButton.setFont(new Font(15));
        placeBidButtons = new VBox(10, backButton);
        placeBid.setLeft(placeBidButtons);
        BorderPane.setAlignment(placeBidButtons, Pos.TOP_LEFT);

        TextField enterBid = new TextField();
        enterBid.setFont(new Font(18));
        Button bid = new Button("Place Bid");
        bid.setFont(new Font(18));
        HBox placeBidBox = new HBox(10, enterBid, bid);
        placeBidBox.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(placeBidBox, Pos.CENTER);
        placeBid.setBottom(placeBidBox);

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
        Button loginButton = new Button("Create Account");
        loginVBox.getChildren().addAll(loginNameBox, loginBalBox, loginIPBox,
                loginPortBox, loginButton);
        loginVBox.setAlignment(Pos.CENTER);
        loginStage.initModality(Modality.APPLICATION_MODAL);
        loginStage.initOwner(primaryStage);
        loginStage.setAlwaysOnTop(true);
        loginStage.setTitle("Log In");
        loginButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            String user = loginNameField.getText();
            String host = loginIPField.getText();
            String port = loginPortField.getText();
            String login = user + ":n:"
                    + loginBalField.getText() + ":0";
            try{
                agent = new Agent(user, host, port, login);
                loginStage.close();
                agent.runBank();
            } catch(Exception e){
                System.out.println("Connection failed. Try again.");
            }
        });

        loginScene = new Scene(loginVBox, 250, 180);
        loginStage.setScene(loginScene);
        loginStage.show();

        scene = new Scene(chooseAuction, 762, 553);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private FlowPane updateAvailableAuctions(){
        FlowPane flow = new FlowPane(20, 10);
        Button auction;
        List<String> auctions = agent.getAuctionNames();
        for(String a : auctions){
            auction = new Button(a);
            flow.getChildren().add(auction);
        }
        flow.setAlignment(Pos.CENTER);
        return flow;
    }
}
