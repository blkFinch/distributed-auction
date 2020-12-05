package Bank;

import Database.Tasks.CreateClient;
import Database.Tasks.UpdateClient;
import shared.ConnectionReqs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Bank class handles logic for managing blocking and moving funds between
 * accounts. Bank is a singleton and holds the only methods that access the
 * database. This is to prevent concurrency errors.
 */
public class Bank {
    public static Bank active;
    private final Map<Integer, Client> houses;
    ArrayList<Client> clients;

    private Bank(){
        clients = new ArrayList<Client>();
        houses = new HashMap<Integer, Client>();
    }

    /**
     * Singleton Bank instance
     * @return Bank Singleton
     */
    public static Bank getActive(){
        if(active == null){active = new Bank();}
        return active;
    }


    public synchronized Client getClient(int id){
        Client thisClient = null;
        try {
            thisClient = Client.read(id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return thisClient;
    }

    public synchronized List<ConnectionReqs> getHouses(){
        List<ConnectionReqs> reqs = new ArrayList<ConnectionReqs>();
        for(Client house : houses.values()){
            ConnectionReqs req = new ConnectionReqs(house.getHost(), house.getPort());
            req.setName(house.getName());
            reqs.add(req);
        }
        return reqs;
    }

    //Creates new record in Database
    public synchronized int createClient(Client client) {
        CreateClient cc = new CreateClient(client);
        System.out.println("creating client");
        try {
            return cc.inject();
        } catch (Exception e) {
            System.out.println("Error connected to DB");
            e.printStackTrace();
        }
        return -999;
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
