package Auction;

import shared.A_AH_Messages;
import shared.ConnectionReqs;
import shared.Message;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class AuctionHouse {
    private final  ArrayList<Item>        auctionList     = new ArrayList<>();
    private final  BlockingQueue<Boolean> check = new LinkedBlockingDeque<>();
    private static boolean                running         = true;
    private        double                 balance         = 0.0;
    private        int                    auctionId;
    private        int                    port;  //Auction server port number
    private final  List<Agent>            agentsList      = new LinkedList<>();
    private        ObjectInputStream      ioIn;
    private        ObjectOutputStream     ioOut;
    private        ServerSocket           auctionSocket   = null; //the socket connected to agents
    private        Socket                 bankSocket; //for bank
    private        String                 ip; //Auction server ip address

    /**
     * creates catalogue for items to sell
     */
    private void createAuctionList(int size) {
        itemSpecsList.createItemSpecsList();
        for (int i = 0; i < size; i++) {
            addSpecs(itemSpecsList.getRandomElement(), auctionId);
        }
    }

    /**
     * addItems completes items for the auction house
     *
     * @param itemSpecs ItemSpecs
     * @param auctionId String
     */
    private void addSpecs(ItemSpecs itemSpecs, int auctionId) {
        Item item = new Item(itemSpecs.name, itemSpecs.description,
                itemSpecs.minimumBid, auctionId);
        auctionList.add(item);
    }

    public AuctionHouse(String address, int bankPort, int auctionPort) throws IOException {
        createAuctionList(3);
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            ip = socket.getLocalAddress().getHostAddress();
        }
        port = auctionSocket.getLocalPort();

        bankSocket = new Socket();
        bankSocket.connect(new InetSocketAddress(address, bankPort),
                3000);
        ioOut = new ObjectOutputStream(bankSocket.getOutputStream());
        ConnectionReqs bankReqs = new ConnectionReqs(ip, port);
        List<ConnectionReqs> ahInfo = new LinkedList<>();
        ahInfo.add(bankReqs);
        Message register = new Message.Builder().command(
                Message.Command.REGISTERHOUSE)
                .connectionReqs(ahInfo).senderId(-1);
        sendToBank(register);
        Thread inThread = new Thread(new AuctionIn());
        inThread.start();

        auctionSocket = new ServerSocket(auctionPort);
        Thread auctionThread = new Thread(new AHServer());
        auctionThread.start();
    }

    private class AuctionIn implements Runnable {
        /**
         * waits for messages.
         */
        @Override
        public void run() {
            try {
                ioIn = new ObjectInputStream(bankSocket.getInputStream());
                while(running){
                    Message message = (Message) ioIn.readObject();
                    Message.Command topic = message.getCommand();
                    switch(topic){
                        case LOGIN:
                            login(message);
                            break;
                        case REGISTERHOUSE:
                            registerHouse(message);
                            break;
                        case GETHOUSES:
                            getHouses(message);
                            break;
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch(IOException e){
                try {
                    ioIn.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        private void getHouses(Message message) {
        }

        private void registerHouse(Message message) {

        }

        private void login(Message message) {
        }

        private void openAccount(String message) {

        }

        /**
         * updates the balance variable to the bank's balance
         *
         * @param message Message
         */
        private void bankBalance(Message message) {
            balance = message.getBalance();
        }

        private void released(String message) {

        }

        private void registered(String message) {

        }
    }

    private class AHServer implements Runnable {
        @Override
        public void run() {
            try {
                while(running) {
                    Socket clientSocket  = auctionSocket.accept();
                    Agent  newAgent      = new Agent(clientSocket);
                    agentsList.add(newAgent);
                }
            } catch (IOException e) {
                running = false;
            }
        }
    }

    /**
     * auctionListMaintenance maintains list size and starts the process of
     * either removing or transferring ownership if bought.
     */
    private class auctionListMaintenance implements Runnable{
        @Override
        public void run() {
            while(running) {
                try {
                    int emptySlots =  3 - auctionList.size();
                    if(emptySlots > 0){
                        createAuctionList(emptySlots);
                    }
                    for(int i = 0;i < auctionList.size(); i++) {
                        Item item = auctionList.get(i);
                        item.remainingTime(System.currentTimeMillis());
                        if(item.getRemainingTime() <= 0){
                            timeExpired(item);
                        }
                    }
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * This class is dedicated to communicating/processing messages from/with
     * agents.
     * Each instance of this class represents communication with one agent.
     */
    private class Agent implements Runnable {
        private final Socket            agentsSocket; //the socket connected to an agent
        private       ObjectInputStream agentIn; //Input stream for agentSocket
        private       ObjectOutputStream agentOut;//output stream for agentSocket
        private       int                    agentId; //The int of the agent when it registers
        private A_AH_Messages message = null; //The message read from agentIn
        private final BlockingQueue<Boolean> bankSignOff;
        //Blocking queue for waiting on a HOLD response from the bank

        /**
         * Constructor for an AgentReqs. Takes socket from AuctionHouseServer,
         * opens in and out streams for it and begins communication.
         *
         * @param socket the accepted socket from the server variable
         */
        public Agent(Socket socket) {
            this.agentsSocket = socket;
            bankSignOff = new LinkedBlockingDeque<>();
            try {
                agentIn = new ObjectInputStream(agentsSocket.getInputStream());
                agentOut = new ObjectOutputStream(
                        agentsSocket.getOutputStream());
                Thread client = new Thread(this);
                client.start();
            } catch(IOException e) {
                agentsList.remove(this);
            }
        }

        /**
         * The run method is dedicating to reading message from an agent.
         * The method also adds the incoming message to the log
         */
        @Override
        public void run() {
            do {
                try{
                    message = (A_AH_Messages) agentIn.readObject();
                    A_AH_Messages.A_AH_MTopic topic = message.getTopic();
                    if(topic != A_AH_Messages.A_AH_MTopic.UPDATE) {
                        System.err.println("From a client: " + message);
                    }
                    switch(topic) {
                        case BID:
                            bid(message);
                            break;
                        case REGISTER:
                            register(message);
                            break;
                        case UPDATE:
                            update();
                            break;
                        case DEREGISTER:
                            agentShutdown(false);
                            break;
                    }
                } catch (IOException|ClassNotFoundException e) {
                    agentShutdown(false);
                    message = null;
                }
            } while(message != null && running);
        }

        /**
         *This method either gracefully or forcefully closes the AgentProxy's
         * socket, streams, and threads.A graceful shutdown means messaging
         * agents to let them know the auction house is shutting down. A
         * forceful shutdown means either to shutdown from a DEREGISTER message
         * or shutdown due to Exceptions
         * @param reason True if a graceful shutdown, false if for
         *               error handling/DEREGISTER message
         */
        private void agentShutdown(Boolean reason) {
            message = null;
            agentsList.remove(this);
            try{
                if(reason){
                    A_AH_Messages shutdown = A_AH_Messages.Builder.newBuilder()
                            .topic(A_AH_Messages.A_AH_MTopic.DEREGISTER)
                            .build();
                    sendOut(shutdown);
                    if(!agentsSocket.isClosed()){
                        agentOut.close();
                        agentIn.close();
                        agentsSocket.close();
                    }
                }else{
                    if(!agentsSocket.isClosed()){
                        agentOut.close();
                        agentIn.close();
                        agentsSocket.close();
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        /**
         * This method is a switch to redirect incoming messages from agents
         * to the appropriate method for processing.
         * @param message The message sent from an agent
         */
        private void process(A_AH_Messages message){
            A_AH_Messages.A_AH_MTopic type = message.getTopic();
            switch(type){
                case BID:
                    bid(message);
                    break;
                case REGISTER:
                    register(message);
                    break;
                case UPDATE:
                    update();
                    break;
                case DEREGISTER:
                    agentShutdown(false);
                    break;
            }
        }

        /**
         * This method creates the message with the updated catalogue
         * and passes it to sendOut to send to the agent
         */
        private void update(){
            A_AH_Messages update = A_AH_Messages.Builder.newBuilder()
                    .topic(A_AH_Messages.A_AH_MTopic.UPDATE)
                    .accountId(auctionId)
                    .auctionList(auctionList)
                    .build();
            sendOut(update);
        }

        /**
         * This method is letting the connected agent know they successfully
         * registered and sends the catalogue of items for sale. The method
         * also stores the agent's int for future reference
         * @param message The register message the agent sent
         */
        private void register(A_AH_Messages message) {
            agentId = message.getAccountId();
            A_AH_Messages reply = A_AH_Messages.Builder.newBuilder()
                    .topic(A_AH_Messages.A_AH_MTopic.REGISTER)
                    .accountId(auctionId).auctionList(auctionList).build();
            sendOut(reply);
        }

        /**
         * This replaces the new bidder/amount with the old bidder/amount and
         * lets the old bidder know they were outbided
         * @param oldBidder int of the last bidder
         * @param item The item that has a new bidder
         */
        private void outBid(int oldBidder, Item item){
            Agent agent = agentSearch(oldBidder);
            A_AH_Messages outbid = A_AH_Messages.Builder.newBuilder()
                    .topic(A_AH_Messages.A_AH_MTopic.OUTBID)
                    .itemId(item.getItemID())
                    .name(item.getName())
                    .accountId(agentId)
                    .build();
            assert agent != null;
            agent.sendOut(outbid);
        }

        /**
         * The item's auction has ended and there's a bidder. This method
         * lets the bidder know they won.
         *
         * .
         * @param item The item the bidder won
         */
        private void winner(Item item){
            A_AH_Messages winner = A_AH_Messages.Builder.newBuilder()
                    .topic(A_AH_Messages.A_AH_MTopic.WINNER)
                    .accountId(agentId)
                    .itemId(item.getItemID())
                    .name(item.getName())
                    .bid(item.getCurrentBid())
                    .build();
            sendOut(winner);
        }

        /**
         * This method grabs the itemID, bidderId, name, and amount of the
         * item to bid on. First it checks if the item is still for sale, then
         * checks if the bid amount is above the minimum/current bid. Then it
         * requests the bank to hold the bidded funds and waits for a response.
         * After receiving the response, it then decides whether to reject or
         * accept the bid.
         * @param message The message with AMType BID
         */
        private void bid(A_AH_Messages message){
            int    itemId   = message.getItem();
            int    bidderId = message.getAccountId();
            String name     = message.getItemName();
            double amount   = message.getBid();
            Item   bidItem  = itemSearch(itemId);
            if(bidItem == null){
                reject(itemId,name);
                return;
            }
            double value = bidItem.getCurrentBid();
            if( value < bidItem.getMinimumBid()){
                value = bidItem.getMinimumBid();
            }
            if(amount > value){
                Message requestHold = new Message.Builder()
                        .command(Message.Command.BLOCK)
                        .accountId(bidderId).cost(amount).senderId(this.agentId);
                try{
                    //requests the hold.
                    sendToBank(requestHold);
                    //waits for the response from bank.
                    Boolean success = bankSignOff.take();
                    if(success){
                        //accepts bid and lets the last bidder know
                        //they were outbidded
                        int oldBidder = bidItem.getBidderId();
                        if(oldBidder != -1) {
                            release(oldBidder, value);
                            outBid(oldBidder, bidItem);
                        }
                        bidItem.setBid(bidderId, amount);
                        accept(bidItem.getItemID(), bidItem.getName());
                    }else{
                        reject(itemId,name);
                    }
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }else{
                reject(itemId,name);
            }
        }

        /**
         * requests the bank to release the hold of (amount) amount on account
         * id
         * @param id the account(bidder) having their funds released
         * @param amount the amount requested to release
         */
        private synchronized void release(int id, Double amount){
            Message release = new Message.Builder()
                    .command(Message.Command.UNBLOCK)
                    .accountId(id).cost(amount).senderId(auctionId);
            sendToBank(release);
        }

        /**
         * Lets the bidder know its bid was rejected due to various reasons
         * (not enough funds, bid not high enough, etc.)
         * @param itemId The int of the item bid on
         * @param name the name of the item
         */
        private void reject(int itemId, String name) {
            A_AH_Messages reject = A_AH_Messages.Builder.newBuilder()
                    .topic(A_AH_Messages.A_AH_MTopic.OVERDRAFT)
                    .name(name)
                    .itemId(itemId)
                    .build();
            sendOut(reject);
        }

        /**
         * Once a bid by an Agent is accepted, this method lets the agent
         * know their bid was accepted. The message also contains the updated
         * catalogue
         * @param itemId int of the item bid on
         * @param itemName String/name of the item bid on
         */
        private void accept(int itemId, String itemName) {
            A_AH_Messages accept = A_AH_Messages.Builder.newBuilder()
                    .topic(A_AH_Messages.A_AH_MTopic.SUCCESS)
                    .itemId(itemId)
                    .name(itemName)
                    .auctionList(auctionList)
                    .build();
            sendOut(accept);
        }

        /**
         * This method is given an AuctionMessage and writes/sends it to
         * agentSocket. The method add the sent message to the log.
         * @param message the message being sent
         */
        private void sendOut(A_AH_Messages message){
            try{
                if(message.getTopic() != A_AH_Messages.A_AH_MTopic.UPDATE){
                    System.err.println("To Agent: " + message);
                }
                agentOut.reset();
                agentOut.writeObject(message);
            }catch(IOException e){
                agentShutdown(false);
            }
        }
    }

    /**
     * searches for agent in active agent list
     * @param id id of agent we want
     * @return returns the agentProxy we want, null otherwise
     */
    private Agent agentSearch(int id) {
        for(Agent agent: agentsList) {
            if(agent.agentId == id) {
                return agent;
            }
        }
        return null;
    }

    /**
     * After the time expires on an Item for sale. This method checks if
     * there was any bidders and sends the WINNER message to that bidder.
     * Also releases the hold on the bidders amount.
     * @param item the item being checked
     */
    private void timeExpired(Item item){
        int bidder = item.getBidderId();
        Agent agent = agentSearch(bidder);
        if (agent != null) {
            agent.winner(item);
            Message release = new Message.Builder()
                    .command(Message.Command.UNBLOCK)
                    .cost(item.getCurrentBid())
                    .accountId(bidder).senderId(auctionId);
            sendToBank(release);
        }
        auctionList.remove(item);
    }

    /**
     * itemSearch searches the auctionList for an item of matching id
     *
     * @param itemId the int of the item being searched
     * @return returns the item searched, or null if item isn't found
     */
    private Item itemSearch(int itemId) {
        for(Item item: auctionList) {
            if(item.getItemID() == itemId) {
                return item;
            }
        }
        return null;
    }

    /**
     * Sends the given message to the bank and adds it to the log for display.
     * Looped messages (GET_AVAILABLE) are ignored when adding to log.
     * @param message message being sent to the bank.
     */
    private synchronized void sendToBank(Message message) {
        try {
            Message.Command temp = message.getCommand();
            if(temp != Message.Command.GETBALANCE){
                System.err.println("Bank: " + message);
            }
            ioOut.reset();
            ioOut.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getBankBalance(){
        Message getAvailable = new Message
                .Builder()
                .command(Message.Command.GETBALANCE)
                .senderId(auctionId);
        sendToBank(getAvailable);
    }

    /**
     * @return returns the catalogue
     */
    public ArrayList<Item> getAuctionList() {
        return auctionList;
    }

    /**
     * getBalance returns ballance
     * @return balance double
     */
    public double getBalance() {
        return balance;
    }

    /**
     * getAuctionId returns auctionId
     */
    int getAuctionId() {
        return auctionId;
    }
}
