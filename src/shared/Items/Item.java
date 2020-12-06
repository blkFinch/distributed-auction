package shared.Items;

import java.io.Serializable;

public class Item implements Serializable {
    private static int    auctionId;
    private static String name;
    private static String description;
    private static int minimumBid;
    private static int currentBid;
    private static int    bidderId;
    private static long   remainingTime;
    private static long   bidTime;
    private static int    itemId;

    public Item(String setName, String descriptionSet, int value, int Id) {
        description   = descriptionSet;
        name          = setName;
        minimumBid    = value;
        auctionId     = Id;
        currentBid    = minimumBid;
        bidderId      = -1;
        remainingTime = 30; //30 seconds until bid is final
        itemId        = Integer.parseInt(String.valueOf(Math.random()*1000) +
                String.valueOf(System.currentTimeMillis()));
        bidTime       = System.currentTimeMillis();
    }

    /**
     * getName returns name String
     *
     * @return name String
     */
    public String getName() {
        return name;
    }

    /**
     * getCurrentBid returns currentBid
     *
     * @return currentBid double
     */
    public int getCurrentBid() {
        return currentBid;
    }

    /**
     * getMinimumBid returns minimumBid
     *
     * @return minimumBid double
     */
    public int getMinimumBid() {
        return minimumBid;
    }

    /**
     * newBid sets new currentBid, bidderID and resets bidTime
     *
     * @param bidder String
     * @param amount double
     */
    public void newBid(int bidder, int amount) {
        this.bidderId = bidder;
        this.currentBid = amount;
        resetBidTime();
    }

    /**
     * restBidTime sets new bidTime to time of current bid
     */
    public void resetBidTime() {
        bidTime = System.currentTimeMillis();
    }

    /**
     * elapsedTime sets/updates the remaining time before bid is accepted
     *
     * @param currentTime long
     */
    public void remainingTime(long currentTime) {
        remainingTime = 30 - ((currentTime-bidTime)/1000);
    }

    /**
     * setBid replace the old bidder and bid with the new ones
     *
     * @param bidder int
     * @param bid double
     */
    public void setBid(int bidder, int bid){
        this.bidderId = bidder;
        this.currentBid = bid;
        resetBidTime();
    }

    /**
     * getRemainingTime returns remaining time
     *
     * @return remainingTime long
     */
    public long getRemainingTime(){
        return remainingTime;
    }

    /**
     * getItemID returns ItemID
     *
     * @return itemID String
     */
    public int getItemID(){
        return itemId;
    }

    /**
     * getBidderID returns bidderID
     *
     * @return bidderID String
     */
    public int getBidderId(){
        return bidderId;
    }
}
