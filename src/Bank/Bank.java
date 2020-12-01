package Bank;

import Database.DatabaseManager;
import Database.TaskRunner;
import Database.Tasks.CreateClient;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class Bank {
    //may be deprecated shift a sepreate table in db
    public static Bank active;

    ArrayList<Client> clients; //this will be list of auctionhouses
    public Bank(){
        clients = new ArrayList<Client>();
    }


    public int getBalance(Client client){
        int balance = 0;
        //return balance of client by id.
        return balance;
    }

    public static Client getClient(int id){
        Client thisClient = null;
        //lookup client by id
        return thisClient;
    }

    public void depositInto(Client client, int amount){
        //lookup client and add funds
    }

    public void blockFunds(Client client, int amount){
        //move funds into blockedFunds column
    }

    public void releaseFunds(Client client){
        //releases blocked funds from client
    }

    public int withdrawBlockedFunds(Client client){
        int withdrawl = 0;
        //returns blocked funds
        return withdrawl;
    }

}
