package Agent;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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
import shared.A_AH_Messages;
import shared.Items.Item;
import shared.Message;

import java.util.ArrayList;
import java.util.Set;

public class AgentGUI extends Application{
    private Scene scene;
    private Agent agent;
    private String username;
    private int userID;
    private BorderPane chooseAuction;
    private BorderPane placeBid;
    private ScrollPane messageLog;
    private Button checkBalance;
    private VBox placeBidButtons;
    private Scene createScene;
    private Scene loginScene;
    private FlowPane itemList;
    private AnimationTimer a;
    private String host;
    private String port;
    private boolean connected;
    private boolean bidScreen;
    private String currAuction;
    private String chosenItem;
    private int chosenItemID;

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Agent");

        userID = -1;
        connected = false;
        bidScreen = false;
        currAuction = "";
        chosenItem = null;
        chosenItemID = 0;

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
        deposit.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            int dep;
            Message depositMessage;
            if(!depositFunds.getText().equals("")){
                dep = Integer.parseInt(depositFunds.getText());
                depositMessage = new Message.Builder()
                        .command(Message.Command.DEPOSIT)
                        .balance(dep)
                        .accountId(userID)
                        .nullId();
                agent.sendBankMessage(depositMessage);
            }
        });
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
            chooseAuction.setRight(messageLog);
            currAuction = "";
            bidScreen = false;
        });
        backButton.setFont(new Font(15));
        placeBidButtons = new VBox(10, backButton);
        placeBid.setLeft(placeBidButtons);
        BorderPane.setAlignment(placeBidButtons, Pos.TOP_LEFT);

        itemList = new FlowPane();
        placeBid.setCenter(itemList);
        BorderPane.setAlignment(itemList, Pos.CENTER);

        TextField enterBid = new TextField();
        enterBid.setFont(new Font(18));
        Button bid = new Button("Place Bid");
        bid.setFont(new Font(18));
        bid.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            int bidAmount;
            A_AH_Messages bidMessage;
            if(chosenItem != null){
                bidAmount = Integer.parseInt(enterBid.getText());
                bidMessage = new A_AH_Messages.Builder()
                        .itemId(chosenItemID)
                        .accountId(userID)
                        .name(chosenItem)
                        .bid(bidAmount)
                        .build();
                agent.sendAuctionMessage(bidMessage);
            }
        });
        HBox placeBidBox = new HBox(10, enterBid, bid);
        placeBidBox.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(placeBidBox, Pos.CENTER);
        placeBid.setBottom(placeBidBox);

        Stage openStage = new Stage();
        Scene openScene;
        VBox openVBox = new VBox(5);
        HBox openIPBox = new HBox(5);
        HBox openPortBox = new HBox(5);
        HBox openButtonBox = new HBox(5);
        Label openIPLabel = new Label("Bank IP:");
        TextField openIPField = new TextField();
        openIPBox.getChildren().addAll(openIPLabel, openIPField);
        openIPBox.setAlignment(Pos.CENTER);
        Label openPortLabel = new Label("Bank Port:");
        TextField openPortField = new TextField();
        openPortBox.getChildren().addAll(openPortLabel, openPortField);
        openPortBox.setAlignment(Pos.CENTER);
        Button createAccountButton = new Button("Create Account");
        createAccountButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            host = openIPField.getText();
            port = openPortField.getText();
            openStage.setScene(createScene);
        });
        Button logInButton = new Button("Log In");
        logInButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            host = openIPField.getText();
            port = openPortField.getText();
            openStage.setScene(loginScene);
        });
        openButtonBox.getChildren().addAll(createAccountButton, logInButton);
        openButtonBox.setAlignment(Pos.CENTER);
        openVBox.getChildren().addAll(openIPBox, openPortBox, openButtonBox);
        openVBox.setAlignment(Pos.CENTER);
        openStage.initModality(Modality.APPLICATION_MODAL);
        openStage.initOwner(primaryStage);
        openStage.setAlwaysOnTop(true);
        openStage.setTitle("Open Connection");
        openScene = new Scene(openVBox, 250, 180);
        openStage.setScene(openScene);
        openStage.show();

        VBox createVBox = new VBox(5);
        HBox createNameBox = new HBox(5);
        HBox createBalBox = new HBox(5);
        Label createNameLabel = new Label("Username:");
        TextField createNameField = new TextField();
        createNameBox.getChildren().addAll(createNameLabel, createNameField);
        createNameBox.setAlignment(Pos.CENTER);
        Label createBalLabel = new Label("Initial Balance:");
        TextField createBalField = new TextField();
        createBalBox.getChildren().addAll(createBalLabel, createBalField);
        createBalBox.setAlignment(Pos.CENTER);
        Button createButton = new Button("Create Account");
        createVBox.getChildren().addAll(createNameBox, createBalBox, createButton);
        createVBox.setAlignment(Pos.CENTER);
        openStage.setTitle("Create Account");
        createButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            String user = createNameField.getText();
            int initBal = Integer.parseInt(createBalField.getText());
            try{
                agent = new Agent(user, host, port, true, initBal);
                openStage.close();
                agent.runBank();
                connected = true;
                username = user;
                a.start();
            } catch(Exception e){
                System.out.println("Connection failed. Try again.");
            }
        });
        createScene = new Scene(createVBox, 250, 180);

        VBox loginVBox = new VBox(5);
        HBox loginNameBox = new HBox(5);
        Label loginNameLabel = new Label("User ID:");
        TextField loginNameField = new TextField();
        loginNameBox.getChildren().addAll(loginNameLabel, loginNameField);
        loginNameBox.setAlignment(Pos.CENTER);
        Button loginButton = new Button("Log In");
        loginVBox.getChildren().addAll(loginNameBox, loginButton);
        loginVBox.setAlignment(Pos.CENTER);
        openStage.setTitle("Log In");
        loginButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            String user = loginNameField.getText();
            try{
                agent = new Agent(user, host, port, false, 0);
                openStage.close();
                agent.runBank();
                connected = true;
                username = "";
                a.start();
            } catch(Exception e){
                System.out.println("Connection failed. Try again.");
                e.printStackTrace();
            }
        });
        loginScene = new Scene(loginVBox, 250, 180);

        scene = new Scene(chooseAuction, 762, 553);
        primaryStage.setScene(scene);
        primaryStage.show();

        a = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if(userID == -1 && agent.getUserID() != -1){
                    userID = agent.getUserID();
                }
                if(username.equals("")){ username = agent.getUsername(); }
                if(connected && (now%10000) == 0){
                    agent.handleMessages();
                    if(bidScreen){ placeBid.setCenter(updateAvailableItems()); }
                    else{ chooseAuction.setCenter(updateAvailableAuctions()); }
                }
            }
        };
    }

    private FlowPane updateAvailableAuctions(){
        FlowPane flow = new FlowPane(20, 10);
        Button auction;
        Set<String> auctions;
        Font buttonFont = new Font(20);
        agent.updateAuctionProxies();
        auctions = agent.getAuctionNames();
        for(String a : auctions){
            final String auctionName = a;
            auction = new Button(a);
            auction.setFont(buttonFont);
            auction.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                agent.setCurrentAuction(auctionName);
                placeBid.setRight(messageLog);
                placeBidButtons.getChildren().add(checkBalance);
                currAuction = auctionName;
                bidScreen = true;
                scene.setRoot(placeBid);
            });
            flow.getChildren().add(auction);
        }
        flow.setAlignment(Pos.CENTER);
        return flow;
    }

    private FlowPane updateAvailableItems(){
        ArrayList<Item> currentItems = agent.getCurrentItems();
        BorderPane border;
        Label itemName;
        Canvas canvas;
        Label currBid;
        GraphicsContext gc;
        Image picture;
        String itemFileName;
        String[] temp;
        int index = 0;
        itemList = new FlowPane(20, 10);
        if(currentItems != null) {
            for (Item item : currentItems) {
                itemName = new Label(item.getName());
                itemName.setTextFill(Color.WHITE);

                itemFileName = "";
                temp = item.getName().split("\\s+");
                for (String s : temp) {
                    itemFileName += s;
                }
                itemFileName += ".jpg";
                canvas = new Canvas(200, 200);
                //picture = new Image("file:Resources\\Items\\"+itemFileName);
                gc = canvas.getGraphicsContext2D();
                gc.setFill(Color.WHITE);
                gc.fillRect(0, 0, 200, 200);
                //gc.drawImage(picture, 0, 0);
                if (chosenItem != null && chosenItem.equals(item.getName())) {
                    gc.setStroke(Color.YELLOW);
                    gc.setLineWidth(7);
                    gc.strokeRect(0, 0, 200, 200);
                }
                final int ind = index;
                final String itemNameFinal = item.getName();
                final String itemFileFinal = itemFileName;
                final int itemID = item.getItemID();
                canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    BorderPane bord = (BorderPane) itemList.getChildren().get(ind);
                    Canvas can = (Canvas) bord.getCenter();
                    GraphicsContext graph = can.getGraphicsContext2D();
                    Image pic;
                    if (chosenItem == null) {
                        graph.setStroke(Color.YELLOW);
                        graph.setLineWidth(7);
                        graph.strokeRect(0, 0, 200, 200);
                        chosenItem = itemNameFinal;
                        chosenItemID = itemID;
                    } else if (chosenItem.equals(itemNameFinal)) {
                        //pic = new Image("file:Resources\\Items\\"+itemFileFinal);
                        graph.setFill(Color.WHITE);
                        graph.fillRect(0, 0, 200, 200);
                        //graph.drawImage(pic, 0, 0);
                        chosenItem = null;
                        chosenItemID = 0;
                    }
                });

                currBid = new Label("Bid: $" + item.getCurrentBid());
                currBid.setTextFill(Color.WHITE);

                border = new BorderPane();
                border.setTop(itemName);
                border.setCenter(canvas);
                border.setBottom(currBid);
                BorderPane.setAlignment(itemName, Pos.CENTER);
                BorderPane.setAlignment(canvas, Pos.CENTER);
                BorderPane.setAlignment(currBid, Pos.CENTER);
                itemList.getChildren().add(border);
                index++;
            }
        }
        itemList.setAlignment(Pos.CENTER);
        return itemList;
    }
}
