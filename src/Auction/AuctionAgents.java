package Auction;

import shared.A_AH_Messages;
import shared.BankMessages;

import javax.management.AttributeList;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class AuctionAgents {
    private static boolean    running         = true;
    private final  List<AuctionAgents.Agent> agentsList = new LinkedList<>();
    private static ServerSocket auctionSocket;

    private void getAuctionSocket() {
        auctionSocket = AuctionMain.getAuctionSocket();
    }

    class AHServer implements Runnable {
        private AttributeList agentsList;

        @Override
        public void run() {
            try {
                while(running) {
                    Socket clientSocket  = auctionSocket.accept();
                    Agent newAgent       = new Agent(clientSocket);
                    agentsList.add(newAgent);
                }
            } catch (IOException e) {
                running = false;
            }
        }
    }

    /**
     * This class is dedicated to communicating/processing messages from/with
     * agents.
     * Each instance of this class represents communication with one agent.
     */
    class Agent implements Runnable {
        private final Socket            agentsSocket; //the socket connected to an agent
        private ObjectInputStream agentIn; //Input stream for agentSocket
        private ObjectOutputStream agentOut;//output stream for agentSocket
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
            try {
                if(reason) {
                    A_AH_Messages shutdown = A_AH_Messages.Builder.newBuilder()
                            .topic(A_AH_Messages.A_AH_MTopic.DEREGISTER)
                            .build();
                    sendOut(shutdown);
                    if(!agentsSocket.isClosed()) {
                        agentOut.close();
                        agentIn.close();
                        agentsSocket.close();
                    }
                } else {
                    if(!agentsSocket.isClosed()) {
                        agentOut.close();
                        agentIn.close();
                        agentsSocket.close();
                    }
                }
            } catch (IOException e) {
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
            AuctionAgents.Agent agent = agentSearch(oldBidder);
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
        private void bid(A_AH_Messages message) {
            int    itemId   = message.getItem();
            int    bidderId = message.getAccountId();
            String name     = message.getItemName();
            double amount   = message.getBid();
            Item bidItem    = itemSearch(itemId);

            if(bidItem == null){
                reject(itemId,name);
                return;
            }
            double value = bidItem.getCurrentBid();
            if( value < bidItem.getMinimumBid()){
                value = bidItem.getMinimumBid();
            }
            if(amount > value){
                BankMessages requestHold = new BankMessages.Builder()
                        .command(BankMessages.Command.BLOCK)
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
            BankMessages release = new BankMessages.Builder()
                    .command(BankMessages.Command.UNBLOCK)
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
     * Sends the given message to the bank and adds it to the log for display.
     * Looped messages (GET_AVAILABLE) are ignored when adding to log.
     * @param message message being sent to the bank.
     */
    private synchronized void sendToBank(BankMessages message) {
        try {
            BankMessages.Command temp = message.getCommand();
            if(temp != BankMessages.Command.GETBALANCE) {
                System.err.println("Bank: " + message);
            }
            bankOut.reset();
            bankOut.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
     * searches for agent in active agent list
     * @param id id of agent we want
     * @return returns the agentProxy we want, null otherwise
     */
    private AuctionAgents.Agent agentSearch(int id) {
        for(AuctionAgents.Agent agent: agentsList) {
            if(agent.agentId == id) {
                return agent;
            }
        }
        return null;
    }
}
