package Database;

import java.sql.*;

/**
 * Public static methods for managing the database.
 */
public class DatabaseManager {
    //Where the database is saved locally
    public static final String DB_URL = "jdbc:sqlite:src/Database/bankDB.db";


    public static void createClientTable(){
        String sql = "CREATE TABLE IF NOT EXISTS clients (\n"
                + "     id integer PRIMARY KEY, \n"
                + "     name string, \n"
                + "     host string, \n"
                + "     port integer, \n"
                + "     isAuctionHouse boolean, \n"
                + "     balance integer DEFAULT 0 \n"
                + ");";
        executeSQL(sql);
        System.out.println("created table clients");
    }

    /**
     * ONLY use this if you really want that table gone
     * @param tableName the table you wish to destroy
     */
    public static void dropTable(String tableName){
        String sql = "DROP TABLE " + tableName +";";
        executeSQL(sql);
        System.out.println("dropped table " + tableName);
    }

    public static void executeSQL(String sql) {
        try(Connection connection = DriverManager.getConnection(DB_URL);
            Statement statement = connection.createStatement()) {
            statement.execute(sql);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
