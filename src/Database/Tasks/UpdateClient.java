package Database.Tasks;

import Bank.Client;
import Database.DatabaseManager;
import Database.Task;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateClient extends Task {
    private Client client;
    public UpdateClient(Client client){
        this.client = client;
    }

    @Override
    public int Execute() {
        String sql = "UPDATE clients SET" +
                " host = ?, port = ?, balance = ?, isAuctionHouse = ?, name = ? " +
                " WHERE id = ?;";

        try(PreparedStatement pstmt = DatabaseManager.getConn().prepareStatement(sql)){
            pstmt.setString(1, String.valueOf(client.getHost()));
            pstmt.setInt(2, client.getPort());
            pstmt.setInt(3, (int)client.getBalance());
            //DEBUG: dummy values for now
            pstmt.setBoolean(4, true);
            pstmt.setInt(5, client.getID());

            pstmt.execute();
        }catch (SQLException throwables){
            throwables.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 1;
    }
}
