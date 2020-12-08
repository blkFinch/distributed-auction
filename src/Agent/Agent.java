package Agent;

import shared.A_AH_Messages;
import shared.ConnectionReqs;
import shared.Items.Item;
import shared.Message;

import java.util.*;

/******************************************************************************
 * Ashley Krattiger                                                           *
 *                                                                            *
 * Agent                                                                      *
 *                                                                            *
 * This class consolidates the information held by the Agent program, passes  *
 * information back and forth, reads messages, and initializes communication  *
 * threads.                                                                   *
 *****************************************************************************/
public class Agent{
    /**************************************************************************
     * Global Variables:                                                      *
     *                                                                        *
     * username - String associated with this Agent's account                 *
     * userID - int associated with this Agent's account                      *
     * bankProxy - AgentProxy connected to the Bank                           *
     * auctionProxies - Map of AgentProxies connected to each Auction House.  *
     *                  Keys are the name of the Auction House                *
     * auctionItems - Map of ArrayList of Items passed in from each Auction   *
     *                House. Keys are the name of the Auction House           *
     * currentAuction - AgentProxy associated with the Auction that is pulled *
     *                  up on the AgentGUI                                    *
     * messageList - List of String messages representing the Messages sent by*
     *               the Bank and all Auction Houses                          *
     * itemsWon - Map of itemIDs associated with items won by this Agent in an*
     *            auction. Keys are the Item names                            *
     *************************************************************************/
    private final String username;
    private int userID;
    private final AgentProxy bankProxy;
    private final Map<String, AgentProxy> auctionProxies;
    private final Map<String, ArrayList<Item>> auctionItems;
    private AgentProxy currentAuction;
    private final List<String> messageList;
    private final Map<String, Integer> itemsWon;

    /**************************************************************************
     * Constructor - Initializes global variables                             *
     *                                                                        *
     * @param user - username for this Agent's account                        *
     * @param host - Bank's IP address                                        *
     * @param port - Bank's open port number                                  *
     * @param newAcc - boolean that's true if a new Bank account needs to be  *
     *                 opened                                                 *
     * @param initBal - initial balance for a new Bank account                *
     *************************************************************************/
    public Agent(String user, String host, String port, boolean newAcc,
                 int initBal){
        username = user;
        userID = -1;
        bankProxy = new AgentProxy(user,"bank", host, port, newAcc);
        auctionProxies = new HashMap<>();
        auctionItems = new HashMap<>();
        messageList = new ArrayList<>();
        itemsWon = new HashMap<>();
        if(newAcc){ bankProxy.setInitBal(initBal); }
    }

    /**************************************************************************
     * getUsername - Gets global variable username                            *
     *                                                                        *
     * Takes no arguments                                                     *
     * @return username                                                       *
     *************************************************************************/
    public String getUsername(){ return username; }

    /**************************************************************************
     * getUserID - Gets global variable userID                                *
     *                                                                        *
     * Takes no arguments                                                     *
     * @return username                                                       *
     *************************************************************************/
    public int getUserID(){ return userID; }

    /**************************************************************************
     * runBank                                                                *
     *                                                                        *
     * Starts the Bank communication Thread                                   *
     *                                                                        *
     * Takes no arguments, returns nothing                                    *
     *************************************************************************/
    public void runBank(){ bankProxy.start(); }

    /**************************************************************************
     * setProxyID                                                             *
     *                                                                        *
     * Sets userID in a given AgentProxy to the userID in this Agent          *
     *                                                                        *
     * @param proxy - AgentProxy whose userID will be set                     *
     * Returns nothing                                                        *
     *************************************************************************/
    private void setProxyID(AgentProxy proxy){ proxy.setUserID(userID); }

    /**************************************************************************
     * sendBankMessage                                                        *
     *                                                                        *
     * Uses the bankProxy to send a given Message to the Bank                 *
     *                                                                        *
     * @param message - Message to be sent to the Bank                        *
     * Returns nothing                                                        *
     *************************************************************************/
    public void sendBankMessage(Message message){
        bankProxy.sendBankMessage(message);
    }

    /**************************************************************************
     * sendAuctionMessage                                                     *
     *                                                                        *
     * Uses the AgentProxy associated with the currently open Auction House to*
     * send a given Message to the open Auction House                         *
     *                                                                        *
     * @param message - Message to be sent to the Auction House               *
     * Returns nothing                                                        *
     *************************************************************************/
    public void sendAuctionMessage(A_AH_Messages message){
        currentAuction.sendAuctionMessage(message);
    }

