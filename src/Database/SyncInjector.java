package Database;

import Database.Tasks.SQLInjector;

import java.util.concurrent.Callable;

public class SyncInjector implements Callable {
    private static SyncInjector active;
    private SQLInjector injection;

    public static SyncInjector getActive(){
        if(active == null){ active = new SyncInjector(); }
        return active;
    }

    public void setInjection(SQLInjector injection) {
        this.injection = injection;
    }

    public Object call(){
        return executeInjection(injection);
    }

    /**
     * This syncronized method should be the only method accessing the DB
     * to prevent concurrent queries
     * @return
     */
    public synchronized Object executeInjection(SQLInjector injection){
        System.out.println("Executing injection : " + injection.getClass() );
        try {
            return injection.inject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
