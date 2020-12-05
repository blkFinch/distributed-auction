package Database.Tasks;

import Database.DatabaseManager;

import java.sql.Connection;

/**
 * Any class that communicates with the database should implement this interface.
 * Because SQLite does not support concurrency, it should only be contacted by
 * one thread at time.
 * @param <V>
 */
public interface SQLInjector<V> {

    /**Should be a synchronized method so only one connection to db is
     * ever used at a time
    **/
    V inject() throws Exception;

}
