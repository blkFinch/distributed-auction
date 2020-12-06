package Agent;

import shared.ConnectionReqs;
import shared.Message;

import java.io.IOException;
import java.util.*;

public class Agent{
    private final String username;
    private int userID;
    private static AgentProxy bankProxy;
    private static Map<String, AgentProxy> auctionProxies;
    private AgentProxy currentAuction;

    public Agent(String user, String host, String port, boolean newAcc, int initBal) throws Exception{
        username = user;
        userID = -1;
        bankProxy = new AgentProxy(user,"bank", host, port, newAcc);
        auctionProxies = new HashMap<>();
        if(newAcc){ bankProxy.setInitBal(initBal); }
    }

    public void runBank(){ bankProxy.start(); }

    private void setProxyID(AgentProxy proxy){ proxy.setUserID(userID); }

    public void sendBankMessage(Message message){ bankProxy.sendMessage(message); }

    public void sendAuctionMessage(String key, Message message){
        auctionProxies.get(key).sendMessage(message);
    }

    public Set<String> getAuctionNames(){ return auctionProxies.keySet(); }

    public void setCurrentAuction(String key){ currentAuction = auctionProxies.get(key); }

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
        for(Message mes : bankMessages){
            if(userID == -1 && mes.getAccountId() != -1){
                userID = mes.getAccountId();
                setProxyID(bankProxy);
            }
        }
    }
}

