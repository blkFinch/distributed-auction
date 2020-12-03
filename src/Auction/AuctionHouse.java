package Auction;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class AuctionHouse {
    private static String auctionId;
    private ServerSocket server; //for agent
    private Socket auctionBank; //for bank
    private ObjectInputStream ioIn;
    private ObjectOutputStream ioOut;
    private static ArrayList<Item> auctionList = new ArrayList<>();
    private final List<String> agentsList = new LinkedList<>();
    private boolean running = true;
    private double balance = 0.0;

    /**
     * addItems completes items for the auction house
     * @param itemSpecs ItemSpecs
     * @param auctionId String
     */
    private static void addSpecs(ItemSpecs itemSpecs, String auctionId) {
        Item item = new Item(itemSpecs.name, itemSpecs.description, itemSpecs.minimumBid, auctionId);
        auctionList.add(item);
    }

    public void main(String[] args) {
        int address = Integer.parseInt(args[0]);
    }

    public void AuctionHouse(String address, int clientPort, int ServerPort) throws IOException {
        //create item list
        auctionId = String.valueOf(Math.random()*100) + String.valueOf(System.currentTimeMillis());
        Auction.itemsList.createItemSpecsList();
        for(int i = 0; i < 3; i++) {
            addSpecs(Auction.itemsList.getRandomElement(), auctionId);
        }

        int portNumber = Integer.parseInt(address);

        //bank connection
        try(
                //Hardcoded to my settings for now
                //TODO: create interface to select bank connection...
                Socket bankSocket = new Socket("localhost",6000);
                //connect to bank IO stream
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(bankSocket.getInputStream()));
                PrintWriter out =
                        new PrintWriter(bankSocket.getOutputStream(), true);
        ){

            System.out.println("running on port: " + portNumber);
            BufferedReader stdIn =
                    new BufferedReader(new InputStreamReader(System.in));
            String fromServer;
            String fromUser;

            fromServer = in.readLine();
            while (fromServer != null) {
                System.out.println("Server: " + fromServer);

                if (fromServer.equals("Bye.")) {break;}

                fromUser = stdIn.readLine();
                if (fromUser != null) {
                    System.out.println("Client: " + fromUser);
                    out.println(fromUser);
                }
                fromServer = in.readLine();
            }

            System.out.println("from server null");
        }
    }

    public void shutdown(){
        ;
    }

    private class AuctionIn implements Runnable {
        /**
         * waits for messages.
         */
        @Override
        public void run() {
            try {
                ioIn = new ObjectInputStream(auctionBank.getInputStream());
                while(running){
                    String message = (String) ioIn.readObject();
                    processMessage(message);
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

        private void processMessage(String message) {
            //need parse indicator
            switch(message){
            }
        }

        private void hold(String message) {

        }

        /**
         * updates the balance variable to the bank balance given by the bank,
         *
         * @param message double
         */
        private void bankBalance(String message) {
            //balance = message.getBalance();
        }

        private void released(String message) {

        }

        private void registered(String message) {

        }
    }

    private class AgentProxy implements Runnable {
        private final Socket agentSocket; //the socket connected to an agent
        private ObjectInputStream agentIn; //Input stream for agentSocket
        private ObjectOutputStream agentOut;//output stream for agentSocket
        private String agentId; //The UUID of the agent when it registers
        private String message = null; //The message read from agentIn
        private final BlockingQueue<Boolean> bankSignOff;
        //Blocking queue for waiting on a HOLD response from the bank

        /**
         * Constructor for an AgentProxy object. The constructor takes in
         * an accepted socket from the server variable, opens streams from it,
         * and begins communication.
         * @param socket the accepted socket from the server variable
         */
        public AgentProxy(Socket socket){
            this.agentSocket = socket;
            bankSignOff = new LinkedBlockingDeque<>();
            try{
                agentIn = new ObjectInputStream(agentSocket.getInputStream());
                agentOut = new ObjectOutputStream(
                        agentSocket.getOutputStream());
                Thread client = new Thread(this);
                client.start();
            }catch(IOException e){
                agentsList.remove(this);
            }
        }

        /**
         * The run method is dedicating to reading message from an agent.
         * The method also adds the incoming message to the log
         */
        @Override
        public void run() {
            do{
                /*try{
                    //get input from agents
                    process(message);
                } catch (IOException|ClassNotFoundException e) {
                    agentShutdown(false);
                    message = null;
                }*/
            }while(message != null && running);
        }

        /**
         * deregister with agents and shutdown
         */
        private void agentShutdown(Boolean reason){
            ;
        }

        /**
         * This method is a switch to redirect incoming messages from agents
         * to the appropriate method for processing.
         * @param message The message sent from an agent
         */
        private void process(String message) {
            //some parsing
            /*switch(message){
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
            }*/
        }

        /**
         * update updates auctionList
         */
        private void update(){

        }

        /**
         * register initial discourse agent
         */
        private void register(String message){

        }

        /**
         * new bid entry process
         */
        private void outBid(String oldBidder,Item item){

        }

        /**
         * announces winner of item
         */
        private void winner(Item item){

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
        private void bid(String message){
            ;
        }

        /**
         * release requests the bank release hold on account
         */
        private synchronized void release(String id, Double amount) {
        }

        /**
         * reject tells bidder bid was rejected
         */
        private void reject(String itemID, String name){

        }

        /**
         * accept lets the agent know bid was accepted.
         * update auctionList
         */
        private void accept(UUID item, String name){

        }

        /**
         * agentOut writes/sends message to agentSocket.
         */
        private void agentOut(String message) {

        }
    }

    private class AuctionServer implements Runnable {
        @Override
        public void run() {
            /*try{
                while(running){
                    Socket clientSocket = server.accept();
                    Agent newAgent = new Agent(clientSocket);
                    agentsList.add(newAgent);
                }
            }catch (IOException e){
                running = false;
            }*/
        }
    }

    /**
     * getBalance returns ballance
     * @return balance double
     */
    public void getBalance() {
        //return balance;
    }

    /**
     * getAuctionId returns auctionId
     */
    void getAuctionId() {
        //return auctionId;
    }
}
