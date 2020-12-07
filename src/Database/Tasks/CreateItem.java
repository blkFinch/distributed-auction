package Database.Tasks;

import Auction.Item;
import Database.DatabaseManager;

import java.sql.*;

public class CreateItem implements SQLInjector<Integer> {
    private final Item item;

    public CreateItem(Item item){ this.item = item; }

    @Override
    public Integer inject() throws Exception {
        Connection dbConn = DatabaseManager.getConn();
        int id = 999; //default to bad id
        String sql = "INSERT INTO items " +
                "   (name, desc, minBid) \n" +
                "   VALUES (?,?,?);";

        try(PreparedStatement pstmt = dbConn
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ){
            pstmt.setString(1, item.getName());
            pstmt.setString(2, item.getDescription());
            pstmt.setInt(3, item.getMinimumBid());

            pstmt.execute();

            //GET GENERATED KEY
            ResultSet key = pstmt.getGeneratedKeys();
            id = key.getInt(1);
        }catch (SQLException throwables){
            throwables.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return id;
    }
}
