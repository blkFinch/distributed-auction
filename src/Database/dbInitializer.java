package Database;

public class dbInitializer {

    /**
     * Utility function for initializing the client table. This should
     *only be called once for set up.
     */
    private static void createClientTable(){
        String sql = "CREATE TABLE IF NOT EXISTS clients (\n"
                + "     id integer PRIMARY KEY, \n"
                + "     name string, \n"
                + "     host string, \n"
                + "     port integer, \n"
                + "     isAuctionHouse boolean, \n"
                + "     balance integer DEFAULT 0 \n"
                + ");";
        DatabaseManager.executeSQL(sql);
        System.out.println("created table clients");
    }

    private  static void  createItemTable(){
        String sql = "CREATE TABLE IF NOT EXISTS items (\n"
                + "     id integer PRIMARY KEY, \n"
                + "     name string, \n"
                + "     description string, \n"
                + "     minimum integer, \n"
                + ");";
        DatabaseManager.executeSQL(sql);
        System.out.println("created table items");
    }

    public static void main(String[] args) throws ClassNotFoundException {
        createClientTable();
        createItemTable();
    }
}
