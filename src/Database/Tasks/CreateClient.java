package Database.Tasks;

import Bank.Client;
import Database.DatabaseManager;
import Database.Task;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateClient extends Task {
    private Client client;

    public CreateClient(Client client){

        this.client =client;
    }

    @Override
    public int Execute() {
        int id = 999; //default to bad id
        String sql = "INSERT INTO clients " +
                "   (host, port, balance, isAuctionHouse, name) \n" +
                "   VALUES (?,?,?,?,?);";

        try(PreparedStatement pstmt =
                    DatabaseManager.getConn()
                                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ){
            pstmt.setString(1, String.valueOf(client.getHost()));
            pstmt.setInt(2, client.getPort());
            pstmt.setInt(3, (int)client.getBalance());
            //DEBUG: dummy values for now
            pstmt.setBoolean(4, true);
            pstmt.setString(5, client.getName());

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
