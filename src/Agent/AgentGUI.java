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
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import shared.A_AH_Messages;
import shared.Items.Item;
import shared.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/******************************************************************************
 * Ashley Krattiger                                                           *
 *                                                                            *
 * AgentGUI                                                                   *
 *                                                                            *
 * Runs the GUI for the Agent. Sends information from the user to the Agent   *
 *****************************************************************************/
public class AgentGUI extends Application{
    /**************************************************************************
     * Global Variables:                                                      *
     *                                                                        *
     * scene - Scene for the primaryStage                                     *
     * agent - Agent for this session of the Agent program                    *
     * username - String associated with the Agent's account                  *
     * userID - int associated with the Agent's account                       *
     * chooseAuction - BorderPane for the screen where you choose which       *
     *                 Auction to enter                                       *
     * placeBid - BorderPane for the screen where you are inside a specific   *
     *            Auction and can place a bid                                 *
     * messageLog - ScrollPane holding the messages received from the servers *
     * messageList - VBox holding each message displayed on the messageLog    *
     * checkBalance - Button that checks the Bank balance of the Agent        *
     * placeBidButtons - VBox that holds the Buttons displayed on the left of *
     *                   the placeBid screen                                  *
     * createScene - Scene for the createAccount screen for the Open          *
     *               Connection Popup                                         *
     * loginScene - Scene for the logIn screen for the Open Connection Popup  *
     * itemList - FlowPane that holds the Item visuals on the placeBid screen *
     * a - AnimationTimer for the GUI                                         *
     * host - Bank's IP address                                               *
     * port - Bank's open port number                                         *
     * connected - boolean that's true if the Agent is connected to the Bank  *
     * bidScreen - boolean that's true if the placeBid screen is visible      *
     * chosenItem - name of the Item the user has selected on the placeBid    *
     *              screen                                                    *
     * chosenItemID - itemID associated with the chosenItem                   *
     *************************************************************************/
    private Scene scene;
    private Agent agent;
    private String username;
    private int userID;
    private BorderPane chooseAuction;
    private BorderPane placeBid;
    private ScrollPane messageLog;
    private VBox messageList;
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
    private String chosenItem;
    private int chosenItemID;

    /**************************************************************************
     * start                                                                  *
     * Overridden method from Application                                     *
     *                                                                        *
     * Sets up GUI and all necessary nodes and processes. Also initializes 3  *
     * Scenes for the Open Connection Pop up Window                           *
     *                                                                        *
     * @param primaryStage - Stage for the GUI                                *
     * @throws Exception - overridden                                         *
     *************************************************************************/
    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Agent");

        userID = -1;
        connected = false;
        bidScreen = false;
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
        messageList = new VBox(10);
        messageLog.setContent(messageList);
        chooseAuction.setRight(messageLog);

