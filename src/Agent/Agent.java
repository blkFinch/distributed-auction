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

    /**************************************************************************
     * setCurrentAuction - Sets global variable currentAuction to new Auction *
     *                                                                        *
     * @param key - key for Map auctionProxies corresponding to the new       *
     *              current Auction House                                     *
     * Returns nothing                                                        *
     *************************************************************************/
    public void setCurrentAuction(String key){
        currentAuction = auctionProxies.get(key);
    }

    /**************************************************************************
     * getCurrentItems - Gets ArrayList<Item> associated with currentAuction  *
     *                                                                        *
     * Takes no arguments                                                     *
     * @return List of Items associated with global variable currentAuction   *
     *************************************************************************/
    public ArrayList<Item> getCurrentItems() {
        return auctionItems.get(currentAuction.getAuctionName());
    }

    /**************************************************************************
     * getMessageList - Gets global variable messageList                      *
     *                                                                        *
     * Takes no arguments                                                     *
     * @return messageList                                                    *
     *************************************************************************/
    public List<String> getMessageList(){
        List<String> newMessages = new ArrayList<>(messageList);
        messageList.clear();
        return newMessages;
    }

    /**************************************************************************
     * printItemsWon                                                          *
     *                                                                        *
     * Prints the name and itemID of each item won by this Agent in an Auction*
     *                                                                        *
     * Takes no arguments, returns nothing                                    *
     *                                                                        *
     * Variable: keySet - set of keys stored in Map itemsWon                  *
     *************************************************************************/
    public void printItemsWon(){
        Set<String> keySet = itemsWon.keySet();
        System.out.println("Items won from auction:");
        for(String key : keySet){
            System.out.println(key+" (ID: "+itemsWon.get(key)+")");
        }
    }

    /**************************************************************************
     * updateAuctionProxies                                                   *
     *                                                                        *
     * Takes List of new connections from the bankProxy and opens connections *
     * to new Auction Houses                                                  *
     *                                                                        *
     * Takes no arguments, returns nothing                                    *
     *                                                                        *
     * Variables:                                                             *
     * newConnections - List of new ConnectionReqs from bankProxy             *
     * auctionProxy - temporary AgentProxy variable that holds each new proxy *
     *                while it is being made                                  *
     *************************************************************************/
    public void updateAuctionProxies(){
        List<ConnectionReqs> newConnections = bankProxy.getNewConnections();
        AgentProxy auctionProxy;
        for(ConnectionReqs conn : newConnections){
            if(!auctionProxies.containsKey(conn.getName())){
                auctionProxy = new AgentProxy(username, "auction",
                        conn.getIp(), "" + conn.getPort(), false);
                setProxyID(auctionProxy);
                auctionProxy.setAuctionName(conn.getName());
                auctionProxy.start();
                auctionProxies.put(conn.getName(), auctionProxy);
            }
        }
    }

    /**************************************************************************
     * handleMessages                                                         *
     *                                                                        *
     * Reads messages from all proxies and handles messages that need to be   *
     * addressed behind the scenes                                            *
     *                                                                        *
     * Takes no arguments, returns nothing                                    *
     *                                                                        *
     * Variables:                                                             *
     * bankMessages - List of Messages from the bankProxy                     *
     * auctionMessages - List of A_AH_Messages from the auctionProxies        *
     * itemList - ArrayList of items passed in from an A_AH_Messages          *
     * keySet - Set of keys from auctionProxies                               *
     * proxy - temporary AgentProxy that holds each auctionProxy while it is  *
     *         being accessed                                                 *
     *************************************************************************/
    public void handleMessages(){
        List<Message> bankMessages = bankProxy.readBankMessages();
        List<A_AH_Messages> auctionMessages;
        ArrayList<Item> itemList;
        Set<String> keySet = auctionProxies.keySet();
        AgentProxy proxy;
        for(Message mes : bankMessages){
            messageList.add("Bank: " + mes.toString());
            if(userID == -1 && mes.getAccountId() != -1){
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

    /**************************************************************************
     * closeConnections                                                       *
     *                                                                        *
     * Sends deregister messages to each open connection and waits for        *
     * confirmation from the server before closing the socket                 *
     *                                                                        *
     * Takes no arguments, returns nothing                                    *
     *                                                                        *
     * Variables:                                                             *
     * deregisterBank - Message to tell Bank to deregister this Agent         *
     * deregisterAuction - A_AH_Messages to tell each Auction to deregister   *
     *                     this Agent                                         *
     * keySet - Set of keys for every AgentProxy in auctionProxies            *
     * bankMessages - List of Messages from bankProxy                         *
     * auctionMessages - List of A_AH_Messages from auctionProxies            *
     * bankClosed - boolean that is true if the deregister confirmation has   *
     *              been received from the bankProxy                          *
     * numConnections - number of total AgentProxies                          *
     * numClosed - number of AgentProxies that have successfully closed so far*
     *************************************************************************/
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

