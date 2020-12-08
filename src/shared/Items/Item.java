package shared.Items;

import java.io.Serializable;
/**
 * Ryan Cooper Adapated
 * Galen furthur modified
 * Message for communicating across sockets. Adapted from
 * https://github.com/ApolloRez/DistributedAuction/tree/master/src
 */
public class Item implements Serializable {
    private  int    auctionId;
    private  String name;
    private  String description;
    private  int minimumBid;
    private  int currentBid;
    private  int    bidderId;
    private  long   remainingTime;
    protected  long   bidTime;
    private  int    itemId;

    public Item(String setName, String descriptionSet, int value, int Id) {
        description   = descriptionSet;
        name          = setName;
        minimumBid    = value;
        auctionId     = Id;
        currentBid    = minimumBid;
        bidderId      = -1;
        remainingTime = 30; //30 seconds until bid is final
        itemId        = Id;
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
    public void newBid(int bidder, double amount) {
        this.bidderId = bidder;
        this.currentBid = (int) amount;
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

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", minimumBid=" + minimumBid +
                ", itemId=" + itemId +
                '}';
    }
}
