package Agent;

import shared.ConnectionReqs;
import shared.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Agent{
    private String username;
    private static AgentProxy bankProxy;
    private static Map<ConnectionReqs, AgentProxy> auctionProxies;

    public Agent(String user, String host, String port, boolean newAcc) throws Exception{
        username = user;
        bankProxy = new AgentProxy(user,"bank", host, port, newAcc);
        auctionProxies = new HashMap<>();
        //bankProxy.run();
    }

    public void runBank(){ bankProxy.run(); }

    public void sendBankMessage(Message message){ bankProxy.sendMessage(message); }

    public void sendAuctionMessage(int ind, Message message){
        auctionProxies.get(ind).sendMessage(message);
    }

    public void updateAuctionProxies(){
        List<ConnectionReqs> newConns = bankProxy.getNewConnections();
        AgentProxy auctionProxy;
        for(ConnectionReqs conn : newConns){
            try{
                auctionProxy = new AgentProxy(username,"auction",
                        conn.getIp(), ""+conn.getPort(), false);
                //auctionProxies.put(conn, )
            } catch(IOException e){
                System.out.println("Connection to auction house failed");
            }
        }
    }


    /*public static void main(String[] args){
        BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
        String input;
        String type;
        String host;
        int port;
        boolean connected = false;
        auctionProxies = new ArrayList<>();

        while(!connected){
            System.out.println("What type of server are you connecting to?");
            try{
                type = in.readLine();
            } catch(IOException e){
                type = "none";
            }
            System.out.println("What is the host IP?");
            try{
                host = in.readLine();
            } catch(IOException e){
                host = "localhost";
            }
            System.out.println("What is the port?");
            try{
                input = in.readLine();
                port = Integer.parseInt(input);
                if(type.equals("bank")){
                    bankProxy = new AgentProxy(username, type, host, ""+port);
                    bankProxy.run();
                }
                else{
                    auctionProxies.add(new AgentProxy(username, type, host, ""+port));
                }
                connected = true;
            } catch(Exception e){
                System.out.println("Connection failed. Try again.");
            }
        }
        System.out.println("You are now connected.");
        String messages;
        while(true){
            messages = bankProxy.readMessages();
            if(!messages.equals("")){
                System.out.println("Bank: "+messages);
            }
            for(AgentProxy a : auctionProxies){
                messages = a.readMessages();
                if(!messages.equals("")){
                    System.out.println("Auction: "+messages);
                }
            }
        }
    } */
}

