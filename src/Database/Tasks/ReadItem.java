package Database.Tasks;

import Bank.Client;
import Bank.ClientBuilder;
import Database.DatabaseManager;
import shared.Items.Item;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ReadItem implements  SQLInjector{
    private final int id;

    public ReadItem(int id) {
        this.id = id;
    }

    public Item inject() throws Exception {
        Connection DBconn = DatabaseManager.getConn();
        Statement statement = DBconn.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM items WHERE id=" + id);

        String name = rs.getString("name");
        String desc = rs.getString("desc");
        int minBid = rs.getInt("minBid");
        int id = this.id;

        Item item = new Item(name,desc,minBid,id);

        return item;
    }
}
