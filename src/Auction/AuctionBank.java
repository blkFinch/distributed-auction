package Auction;

import shared.BankMessages;

import javax.management.AttributeList;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

public class AuctionBank {
    private static boolean               running         = true;
    private static final ArrayList<Item> auctionList     = new ArrayList<>();
    private static int                   auctionId;

    /**
     * creates catalogue for items to sell
     */
    private static void createAuctionList(int size) {
        ItemSpecsList.createItemSpecsList();
        for (int i = 0; i < size; i++) {
            addSpecs(ItemSpecsList.getRandomElement(), auctionId);
        }
    }

    /**
     * addItems completes items for the auction house
     *
     * @param itemSpecs ItemSpecs
     * @param auctionId String
     */
    private static void addSpecs(ItemSpecs itemSpecs, int auctionId) {
        Item item = new Item(itemSpecs.name, itemSpecs.description,
                itemSpecs.minimumBid, auctionId);
        auctionList.add(item);
    }

    class AuctionIn implements Runnable {
        private ObjectInputStream bankIn;
        private Socket bankSocket;

        /**
         * waits for messages.
         */
        @Override
        public void run() {
            try {
                setbankSocket();
                bankIn = new ObjectInputStream(bankSocket.getInputStream());
                while (running) {
                    BankMessages message = (BankMessages) bankIn.readObject();
                    BankMessages.Command topic = message.getCommand();
                    switch (topic) {
                        case BLOCK:
                            block(message);
                            break;
                        case LOGIN:
                            login(message);
                            break;
                        case REGISTERHOUSE:
                            registerHouse(message);
                            break;
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                try {
                    bankIn.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        private void setbankSocket() {
            bankSocket = AuctionMain.getBankSocket();
        }

        private void registerHouse (BankMessages message){
        }

        private void block (BankMessages message){
            int bidder = message.getAccountId();
            BankMessages.Response response = message.getResponse();
            AuctionAgents.Agent temp = agentSearch(bidder);
            if (temp != null) {
                try {
                    if (response == BankMessages.Response.SUCCESS) {
                        temp.bankSignOff.put(true);
                    } else if (response == BankMessages.Response.OVERDRAFT) {
                        temp.bankSignOff.put(false);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * updates the balance variable to the bank balance given by the bank,
         *
         * @param message the message with the available balance for this object
         */
        private void bankBalance (BankMessages message){
            balance = message.getBalance();
        }

        private void released (BankMessages message){
            BankMessages.Response response = message.getResponse();
        }

        private void login (BankMessages message){
            auctionId = message.getAccountId();
            createAuctionList(3);
            check.add(true);
            Thread timer = new Thread(new auctionListMaintenance());
            timer.setDaemon(true);
            timer.setPriority(4);
            timer.start();
        }
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
}
