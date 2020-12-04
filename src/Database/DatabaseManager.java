package Database;

import java.sql.*;

/**
 * Public static methods for managing the database.
 */
public class DatabaseManager {
    //Where the database is saved locally
    public static final String DB_URL = "jdbc:sqlite:src/Database/bankDB.db";
    private static Connection conn = null;

    public static synchronized Connection getConn() throws Exception {
        if(conn == null){
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(DB_URL);
        }
        return conn;
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
        try(Connection connection = getConn();
            Statement statement = connection.createStatement()) {
            statement.execute(sql);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
