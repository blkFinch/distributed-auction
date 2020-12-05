package Database.Tasks;

import Bank.Client;
import Database.DatabaseManager;

import java.sql.*;

public class CreateClient implements SQLInjector<Integer> {
    private final Client client;

    public CreateClient(Client client){
        this.client =client;
    }

    /**
     * Injects the new client into database
     * @return The client unique ID
     * @throws Exception if unable to connect to DB
     */
    @Override
    public synchronized Integer inject() throws Exception {
        Connection dbConn = DatabaseManager.getConn();
        int id = 999; //default to bad id
        String sql = "INSERT INTO clients " +
                "   (host, port, balance, isAuctionHouse, name) \n" +
                "   VALUES (?,?,?,?,?);";

        try(PreparedStatement pstmt = dbConn
                                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ){
            pstmt.setString(1, String.valueOf(client.getHost()));
            pstmt.setInt(2, client.getPort());
            pstmt.setInt(3, (int)client.getBalance());
            pstmt.setBoolean(4, client.isAuctionHouse());
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
        dbConn.close();
        return id;
    }
}
