package Bank;

import shared.Message;

public class Logger {
    private final boolean log = true;

    public static void logMessage(Message message){
        System.out.println(message.toString());
    }
}