    /**************************************************************************
     * getAuctionNames - Gets global variable userID                          *
     *                                                                        *
     * Takes no arguments                                                     *
     * @return username                                                       *
     *************************************************************************/
    public Set<String> getAuctionNames(){ return auctionProxies.keySet(); }

    public void setCurrentAuction(String key){ currentAuction = auctionProxies.get(key); }

    public ArrayList<Item> getCurrentItems() {
        return auctionItems.get(currentAuction.getAuctionName());
    }

    public List<String> getMessageList(){
        List<String> newMessages = new ArrayList<>(messageList);
        messageList.clear();
        return newMessages;
    }

    public void printItemsWon(){
        Set<String> keySet = itemsWon.keySet();
        System.out.println("Items won from auction:");
        for(String key : keySet){
            System.out.println(key+" (ID: "+itemsWon.get(key)+")");
        }
    }

    public void updateAuctionProxies(){
        List<ConnectionReqs> newConnections = bankProxy.getNewConnections();
        AgentProxy auctionProxy;
        for(ConnectionReqs conn : newConnections){
            if(!auctionProxies.containsKey(conn.getName())){
                auctionProxy = new AgentProxy(username, "auction", conn.getIp(),
                        "" + conn.getPort(), false);
                setProxyID(auctionProxy);
                auctionProxy.setAuctionName(conn.getName());
                auctionProxy.start();
                auctionProxies.put(conn.getName(), auctionProxy);
            }
        }
    }

    public void handleMessages(){
        List<Message> bankMessages = bankProxy.readBankMessages();
        List<A_AH_Messages> auctionMessages;
        ArrayList<Item> itemList;
        Set<String> keySet = auctionProxies.keySet();
        AgentProxy proxy;
        for(Message mes : bankMessages){
            messageList.add("Bank: " + mes.toString());
            if(userID == -1 && mes.getAccountId() != -1){
                System.out.println("Agent: "+mes.getAccountId());
                userID = mes.getAccountId();
                setProxyID(bankProxy);
            }
        }
        for(String key : keySet){
            proxy = auctionProxies.get(key);
            auctionMessages = proxy.readAuctionMessages();
            for(A_AH_Messages mes : auctionMessages){
                messageList.add(key + ": " + mes.toString());
                if(mes.getAuctionList() != null){
                    itemList = new ArrayList<>(mes.getAuctionList());
                    if(auctionItems.containsKey(key)){
                        auctionItems.replace(key, itemList);
                    }
                    else{
                        auctionItems.put(key, itemList);
                    }
                }
                if(mes.getTopic() == A_AH_Messages.A_AH_MTopic.WINNER){
                    itemsWon.put(mes.getItemName(), mes.getItem());
                }
            }
        }
    }

    public void closeConnections(){
        Message deregisterBank = new Message.Builder()
                .command(Message.Command.DEREGISTER)
                .senderId(userID);
        A_AH_Messages deregisterAuction = new A_AH_Messages.Builder()
                .topic(A_AH_Messages.A_AH_MTopic.DEREGISTER)
                .accountId(userID)
                .build();
        Set<String> keySet = auctionProxies.keySet();
        List<Message> bankMessages;
        List<A_AH_Messages> auctionMessages;
        boolean bankClosed = false;
        int numConnections = 0;
        int numClosed = 0;
        for(String key : keySet){
            auctionProxies.get(key).sendAuctionMessage(deregisterAuction);
            numConnections++;
        }
        bankProxy.sendBankMessage(deregisterBank);
        numConnections++;
        while(numClosed != numConnections){
            if(!bankClosed){
                bankMessages = bankProxy.readBankMessages();
                for(Message mes : bankMessages){
                    if(mes.getCommand() == Message.Command.DEREGISTER){
                        numClosed++;
                        bankClosed = true;
                    }
                }
            }
            for(String key : keySet){
                auctionMessages =auctionProxies.get(key).readAuctionMessages();
                for(A_AH_Messages mes : auctionMessages){
                    if(mes.getTopic() == A_AH_Messages.A_AH_MTopic.DEREGISTER){
                        numClosed++;
                        auctionProxies.remove(key);
                    }
                }
                keySet = auctionProxies.keySet();
            }
        }
    }
}

