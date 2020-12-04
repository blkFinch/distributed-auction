package Bank;

import java.sql.*;

import Database.DatabaseManager;
import Database.Tasks.CreateClient;
import Database.Tasks.UpdateClient;

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

    //Creates new record in Database
    public synchronized Client createClient(Client client){
        CreateClient cc = new CreateClient(client);
        System.out.println("creating client");
        cc.Execute();
        return client;
    }

    //Looks up Client by ID
    public synchronized  Client lookUpClient(int id) throws Exception {
        return Client.read(id);
    }

    public synchronized Client updateClient(Client client){
        UpdateClient uc = new UpdateClient(client);
        uc.Execute();
        System.out.println("Updated client id: " + client.getID());
        return client;
    }

    public synchronized int registerAuctionHouse(Client house) {
        houses.put(house.getID(), house);
        return house.getID();
    }

    public synchronized void depositInto(Client client, int amount){
        client.deposit(amount);
        updateClient(client);
    }

    public synchronized void blockFunds(Client client, int amount){
        client.holdFunds(amount);
    }

    public synchronized void releaseFunds(Client client, int amount){
        client.releaseFunds(amount);
    }

    public synchronized int withdrawFunds(Client client, int amount){
        if(client.releaseFunds(amount)){
            client.withdraw(amount);
            return amount;
        }else{
            return 0;
        }
    }

}