        checkBalance = new Button("Check Balance");
        checkBalance.setFont(new Font(15));
        checkBalance.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            Message checkBal = new Message.Builder()
                    .command(Message.Command.GETBALANCE)
                    .accountId(userID)
                    .nullId();
            agent.sendBankMessage(checkBal);
        });
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
                        .topic(A_AH_Messages.A_AH_MTopic.BID)
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
        createAccountButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->{
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
        createVBox.getChildren().addAll(createNameBox, createBalBox,
                createButton);
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

        a = new AnimationTimer(){
            @Override
            public void handle(long now){
                if(userID == -1 && agent.getUserID() != -1){
                    userID = agent.getUserID();
                }
                if(username.equals("")){ username = agent.getUsername(); }
                if(connected && (now%10000) == 0){
                    agent.handleMessages();
                    updateMessageLog();
                    if(bidScreen){placeBid.setCenter(updateAvailableItems()); }
                    else{ chooseAuction.setCenter(updateAvailableAuctions()); }
                }
            }
        };
    }

    /**************************************************************************
     * updateAvailableAuctions                                                *
     *                                                                        *
     * Updates the list of Buttons that connect you to open Auctions          *
     *                                                                        *
     * Takes no arguments                                                     *
     * @return new FlowPane that holds the AuctionHouse Buttons               *
     *                                                                        *
     * Variables:                                                             *
     * flow - FlowPane to hold the new list of AuctionHouse Buttons           *
     * auction - temporary Button that holds each Button while it's being made*
     * auctions - Set of auction names given by Agent                         *
     * buttonFont - Font for the Buttons                                      *
     * updateAuctions - Message that requests updates on which AuctionHouses  *
     *                  are open                                              *
     * auctionName - final version of the Auction's name for use in event     *
     *               handler                                                  *
     *************************************************************************/
    private FlowPane updateAvailableAuctions(){
        FlowPane flow = new FlowPane(20, 10);
        Button auction;
        Set<String> auctions;
        Font buttonFont = new Font(20);
        Message updateAuctions = new Message.Builder()
                .command(Message.Command.GETHOUSES)
                .nullId();
        agent.sendBankMessage(updateAuctions);
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
                bidScreen = true;
                scene.setRoot(placeBid);
            });
            flow.getChildren().add(auction);
        }
        flow.setAlignment(Pos.CENTER);
        return flow;
    }

    /**************************************************************************
     * updateAvailableItems                                                   *
     *                                                                        *
     * Updates the list of Item visualizations on the placeBid screen         *
     *                                                                        *
     * Takes no arguments                                                     *
     * @return FlowPane holding new visualizations                            *
     *                                                                        *
     * Variables:                                                             *
     * currentItems - ArrayList of current Items up for auction               *
     * border - BorderPane for each visualization while it is being made      *
     * itemName - Label for each visualization. Holds name of the item        *
     * canvas - Canvas for each visualization                                 *
     * currBid - Label for each visualization. Holds the current bid on the   *
     *           item                                                         *
     * gc - GraphicsContext of each canvas                                    *
     * picture - Image for each visualization                                 *
     * itemFileName - String that holds the image in Resources to be used in  *
     *                Image picture                                           *
     * index - index of each visualization in the FlowPane                    *
     *************************************************************************/
    private FlowPane updateAvailableItems(){
        ArrayList<Item> currentItems = agent.getCurrentItems();
        BorderPane border;
        Label itemName;
        Canvas canvas;
        Label currBid;
        GraphicsContext gc;
        Image picture;
        String itemFileName;
        int index = 0;
        itemList = new FlowPane(20, 10);
        if(currentItems != null) {
            for (Item item : currentItems) {
                itemName = new Label(item.getName());
                itemName.setTextFill(Color.WHITE);

                itemFileName = item.getName()+".png";
                canvas = new Canvas(200, 200);
                picture = new Image("file:Resources\\Items\\"+itemFileName);
                gc = canvas.getGraphicsContext2D();
                gc.setFill(Color.WHITE);
                gc.fillRect(0, 0, 200, 200);
                gc.drawImage(picture, 0, 0);
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
                    BorderPane bord = (BorderPane) itemList
                            .getChildren().get(ind);
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
                        pic = new Image("file:Resources\\Items\\"+itemFileFinal);
                        graph.setFill(Color.WHITE);
                        graph.fillRect(0, 0, 200, 200);
                        graph.drawImage(pic, 0, 0);
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

    /**************************************************************************
     * updateMessageLog                                                       *
     *                                                                        *
     * Updates the messageLog to have the most recent Messages                *
     *                                                                        *
     * Takes no arguments, returns nothing                                    *
     *                                                                        *
     * Variables:                                                             *
     * newMessages - List of new messages from the servers                    *
     * split - String[] that holds a message split by commas                  *
     * compiledMessage - holds the final message that will be added to        *
     *                   messageLog                                           *
     *************************************************************************/
    private void updateMessageLog(){
        List<String> newMessages = agent.getMessageList();
        String[] split;
        String compiledMessage;
        for(String mes : newMessages){
            compiledMessage = "";
            split = mes.split(",");
            for(String segment : split){
                compiledMessage += segment;
                compiledMessage += ",\n";
            }
            messageList.getChildren().add(new Text(compiledMessage));
        }
    }

    /**************************************************************************
     * stop                                                                   *
     * Overridden method from Application                                     *
     *                                                                        *
     * Closes communications properly and prints won items when the GUI is    *
     * closed                                                                 *
     *************************************************************************/
    @Override
    public void stop(){
        connected = false;
        agent.closeConnections();
        agent.printItemsWon();
        System.exit(0);
    }
}
