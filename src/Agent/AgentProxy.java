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

    public AgentProxy(String type, String host, int port) throws IOException{
        if(type.equals("bank")){ proxyType = 0; }
        else if(type.equals("auction")){ proxyType = 1; }
        else{ proxyType = -1; }
        hostIP = host;
        portNum = port;
        inMessages = new ArrayList<>();
        outMessages = new ArrayList<>();
        running = true;
    }

    private void bankClient(String host, int port) throws IOException{
        socket = new Socket(host, port);
        BufferedReader in = new BufferedReader(new
                InputStreamReader(socket.getInputStream()));
        BufferedReader keyboard = new BufferedReader(new
                InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        String fromServer;

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
        }
    }

    private void auctionClient(String host, int port) throws IOException{
        socket = new Socket(host, port);
    }

    public void sendMessage(String message){ outMessages.add(message); }

    @Override
    public void run(){
        try{
            if(proxyType == 0){ bankClient(hostIP, portNum); }
            else if(proxyType == 1){ auctionClient((hostIP, portNum)); }
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
