package Bank;

import Database.TaskRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class BankServer {
    public static Bank activeBank;

    public static void main(String[] args) throws IOException {
        TaskRunner runner = new TaskRunner();
        runner.start();
        activeBank = new Bank();

        if (args.length != 1) {
            System.err.println(
                    "Usage: java BankServer <port number>");
            System.exit(1);
        }

        //I'm setting this port 6000 in my config -gh
        int portNumber = Integer.parseInt(args[0]);//6000
        ServerSocket serverSocket = new ServerSocket(portNumber);
        System.out.println("listening...");

        //While bank server is running
        while(true){
            //Listen for socket connections
            try {
                Socket clientSocket = serverSocket.accept();
                //create new thread for each new client
                BankThread bt = new BankThread(clientSocket, runner);
                bt.start();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
