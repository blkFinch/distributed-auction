package Bank;

import shared.Message;

public class Logger {
    public static boolean log = false;

    public static void logMessage(Message message){
        if(log){
            System.out.println(message.toString());
        }
    }
}