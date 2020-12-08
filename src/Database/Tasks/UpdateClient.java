package Database.Tasks;

import Bank.Client;
import Database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * SQLInjector to update client in the database. Given a client
 * this will generate a statement to update that client in DB
 */
public class UpdateClient implements SQLInjector {
    private Client client;
    public UpdateClient(Client client){
        this.client = client;
    }

    /**
     * The injection to be used by the single threaded SyncInjector
     * to prevent stepping on the database
     * @return
     * @throws Exception
     */
    @Override
    public Integer inject() throws Exception {
        Connection DBconn = DatabaseManager.getConn();
        System.out.println(client.toString());
        String sql = "UPDATE clients SET" +
                " host = ?, port = ?, balance = ?, isAuctionHouse = ?, name = ? " +
                " WHERE id = ?;";

        try(PreparedStatement pstmt = DBconn.prepareStatement(sql)){
            pstmt.setString(1, String.valueOf(client.getHost()));
            pstmt.setInt(2, client.getPort());
            pstmt.setInt(3, (int)client.getBalance());
            pstmt.setBoolean(4, client.isAuctionHouse());
            pstmt.setString(5,client.getName());
            pstmt.setInt(6, client.getID());

            int numRows = pstmt.executeUpdate();

            System.out.println( "Update modified " + numRows + " rows." );
        }catch (SQLException throwables){
            throwables.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 1;
    }
}
