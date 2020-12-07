package Auction;

import shared.DBMessage;
import shared.Items.Item;
import shared.Message;

import java.util.ArrayList;

public class CountDown implements Runnable {
    public static ArrayList<Item> auctionList = new ArrayList<>();
    private static final ArrayList<Item> auctionHistory = new ArrayList<>();

    public static ArrayList<Item> getAuctionList() {
        return auctionList;
    }
    /**
     * run started at list creation and at the addition of each new
     * item to the list. Starts time on that item and manages method
     * calls to determine winner and swap in new items.
     */
    @Override
    public void run() {
        while(AH_AgentThread.running) {
            try {
                ArrayList<Item> auctionList = getAuctionList();
                int needed =  4 - auctionList.size();
                if(needed > 0){
                    addItems(needed);
                }
                for (Item value : auctionList) {
                    long currentTime = System.currentTimeMillis();
                    value.remainingTime(currentTime);
                    long timeLeft = value.getRemainingTime();
                    if (timeLeft <= 0) {
                        itemResult(value);
                    }
                }
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * After the time expires on an Item for sale. This method checks if
     * there was any bidders and sends the WINNER message to that bidder.
     * Also releases the hold on the bidders amount.
     * @param item the item being checked
     */
    private void itemResult(Item item) {
        int bidder = item.getBidderId();
        AH_AgentThread agent = AuctionServer.agentSearch(bidder);
        if (agent != null) {
            agent.winner(item);
            Message release = new Message.Builder()
                    .command(Message.Command.UNBLOCK)
                    .balance(item.getCurrentBid())
                    .accountId(bidder).senderId(AuctionServer.auctionId);
            BankActions.sendToBank(release);
        }
        auctionList.remove(item);
    }

    static void addItems(int needed) {
        while(needed > 0) {
            //message = DBMessage.Builder().command(GET).table.(ITEMS).accountId(your random int).build
            DBMessage message = new DBMessage.Builder().command(DBMessage.Command.GET)
                  .table(DBMessage.Table.ITEM).accountId((int) (Math.random() * (199))).build();
            if(!auctionHistory.contains((Item) message.getPayload())) {
                auctionList.add((Item) message.getPayload());
                auctionHistory.add((Item) message.getPayload());
                needed--;
            }
        }
    }
}
