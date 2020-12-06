package Auction;

import shared.Items.Item;
import shared.Items.ItemSpecs;
import shared.Items.ItemSpecsList;

import java.util.ArrayList;

public class AuctionHouseSpecs {
    private static ArrayList<Item> auctionList = new ArrayList<>();
    /*private final  BlockingQueue<Boolean>   check           = new LinkedBlockingDeque<>();
    private static boolean                  running         = true;
    private        double                   balance         = 0.0;
    private        int                      auctionId;
    private        int                      port;  //Auction server port number
    private final  List<AuctionHouse.Agent> agentsList      = new LinkedList<>();
    private        ObjectInputStream        ioIn;
    private        ObjectOutputStream       ioOut;
    private        ServerSocket             auctionSocket   = null; //the socket connected to agents
    private        Socket                   bankSocket; //for bank
    private        String                   ip; //Auction server ip address*/

    public static ArrayList<Item> getAuctionList() {
        return auctionList;
    }
    /**
     * creates catalogue for items to sell
     */
    static void createAuctionList(int size) {
        ItemSpecsList.createItemSpecsList();
        for (int i = 0; i < size; i++) {
            addSpecs(ItemSpecsList.getRandomElement());//, auctionId);
        }
    }

    /**
     * addItems completes items for the auction house
     *
     * @param itemSpecs ItemSpecs
     */
    private static void addSpecs(ItemSpecs itemSpecs) {//}, int auctionId) {
        Item item = new Item(itemSpecs.name, itemSpecs.description,
                itemSpecs.minimumBid, -1);
        auctionList.add(item);
    }

    /**
     * Constructor for an AgentProxy object. The constructor takes in
     * an accepted socket from the server variable, opens streams from it,
     * and begins communication.
     * @param socket the accepted socket from the server variable
     *//*
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
            activeAgents.remove(this);
        }
    }*/
}
