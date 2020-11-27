package Agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Agent{
    private static AgentProxy bankProxy;
    private static AgentProxy auctionProxy;

    public static void main(String[] args){
        BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
        String input;
        String type;
        String host;
        int port;
        boolean connected = false;

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
                bankProxy = new AgentProxy(type, host, port);
                connected = true;
            } catch(Exception e){
                System.out.println("Connection failed. Try again.");
            }
        }
        System.out.println("You are now connected.");
    }
}
