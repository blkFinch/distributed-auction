package Bank;

import shared.Message;

public class Logger {
    public static boolean log = false;

    public static void logMessage(Message message){
        if(log){
            if(message.getCommand() != Message.Command.GETHOUSES){
                System.out.println(message.toString());
            }

        }
    }
}