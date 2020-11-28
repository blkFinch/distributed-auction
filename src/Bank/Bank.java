package Bank;

import Database.DatabaseManager;

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

    public static void addNewClient(InetAddress host, int portnumber, String name){
        Client newClient = new Client(host, portnumber);
        newClient.setName(name);
        System.out.println("added a new client at " + newClient.getHost() + " : " + newClient.getPort());
        System.out.println("balance of " + newClient.getBalance());

        //TODO: send this to task manager
        try(Connection conn = DatabaseManager.getConn()){
            newClient.save(conn);
            System.out.println("client saved to database");
        }catch (SQLException throwables){
            throwables.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
