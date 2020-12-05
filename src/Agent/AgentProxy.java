package Agent;

import shared.BankMessages;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class AgentProxy implements Runnable{
    private String username;
    private int proxyType;
    private String hostIP;
    private int portNum;
    private String login;
    private Socket socket;
    private List<BankMessages> inMessages;
    private ObjectOutputStream out;
    private boolean running;

    public AgentProxy(String user, String type, String host, String port) throws IOException{
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
        inMessages = new ArrayList<>();
        running = true;
    }

    private void clientHandler(String host, int port) throws Exception{
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        BankMessages fromServer;
        BankMessages newUserRequest = new BankMessages.Builder()
                .command(BankMessages.Command.OPENACCOUNT)
                .accountName(username)
                .nullId();
        BankMessages getHouses = new BankMessages.Builder()
                .command(BankMessages.Command.GETHOUSES)
                .nullId();

        out.writeObject(newUserRequest);
        out.writeObject(getHouses);

        //while(running){
            fromServer = (BankMessages)in.readObject();
            System.out.println("Bank: " + fromServer.toString());
            inMessages.add(fromServer);
        //}
    }

    public void setLogin(String log){ login = log; }

    public synchronized void sendMessage(BankMessages message){
        try {
            out.writeObject(message);
        } catch(IOException e){
            System.out.println("Message failed to send");
        }
    }

    public synchronized String readMessages(){
        String messages = "";
        for(BankMessages mes : inMessages){ messages += (mes+"\n"); }
        inMessages.clear();
        return messages;
    }

    @Override
    public void run(){
        try{
            if(proxyType == 0 || proxyType == 1){
                clientHandler(hostIP, portNum);
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
