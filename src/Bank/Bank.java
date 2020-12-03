package Bank;

import java.sql.*;

import Database.DatabaseManager;
import Database.Tasks.CreateClient;

import java.util.ArrayList;

public class Bank {
    public static Bank active;

    ArrayList<Client> clients; //this will be list of auctionhouses
    private Bank(){
        clients = new ArrayList<Client>();
    }

    public static Bank getActive(){
        if(active == null){active = new Bank();}
        return active;
    }


    public int getBalance(Client client){
        int balance = 0;
        //return balance of client by id.
        return balance;
    }

    public synchronized Client getClient(int id){
        Client thisClient = Client.read(id);


        return thisClient;
    }

    public synchronized Client createClient(Client client){
        CreateClient cc = new CreateClient(client);
        cc.Execute();
        return client;
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
