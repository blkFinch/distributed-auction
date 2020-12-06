package Agent;

import shared.ConnectionReqs;
import shared.Message;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class AgentProxy extends Thread{
    private String username;
    private int initBal;
    private int proxyType;
    private String hostIP;
    private int portNum;
    private boolean newAccount;
    private Socket socket;
    private List<Message> inMessages;
    private List<ConnectionReqs> newConnections;
    private ObjectOutputStream out;
    private boolean running;

    public AgentProxy(String user, String type, String host, String port, boolean newAcc) throws IOException{
        username = user;
        if(type.equals("bank")){ proxyType = 0; }
        else if(type.equals("auction")){ proxyType = 1; }
        else{ proxyType = -1; }
        hostIP = host;
        try {
            portNum = Integer.parseInt(port);
        } catch(Exception e){
            proxyType = -1;
        }
        newAccount = newAcc;
        inMessages = new ArrayList<>();
        newConnections = new ArrayList<>();
        running = true;
    }

    private void bankHandler() throws Exception{
        socket = new Socket(hostIP, portNum);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        Message fromServer;
        Message loginRequest;
        Message getHouses = new Message.Builder()
                .command(Message.Command.GETHOUSES)
                .nullId();

        if(newAccount){
            loginRequest = new Message.Builder()
                    .command(Message.Command.OPENACCOUNT)
                    .accountName(username)
                    .cost(initBal)
                    .nullId();
        }
        else{
            loginRequest = new Message.Builder()
                    .command(Message.Command.LOGIN)
                    .senderId(Integer.parseInt(username));
        }

        System.out.println("Server: " + loginRequest.toString());
        out.writeObject(loginRequest);

        while(running){
            fromServer = (Message)in.readObject();
            System.out.println("Bank: " + fromServer.toString());
            if(fromServer.getConnectionReqs() != null){
                newConnections.addAll(fromServer.getConnectionReqs());
            }
            else{
                inMessages.add(fromServer);
            }
        }
    }

    private void auctionHandler() throws Exception{
        socket = new Socket(hostIP, portNum);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        Message fromServer;

        while(running){
            fromServer = (Message)in.readObject();
            System.out.println("Bank: " + fromServer.toString());
            inMessages.add(fromServer);
        }
    }

    public void setInitBal(int bal){ initBal = bal; }

    public synchronized void sendMessage(Message message){
        try {
            out.writeObject(message);
        } catch(IOException e){
            System.out.println("Message failed to send");
        }
    }

    public synchronized String readMessages(){
        String messages = "";
        for(Message mes : inMessages){ messages += (mes+"\n"); }
        inMessages.clear();
        return messages;
    }

    public List<ConnectionReqs> getNewConnections(){
        List<ConnectionReqs> connList = new ArrayList<>(newConnections);
        newConnections.clear();
        return connList;
    }

    @Override
    public void run(){
        try{
            if(proxyType == 0){
                bankHandler();
            } else if (proxyType == 1){
                auctionHandler();
            }
            else{ System.out.println("Connection failed."); }
        } catch(Exception e){
            System.out.println("Connection failed");
        } finally{
            try{ socket.close(); }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
