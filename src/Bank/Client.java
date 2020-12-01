package Bank;

import Database.TaskRunner;
import Database.Tasks.CreateClient;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/** Model class for the Client table in the database
 * -g. hutchison
 */
public class Client {
    private int portNumber;
    private InetAddress host;
    private int balance;
    private String name;
    private boolean isAuctionHouse;

    //<editor-fold desc="GETTERS SETTERS">
    public int getPort(){
        return portNumber;
    }

    public InetAddress getHost(){
        return host;
    }

    public int getBalance(){
        return this.balance;
    }
    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAuctionHouse() {
        return isAuctionHouse;
    }

    public void setAuctionHouse(boolean auctionHouse) {
        isAuctionHouse = auctionHouse;
    }

    //</editor-fold>

    public Client(InetAddress host, int port){
        this.portNumber = port;
        this.host = host;
        this.balance = 0;
    }

    public Client save(){
        CreateClient createClient = new CreateClient(this);
        TaskRunner.jobQueue.offer(createClient);
        return this;
    }

    public static void create(){

    }

    public static void read(){

    }

    public static void update(){

    }

    public static void drestroy(){

    }
}
