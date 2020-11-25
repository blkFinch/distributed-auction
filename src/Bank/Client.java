package Bank;

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

    public Client save(Connection conn){
        String sql = "INSERT INTO clients " +
                "   (host, port, balance, isAuctionHouse, name) \n" +
                "   VALUES (?,?,?,?,?);";

        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, String.valueOf(this.host));
            pstmt.setInt(2, this.portNumber);
            pstmt.setInt(3, this.balance);
            //DEBUG: dummy values for now
            pstmt.setBoolean(4, true);
            pstmt.setString(5, "my auction house");

            pstmt.execute();
        }catch (SQLException throwables){
            throwables.printStackTrace();
        }

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
