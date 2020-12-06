package Auction;

import shared.A_AH_Messages;
import shared.A_AH_Messages.A_AH_MTopic;
import shared.Items.Item;
import shared.Message;
import Auction.AuctionHouseSpecs;
import Auction.AH_AgentThread;
import Auction.BankActions;

import java.io.IOException;
import java.util.UUID;

public class AgentActions {
    /**
     * This method grabs the itemID, bidderId, name, and amount of the
     * item to bid on. First it checks if the item is still for sale, then
     * checks if the bid amount is above the minimum/current bid. Then it
     * requests the bank to hold the bidded funds and waits for a response.
     * After receiving the response, it then decides whether to reject or
     * accept the bid.
     * @param message The message with AMType BID
     */
    static void bid(A_AH_Messages message) {
        int itemID = message.getItem();
        int bidderId = message.getAccountId();
        String name = message.getItemName();
        double cost = message.getBid();
        Item bidItem = itemSearch(itemID);
        if(bidItem == null){
            reject(itemID,name);
            return;
        }
        double value = bidItem.getCurrentBid();
        if( value < bidItem.getMinimumBid()){
            value = bidItem.getMinimumBid();
        }
        if(cost > value){
            Message requestHold = new Message.Builder()
                    .command(Message.Command.HOLD)
                    .accountId(bidderId).amount(cost).send(this.agentId);
            try{
                //requests the hold.
                BankActions.sendToBank(requestHold);
                //waits for the response from bank.
                Boolean success = bankSignOff.take();
                if(success){
                    //accepts bid and lets the last bidder know
                    //they were outbidded
                    int oldBidder = bidItem.getBidderId();
                    if(oldBidder != -1){
                        release(oldBidder, value);
                        outBid(oldBidder, bidItem);
                    }
                    bidItem.outBid(bidderId, cost);
                    accept(bidItem.getItemID(), bidItem.name());
                }else{
                    reject(itemID,name);
                }
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }else{
            reject(itemID,name);
        }
    }

    /**
     * This method is letting the connected agent know they successfully
     * registered and sends the catalogue of items for sale. The method
     * also stores the agent's UUID for future reference
     * @param message The register message the agent sent
     */
    static A_AH_Messages register(A_AH_Messages message) {
        int agentId = message.getAccountId();
        A_AH_Messages reply = A_AH_Messages.Builder.newBuilder()
                .topic(A_AH_MTopic.REGISTER)
                //.auctionId(auctionId)
                .auctionList(AuctionHouseSpecs.getAuctionList())
                .build();
        return reply;
    }

    /**
     * This method creates the message with the updated catalogue
     * and passes it to sendOut to send to the agent
     */
    static A_AH_Messages update(){
        A_AH_Messages update = A_AH_Messages.Builder.newBuilder()
                .topic(A_AH_Messages.A_AH_MTopic.UPDATE)
                .auctionList(AuctionHouseSpecs.getAuctionList())
                .build();
        return update;
    }

    /**
     * creates message to agent about shutdown then shuts down agent port.
     * sending message left to AH_AgentThread.
     */
    static A_AH_Messages deRegister() {
        A_AH_Messages shutDown = A_AH_Messages.Builder.newBuilder()
                .topic(A_AH_Messages.A_AH_MTopic.DEREGISTER)
                .build();
        return shutDown;
    }

    /**
     * Lets the bidder know its bid was rejected due to various reasons
     * (not enough funds, bid not high enough, etc.)
     * @param itemId The int of the item bid on
     * @param name the name of the item
     */
    private static void reject(int itemId, String name) {
        A_AH_Messages reject = A_AH_Messages.Builder.newBuilder()
                .topic(A_AH_MTopic.FAILURE)
                .name(name)
                .itemId(itemId)
                .build();
        AH_AgentThread.sendOut(reject);
    }

    /**
     * itemSearch searches the auctionList for an item of matching id
     *
     * @param itemId the int of the item being searched
     * @return returns the item searched, or null if item isn't found
     */
     private static Item itemSearch(int itemId) {
        for(Item item: AuctionHouseSpecs.getAuctionList()) {
            if(item.getItemID() == itemId) {
                return item;
            }
        }
        return null;
    }

    /**
     * requests the bank to release the hold of (amount) amount on account
     * id
     * @param id the account(bidder) having their funds released
     * @param amount the amount requested to release
     */
    private synchronized void release(int id, int amount) {
        Message unBlock = new Message.Builder()
                .command(Message.Command.UNBLOCK)
                .accountId(id)
                .cost(amount)
                .senderId(0);
        BankActions.sendToBank(unBlock);
    }
}
