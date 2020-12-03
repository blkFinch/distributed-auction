package Auction;


public class Item {
    private String auctionID;
    private String name;
    private String description;
    private double minimumBid;
    private double currentBid;
    private String bidderId;
    private long remainingTime;
    private long bidTime;
    private String itemID;

    public void Item(String name, double value, String auctionID) {
        this.name = name;
        this.minimumBid = value;
        this.auctionID = auctionID;
        this.currentBid = this.minimumBid;
        bidderId = null;
        remainingTime = 30; //30 until bid is final
        itemID = String.valueOf(Math.random()*1000);
        bidTime = System.currentTimeMillis();
    }

    /**
     * getName returns name String
     * @return name String
     */
    public String getName() {
        return name;
    }

    /**
     * getCurrentBid returns currentBid
     * @return currentBid double
     */
    public double getCurrentBid() {
        return currentBid;
    }

    /**
     * getMinimumBid returns minimumBid
     * @return minimumBid double
     */
    public double getMinimumBid() {
        return minimumBid;
    }

    /**
     * newBid sets new currentBid, bidderID and resets bidTime
     * @param bidder String
     * @param amount double
     */
    public void newBid(String bidder, double amount) {
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
     * @param currentTime long
     */
    public void remainingTime(long currentTime) {
        remainingTime = 30 - ((currentTime-bidTime)/1000);
    }

    /**
     * getRemainingTime returns remaining time
     * @return remainingTime long
     */
    public long getRemainingTime(){
        return remainingTime;
    }

    /**
     * getItemID returns ItemID
     * @return itemID String
     */
    public String getItemID(){
        return itemID;
    }

    /**
     * getBidderID returns bidderID
     * @return bidderID String
     */
    public String getBidderID(){
        return bidderId;
    }
}
