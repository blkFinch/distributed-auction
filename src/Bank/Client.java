package Bank;

import Database.DatabaseManager;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.Statement;

/** Model class for the Client table in the database
 * -g. hutchison
 */
public class Client implements Serializable {
    private final int ID;
    private int portNumber;
    private String host;
    private double balance;
    private double heldFunds;
    private String name;
    private boolean isAuctionHouse = false;

    //<editor-fold desc="GETTERS SETTERS">
    public int getPort(){
        return portNumber;
    }

    public String getHost(){
        return host;
    }

    public double getBalance(){
        return this.balance;
    }
    public double getHeldFunds() { return this.heldFunds; }
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

    public int getID(){ return this.ID; }

    //</editor-fold>

    public Client(int id, int portNumber, String host,
                  int balance, String name, boolean isAuctionHouse){
        this.ID = id;
        this.portNumber = portNumber;
        this.host = host;
        this.balance = balance;
        this.name = name;
        this.isAuctionHouse = isAuctionHouse;
    }

    //ACCOUNT ACTIONS
    //

    /**
     * Deposit an amount of currency into this account.
     *
     * @param amount double
     */
    public void deposit(double amount) {
        balance += amount;
    }

    /**
     * Withdraw an amount of currency from this account. If there isn't enough
     * currency in the account, return false.
     * @param amount double
     * @return boolean
     */
    public boolean withdraw(double amount) {
        if (balance - amount < 0) {
            return false;
        } else {
            balance -= amount;
            return true;
        }
    }

    /**
     * Hold a given amount of currency, adding it to the total amount of funds
     * that is currently on hold. If there isn't enough currency to hold
     * then return false.
     * @param amount double
     * @return boolean
     */
    public boolean holdFunds(double amount) {
        if (balance - amount < 0) {
            return false;
        } else {
            balance -= amount;
            heldFunds += amount;
            return true;
        }
    }

    /**
     * Release the amount of currency from the hold total. If the amount to be
     * released is greater than the total held funds, return false.
     * @param amount double
     * @return boolean
     */
    public boolean releaseFunds(double amount) {
        if (heldFunds - amount < 0) {
            return false;
        } else {
            balance += amount;
            heldFunds -= amount;
            return true;
        }
    }
    //CRUD
    //TODO: extract
    public static Client read(int id) throws Exception {
        Statement statement = DatabaseManager.getConn().createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM clients WHERE id=" + id);
        Client client = new ClientBuilder()
                .setId(id)
                .setName(rs.getString("name"))
                .setBalance(rs.getInt("balance"))
                .setAuctionHouse(rs.getBoolean("isAuctionHouse"))
                .build();
        return client;
    }

}
