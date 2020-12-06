package Agent;

import Auction.Item;
import shared.A_AH_Messages;
import shared.ConnectionReqs;
import shared.Message;

import java.io.IOException;
import java.util.*;

public class Agent{
    private final String username;
    private int userID;
    private AgentProxy bankProxy;
    private Map<String, AgentProxy> auctionProxies;
    private AgentProxy currentAuction;
    private ArrayList<Item> currentItems;
    private List<String> messageList;

    public Agent(String user, String host, String port, boolean newAcc, int initBal) throws Exception{
        username = user;
        userID = -1;
        bankProxy = new AgentProxy(user,"bank", host, port, newAcc);
        auctionProxies = new HashMap<>();
        currentItems = new ArrayList<>();
        messageList = new ArrayList<>();
        if(newAcc){ bankProxy.setInitBal(initBal); }
    }

    public String getUsername(){ return username; }

    public int getUserID(){ return userID; }

    public void runBank(){ bankProxy.start(); }

    private void setProxyID(AgentProxy proxy){ proxy.setUserID(userID); }

    public void sendBankMessage(Message message){ bankProxy.sendMessage(message); }

    public void sendAuctionMessage(Message message){
        currentAuction.sendMessage(message);
    }

    public Set<String> getAuctionNames(){ return auctionProxies.keySet(); }

    public void setCurrentAuction(String key){ currentAuction = auctionProxies.get(key); }

    public ArrayList<Item> getCurrentItems() { return currentItems; }

    public void updateAuctionProxies(){
        List<ConnectionReqs> newConnections = bankProxy.getNewConnections();
        AgentProxy auctionProxy;
        for(ConnectionReqs conn : newConnections){
            try{
                auctionProxy = new AgentProxy(username,"auction", conn.getIp(),
                        ""+conn.getPort(), false);
                setProxyID(auctionProxy);
                auctionProxy.start();
                auctionProxies.put(conn.getName(), auctionProxy);
            } catch(IOException e){
                System.out.println("Connection to auction house failed");
            }
        }
    }

    public void handleMessages(){
        List<Message> bankMessages = bankProxy.readBankMessages();
        List<A_AH_Messages> auctionMessages;
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
                if(key.equals(currentAuction.getName())){
                    if(mes.getAuctionList() != null ){
                        currentItems = mes.getAuctionList();
                    }
                }
            }
        }
    }
}

