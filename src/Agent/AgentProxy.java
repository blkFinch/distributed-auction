package Agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class AgentProxy implements Runnable{
    private int proxyType;
    private String hostIP;
    private int portNum;
    private Socket socket;
    private List<String> inMessages;
    private List<String> outMessages;
    private boolean running;

    public AgentProxy(String type, String host, String port) throws IOException{
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
        outMessages = new ArrayList<>();
        running = true;
    }

    private void clientHandler(String host, int port) throws IOException{
        socket = new Socket(host, port);
        BufferedReader in = new BufferedReader(new
                InputStreamReader(socket.getInputStream()));
        BufferedReader keyboard = new BufferedReader(new
                InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        String fromServer;
        String fromUser;

        while(running){
            if(in.ready()){
                fromServer = in.readLine();
                System.out.println("Bank: " + fromServer);
                if(fromServer.equals("Bye.")){
                    running = false;
                    break;
                }
                else{ inMessages.add(fromServer); }
            }

            if(keyboard.ready()){
                if((fromUser=keyboard.readLine()) != null){
                    System.out.println("Client: " + fromUser);
                    out.println(fromUser);
                }
                else if(outMessages.size() > 0){
                    for(String mes : outMessages){ out.println(mes); }
                    outMessages.clear();
                }
            }
        }
    }

    public synchronized void sendMessage(String message){
        outMessages.add(message);
    }

    public synchronized String readMessages(){
        String messages = "";
        for(String mes : inMessages){ messages += (mes+"\n"); }
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
        } catch(IOException e){
            System.out.println("Connection failed");
        } finally{
            try{ socket.close(); }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
