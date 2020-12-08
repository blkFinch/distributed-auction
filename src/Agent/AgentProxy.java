package Agent;

import shared.A_AH_Messages;
import shared.ConnectionReqs;
import shared.Message;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/******************************************************************************
 * Ashley Krattiger                                                           *
 *                                                                            *
 * AgentProxy                                                                 *
 *                                                                            *
 * Extends Thread                                                             *
 *                                                                            *
 * This class handles communicating with a server and passing messages to and *
 * from the Agent class.                                                      *
 *****************************************************************************/
public class AgentProxy extends Thread{
    /**************************************************************************
     * Global Variables                                                       *
     *                                                                        *
     * username - String associated with this Agent's account                 *
     * userID - int associated with this Agent's account                      *
     * initBal - initial balance to be added to a new Bank account            *
     * proxyType - int signifying whether the proxy is a Bank Proxy (0) or an *
     *             Auction Proxy (1)                                          *
     * hostIP - String holding the IP address of the server                   *
     * portNum - int holding the open port of the server                      *
     * newAccount - boolean that is true if a new account needs to be         *
     *              registered with the Bank                                  *
     * socket - socket used to communicate with the server                    *
     * auctionName - String holding the name of the Auction House the proxy is*
     *               communicating with                                       *
     * inMessages - List of messages read in from the Bank server             *
     * aucInMessages - List of messages read in from the Auction server       *
     * newConnections - List of ConnectionReqs for all open Auction Houses    *
     *                  passed in from the Bank                               *
     * out - ObjectOutputStream that sends messages to the server             *
     * running - boolean that is true if the connection is still open         *
     *************************************************************************/
    private final String username;
    private int userID;
    private int initBal;
    private int proxyType;
    private final String hostIP;
    private int portNum;
    private final boolean newAccount;
    private Socket socket;
    private String auctionName;
    private final List<Message> inMessages;
    private final List<A_AH_Messages> aucInMessages;
    private final List<ConnectionReqs> newConnections;
    private ObjectOutputStream out;
    private boolean running;

    /**************************************************************************
     * Constructor - initializes global variables                             *
     *                                                                        *
     * @param user - username for this Agent's account                        *
     * @param type - type of proxy (either "bank" or "auction")               *
     * @param host - server's IP address                                      *
     * @param port - server's open port number                                *
     * @param newAcc - boolean that's true if a new Bank account needs to be  *
     *                 opened                                                 *
     *************************************************************************/
    public AgentProxy(String user, String type, String host, String port,
                      boolean newAcc){
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
        aucInMessages = new ArrayList<>();
        newConnections = new ArrayList<>();
        running = true;
    }

    /**************************************************************************
     * bankHandler                                                            *
     *                                                                        *
     * Opens socket, sends initial message to server, handles reading input   *
     * from the server.                                                       *
     *                                                                        *
     * Takes no arguments, returns nothing                                    *
     * @throws Exception if there is a problem connecting to the server       *
     *                                                                        *
     * Variables:                                                             *
     * in - ObjectInputStream that receives messages from the server          *
     * fromServer - Message that holds each read in message                   *
     * loginRequest - Message sent upon opening connection to the Bank. Either*
     *                asks to log in or create a new account                  *
     *************************************************************************/
    private void bankHandler() throws Exception{
        socket = new Socket(hostIP, portNum);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        Message fromServer;
        Message loginRequest;

        if(newAccount){
            loginRequest = new Message.Builder()
                    .command(Message.Command.OPENACCOUNT)
                    .accountName(username)
                    .balance(initBal)
                    .nullId();
        }
        else{
            loginRequest = new Message.Builder()
                    .command(Message.Command.LOGIN)
                    .senderId(Integer.parseInt(username));
        }

        System.out.println("Agent: " + loginRequest.toString());
        out.writeObject(loginRequest);

        while(running){
            fromServer = (Message)in.readObject();
            if(fromServer.getCommand() == Message.Command.DEREGISTER){
                running = false;
            }
            if(fromServer.getConnectionReqs() != null){
                newConnections.addAll(fromServer.getConnectionReqs());
                if(fromServer.getResponse() != null){
                    System.out.println("Bank: " + fromServer.toString());
                    inMessages.add(fromServer);
                }
            }
            else{
                System.out.println("Bank: " + fromServer.toString());
                inMessages.add(fromServer);
            }
        }
    }

