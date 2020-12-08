package Database;

import Bank.BankThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The main server for the database. This hosts the DB file and handles
 * all requests to READ and WRITE to DB
 */
public class DatabaseServer {
    public static void main(String[] args) throws IOException {

        //I'm setting this port 6002 as noted in README
        int portNumber = 6002;
        ServerSocket serverSocket = new ServerSocket(portNumber);
        System.out.println("port open: " + portNumber);
        System.out.println("listening...");

        //While bank server is running
        while(true){
            //Listen for socket connections
            try {
                Socket clientSocket = serverSocket.accept();
                //create new thread for each new client
                DBThread dbThread = new DBThread(clientSocket);
                dbThread.start();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
