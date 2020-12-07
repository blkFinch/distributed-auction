package Database;

import Auction.Item;
import Database.Tasks.CreateItem;
import shared.ItemFaker;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;


public class dbInitializer {


    private static void createItemTable(){
        String sql = "CREATE TABLE IF NOT EXISTS items (\n"
                + "     id integer PRIMARY KEY, \n"
                + "     ownerId integer, \n"
                + "     name string, \n"
                + "     desc string, \n"
                + "     minBid integer DEFAULT 0 \n"
                + ");";
        DatabaseManager.executeSQL(sql);
        System.out.println("created table items");
    }

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

    public static void main(String[] args) throws ClassNotFoundException {
        createClientTable();
        createItemTable();
        populateItemTable(200);
    }

    private static void populateItemTable(int entries){

        for(int i = 0; i < entries; i++){
            Item item = ItemFaker.randomizedPokeon();
            System.out.println("Adding item: " + item.getName());
            CreateItem ci = new CreateItem(item);
            SyncInjector.getActive().setInjection(ci);
            FutureTask task = new FutureTask<>(SyncInjector.getActive());
            Thread t = new Thread(task);
            t.start();
            try {
                while(task.get() == null);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }
    }

}
