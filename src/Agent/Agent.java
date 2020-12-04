package Agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Agent{
    private static AgentProxy bankProxy;
    private static List<AgentProxy> auctionProxies;
    private List<String> auctionNames;

    public Agent(String host, String port, String login) throws Exception{
        auctionNames = new ArrayList<>();
        bankProxy = new AgentProxy("bank", host, port);
        bankProxy.setLogin(login);
        bankProxy.run();
    }

    public void sendBankMessage(String message){ bankProxy.sendMessage(message); }

    public void sendAuctionMessage(int ind, String message){
        auctionProxies.get(ind).sendMessage(message);
    }

    public List<String> getAuctionNames(){ return auctionNames; }

    public static void main(String[] args){
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
                    bankProxy = new AgentProxy(type, host, ""+port);
                    bankProxy.run();
                }
                else{
                    auctionProxies.add(new AgentProxy(type, host, ""+port));
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
    }
}

