package Database;

import Database.Tasks.SQLInjector;

public class SyncInjector {
    private static SyncInjector active;

    public static SyncInjector getActive(){
        if(active == null){ active = new SyncInjector(); }
        return active;
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
