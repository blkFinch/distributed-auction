package Bank;

import java.sql.*;

import Database.DatabaseManager;
import Database.Tasks.CreateClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bank {
    public static Bank active;
    private final Map<Integer, Client> houses;
    ArrayList<Client> clients; //this will be list of auctionhouses
    private Bank(){
        clients = new ArrayList<Client>();
        houses = new HashMap<Integer, Client>();
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


    //TODO: error handling
    public synchronized Client getClient(int id){
        Client thisClient = null;
        try {
            thisClient = Client.read(id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return thisClient;
    }

    public synchronized List<Client> getHouses(){
        List<Client> houses = new ArrayList<Client>(this.houses.values());
        return houses;
    }

    //TODO: remove Create client task
    public synchronized Client createClient(Client client){
        CreateClient cc = new CreateClient(client);
        cc.Execute();
        return client;
    }

    public synchronized int registerAuctionHouse(Client house) {
        houses.put(house.getID(), house);
        return house.getID();
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
