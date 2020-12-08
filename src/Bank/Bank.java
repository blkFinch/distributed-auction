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
        clients = new ArrayList<Client>(); //All Active users including AH and Agents
        houses = new HashMap<Integer, Client>(); //Active AH
    }

    /**
     * Singleton Bank instance
     * @return Bank Singleton
     */
    public static Bank getActive(){
        if(active == null){active = new Bank();}
        return active;
    }


    //SESSION METHODS
    //

    /**
     * Gets a list of active AuctionHouses and their Connection Info
     * @return List of ConnectionReqs
     */

    public void loginUser(Client user){
        System.out.println(
                "logging in user :  " + user.getID() +":" + user.getName()
        );
        if(user.isAuctionHouse()){
            houses.put(user.getID(), user);
        }
        clients.add(user);
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

    public synchronized int registerAuctionHouse(Client house) {
        houses.put(house.getID(), house);
        clients.add(house);
        return house.getID();
    }

    public Client findActiveClient(int id){
        for (Client client: clients) {
            System.out.println("checking " + client.getID());
            if (client.getID() == id){ return client;}
        }
        return null;
    }

    public void deregisterUser(Client user){
        if(user.isAuctionHouse()){
            houses.remove(user.getID());
        }

        clients.remove(user);
    }


    //MONEY HANDLING
    //

    /**
     * Deposits money into client balance and updates DB
     * @param client
     * @param amount
     */
    public synchronized void depositInto(Client client, int amount){
        client.deposit(amount);
        updateClient(client);
    }

    /**
     * Withdraws money and updates DB
     * @param client
     * @param amount
     * @return the amount withdrawn
     */
    public synchronized int withdrawFunds(Client client, int amount){
        if(client.releaseFunds(amount)){
            client.withdraw(amount);
            updateClient(client);
            return amount;
        }else{
            return 0;
        }
    }

    //DATABASE CALLS
    //
    /**
     * Makes a POST request to DB with Client to be created
     * @param client
     * @return
     */
    public synchronized int createClient(Client client) {
        DBMessage req = new DBMessage.Builder()
                .command(DBMessage.Command.PUT)
                .table(DBMessage.Table.CLIENT)
                .payload(client)
                .senderId(0)
                .build();
        DBMessage res = sendToDB(req);
        System.out.println(res.toString());
        if(res.getResponse() == Message.Response.SUCCESS){
            return res.getAccountId();
        }
        return -999;
    }

    /**
     * Updats client record in DB
     * @param client
     * @return Client that was just updated
     */
    public synchronized Client updateClient(Client client){
        DBMessage req = new DBMessage.Builder()
                .command(DBMessage.Command.UPDATE)
                .table(DBMessage.Table.CLIENT)
                .payload(client)
                .build();

        DBMessage response = sendToDB(req);

        if(response.getResponse() == Message.Response.SUCCESS){
            return client;
        }
        return null;
    }

    /**
     * Makes a request to DB to look up client by ID
     * @param id the unique ID of the client
     * @return the Client, null if error
     */
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
