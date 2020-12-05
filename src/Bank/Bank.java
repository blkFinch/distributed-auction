package Bank;

import Database.Tasks.CreateClient;
import Database.Tasks.UpdateClient;
import shared.ConnectionReqs;
import shared.DBMessage;
import shared.Message;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLOutput;
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

    //TODO: refactor to DB comms
    public synchronized Client getClient(int id){

        Client thisClient = null;
        DBMessage req = new DBMessage.Builder()
                .command(DBMessage.Command.GET)
                .table(DBMessage.Table.CLIENT)
                .accountId(id)
                .build();

        DBMessage response = sendToDB(req);
        if(response.getResponse() == Message.Response.SUCCESS){
            thisClient = (Client) response.getPayload();
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
        DBMessage req = new DBMessage.Builder()
                                    .command(DBMessage.Command.PUT)
                                    .table(DBMessage.Table.CLIENT)
                                    .payload(client)
                                    .senderId(0)
                                    .build();
       DBMessage res = sendToDB(req);

       if(res.getResponse() == Message.Response.SUCCESS){
           return res.getAccountId();
       }
       return -999;
    }

    public synchronized Client updateClient(Client client){
        UpdateClient uc = new UpdateClient(client);
        try {
            uc.inject();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    //DATABASE CALLS

    private DBMessage sendToDB(DBMessage req){
        try {
            Socket dbSocket = new Socket("localhost", 6002);

            ObjectOutputStream out = new ObjectOutputStream(dbSocket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(dbSocket.getInputStream());

            out.writeObject(req);

            DBMessage res = (DBMessage) in.readObject();
            return res;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
