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
        Client thisClient = null;
        //TODO: extract
        try {
            Statement statement = DatabaseManager.getConn().createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM clients WHERE id=" + id);
            System.out.println(rs.getString("name"));
        } catch (Exception e) {
            e.printStackTrace();
        }

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
=======
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
