package Bank;

import Database.DatabaseManager;

import java.net.InetAddress;
import java.sql.ResultSet;
import java.sql.Statement;

/** Model class for the Client table in the database
 * -g. hutchison
 */
public class Client {
    private final int ID;
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

    public Client(int id){
        this.ID = id;
    }

    public Client(int id, int portNumber, InetAddress host,
                  int balance, String name, boolean isAuctionHouse){
        this.ID = id;
        this.portNumber = portNumber;
        this.host = host;
        this.balance = balance;
        this.name = name;
        this.isAuctionHouse = isAuctionHouse;
    }

    public static Client create(){
        return null;
    }

    public static Client read(int id){
        //TODO: extract
        try {
            Statement statement = DatabaseManager.getConn().createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM clients WHERE id=" + id);
            Client client = new ClientBuilder()
                    .setId(id)
                    .setName(rs.getString("name"))
                    .setBalance(rs.getInt("balance"))
                    .setAuctionHouse(rs.getBoolean("isAuctionHouse"))
                    .build();
            return client;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Client save(){
        return null;
    }

    public static Client destroy(){
        return null;
    }

}