    /**************************************************************************
     * auctionHandler                                                         *
     *                                                                        *
     * Opens socket, sends initial message to server, handles reading input   *
     * from the server.                                                       *
     *                                                                        *
     * Takes no arguments, returns nothing                                    *
     * @throws Exception if there is a problem connecting to the server       *
     *                                                                        *
     * Variables:                                                             *
     * in - ObjectInputStream that receives messages from the server          *
     * fromServer - A_AH_Messages that holds each read in message             *
     * loginRequest - A_AH_Messages sent upon opening connection to the       *
     *                Auction                                                 *
     *************************************************************************/
    private void auctionHandler() throws Exception{
        System.out.println("Host: "+hostIP+" Port");
        socket = new Socket(hostIP, portNum);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        A_AH_Messages fromServer;
        A_AH_Messages register = new A_AH_Messages.Builder()
                .topic(A_AH_Messages.A_AH_MTopic.REGISTER)
                .accountId(userID)
                .build();

        System.out.println("Agent: " + register.toString());
        out.writeObject(register);

        while(running){
            fromServer = (A_AH_Messages) in.readObject();
            System.out.println("Auction: " + fromServer.toString());
            if(fromServer.getTopic() == A_AH_Messages.A_AH_MTopic.DEREGISTER){
                running = false;
            }
            aucInMessages.add(fromServer);
        }
    }

    /**************************************************************************
     * setUserID - Sets global variable userID                                *
     *                                                                        *
     * @param id - int to assign to userID                                    *
     * Returns nothing                                                        *
     *************************************************************************/
    public void setUserID(int id){ userID = id; }

    /**************************************************************************
     * setInitBal - Sets global variable initBal                              *
     *                                                                        *
     * @param bal - int to assign to initBal                                  *
     * Returns nothing                                                        *
     *************************************************************************/
    public void setInitBal(int bal){ initBal = bal; }

    /**************************************************************************
     * setAuctionName - Sets global variable auctionName                      *
     *                                                                        *
     * @param name - String to assign to auctionName                          *
     * Returns nothing                                                        *
     *************************************************************************/
    public void setAuctionName(String name){ auctionName = name; }

    /**************************************************************************
     * getAuctionName - Gets global variable auctionName                      *
     *                                                                        *
     * Takes no arguments                                                     *
     * @return auctionName                                                    *
     *************************************************************************/
    public String getAuctionName(){ return auctionName; }

    /**************************************************************************
     * sendBankMessage                                                        *
     *                                                                        *
     * Sends a message to the Bank server                                     *
     *                                                                        *
     * @param message - Message to be sent to the Bank                        *
     * Returns nothing                                                        *
     *************************************************************************/
    public synchronized void sendBankMessage(Message message){
        try {
            if(message.getCommand() != Message.Command.GETHOUSES){
                System.out.println("Agent: " + message.toString());
            }
            out.writeObject(message);
        } catch(IOException e){
            System.out.println("Message failed to send");
        }
    }

    /**************************************************************************
     * sendAuctionMessage                                                     *
     *                                                                        *
     * Sends a message to the Auction server                                  *
     *                                                                        *
     * @param message - Message to be sent to the Auction                     *
     * Returns nothing                                                        *
     *************************************************************************/
    public synchronized void sendAuctionMessage(A_AH_Messages message){
        try {
            System.out.println("Agent: "+message.toString());
            out.writeObject(message);
        } catch(IOException e){
            System.out.println("Message failed to send");
        }
    }

    /**************************************************************************
     * readBankMessages                                                       *
     *                                                                        *
     * Copies the messages from global variable inMessages and clears it out  *
     *                                                                        *
     * Takes no arguments                                                     *
     * @return contents from inMessages in a new List                         *
     *                                                                        *
     * Variable: messages - new List for the Messages                         *
     *************************************************************************/
    public synchronized List<Message> readBankMessages(){
        List<Message> messages = new ArrayList<>(inMessages);
        inMessages.clear();
        return messages;
    }

    /**************************************************************************
     * readAuctionMessages                                                    *
     *                                                                        *
     * Copies the messages from global variable aucInMessages and clears it   *
     *                                                                        *
     * Takes no arguments                                                     *
     * @return contents from aucInMessages in a new List                      *
     *                                                                        *
     * Variable: messages - new List for the A_AH_Messages                    *
     *************************************************************************/
    public synchronized List<A_AH_Messages> readAuctionMessages(){
        List<A_AH_Messages> messages = new ArrayList<>(aucInMessages);
        aucInMessages.clear();
        return messages;
    }

    /**************************************************************************
     * getNewConnections                                                      *
     *                                                                        *
     * Copies the ConnectionReqs from global variable newConnections and      *
     * clears it                                                              *
     *                                                                        *
     * Takes no arguments                                                     *
     * @return contents from newConnections in a new List                     *
     *                                                                        *
     * Variable: messages - new List for the ConnectionReqs                   *
     *************************************************************************/
    public List<ConnectionReqs> getNewConnections(){
        List<ConnectionReqs> connList = new ArrayList<>(newConnections);
        newConnections.clear();
        return connList;
    }

    /**************************************************************************
     * run                                                                    *
     * Overridden from Thread                                                 *
     *                                                                        *
     * The process that runs when the AgentProxy Thread is Started            *
     *                                                                        *
     * Takes no arguments, returns nothing                                    *
     *************************************************************************/
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
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
