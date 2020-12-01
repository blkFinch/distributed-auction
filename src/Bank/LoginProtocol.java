package Bank;

import Database.Tasks.CreateClient;

import java.net.InetAddress;
import java.net.Socket;
import java.sql.SQLOutput;

import static Bank.BankServer.activeBank;

public class LoginProtocol {
    //STATE
    private static final int LOGIN = 0;
    private static final int NEW_USER = 1;
    private static final int init = 3;
    private int state = 3;

    private Socket clientSocket;

    public LoginProtocol(Socket clientSocket){
        this.clientSocket = clientSocket;
    }

    public String processInput(String input){
        String output = null;

        switch (state){
            case 0:
                if(Integer.parseInt(input) == 0){
                    output="Please create client: " +
                            "'name:isAuction(y or n):starting balance:open port(0 for null)'";
                    //ex: fred:n:600:0
                    state = 1;
                    //initialize as new client

                }
                else{
                    Client client = Bank.getClient(Integer.parseInt(input));
                    output = "hello "+ client.getName();
                }
                break;
            case 1:
                String[] clientString = input.split(":");

                Client newClient = createClinetFromString(clientString);
                if(newClient.save() != null){
                    output = "save success";
                }else{
                    output = "save failed";
                    state = 3;
                }
                break;
            case 3:
                output = "Please enter ID or enter 0 to create new";
                state = 0;
                break;
        }

        return output;
    }

    private Client createClinetFromString(String[] clientString) {
        String clientName = clientString[0];
        int clientPort = Integer.parseInt(clientString[3]);
        boolean auction = clientString[1].equals("y");
        InetAddress clientHost = clientSocket.getInetAddress();
        Client newClient = new Client(clientHost, clientPort);
        newClient.setName(clientName);
        newClient.setAuctionHouse(auction);
        newClient.setBalance(Integer.parseInt(clientString[2]));
        return newClient;
    }
}
