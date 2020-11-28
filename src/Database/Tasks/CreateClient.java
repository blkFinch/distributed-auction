package Database.Tasks;

import Bank.Client;
import Database.DatabaseManager;
import Database.Task;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreateClient extends Task {
    private Client client;
    public CreateClient(Client client){
        this.client =client;
    }
    @Override
    public void Execute() {
        String sql = "INSERT INTO clients " +
                "   (host, port, balance, isAuctionHouse, name) \n" +
                "   VALUES (?,?,?,?,?);";

        try(PreparedStatement pstmt = DatabaseManager.getConn().prepareStatement(sql)){
            pstmt.setString(1, String.valueOf(client.getHost()));
            pstmt.setInt(2, client.getPort());
            pstmt.setInt(3, client.getBalance());
            //DEBUG: dummy values for now
            pstmt.setBoolean(4, true);
            pstmt.setString(5, client.getName());

            pstmt.execute();
        }catch (SQLException throwables){
            throwables.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}