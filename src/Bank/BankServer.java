package Bank;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BankServer {
    public static Bank activeBank;

    public static void main(String[] args) throws IOException {
        //Hardcode port
        int portNumber = 6000;

        //Requires
        if (args.length < 1) {
            System.err.println(
                    "Usage: java BankServer <DB ipaddress> (optional) <log>");
            System.exit(1);
        }

        //Set DB address and port
        Bank.getActive().setDbIPaddress(args[0]);
        Bank.getActive().setDbPort(6002); //hardcoded as per README

        //Optional arg to set Logger
        if(args[1] == "log"){ Logger.log = true; }

        ServerSocket serverSocket = new ServerSocket(portNumber);
        System.out.println("port open: " + portNumber);
        System.out.println("listening...");

        //While bank server is running
        while(true){
            //Listen for socket connections
            try {
                Socket clientSocket = serverSocket.accept();
                //create new thread for each new client
                BankThread bt = new BankThread(clientSocket);
                bt.start();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
