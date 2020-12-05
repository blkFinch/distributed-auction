package Database.Tasks;

import Bank.Client;
import Database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateClient implements SQLInjector {
    private Client client;
    public UpdateClient(Client client){
        this.client = client;
    }

    @Override
    public synchronized Integer inject() throws Exception {
        Connection DBconn = DatabaseManager.getConn();
        String sql = "UPDATE clients SET" +
                " host = ?, port = ?, balance = ?, isAuctionHouse = ?, name = ? " +
                " WHERE id = ?;";

        try(PreparedStatement pstmt = DBconn.prepareStatement(sql)){
            pstmt.setString(1, String.valueOf(client.getHost()));
            pstmt.setInt(2, client.getPort());
            pstmt.setInt(3, (int)client.getBalance());
            //DEBUG: dummy values for now
            pstmt.setBoolean(4, client.isAuctionHouse());
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
