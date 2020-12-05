package Database.Tasks;

import Bank.Client;
import Bank.ClientBuilder;
import Database.DatabaseManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ReadClient implements SQLInjector {
    private final int id;

    public ReadClient(int id) {
        this.id = id;
    }

    public Client inject() throws Exception {
        Connection DBconn = DatabaseManager.getConn();
        Statement statement = DBconn.createStatement();
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
