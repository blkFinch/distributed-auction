package Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class auctionHouseTest {

    public static void main(String[] args) throws IOException {
        int portNumber = Integer.parseInt(args[0]);

        try(
                ServerSocket serverSocket = new ServerSocket(portNumber);
                //Hardcoded to my settings for now
                //TODO: create interface to select bank connection...
                Socket bankSocket = new Socket("localhost",6000);
                //connect to bank IO stream
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(bankSocket.getInputStream()));
                PrintWriter out =
                        new PrintWriter(bankSocket.getOutputStream(), true);
            ){
                System.out.println("running on port: " + portNumber);
                String fromServer;


            while ((fromServer = in.readLine()) != null) {
                System.out.println("Server: " + fromServer);
            }
                //eventually clients will connect here
                Socket clientSocket = serverSocket.accept();

                //TODO: create server communication handler


        }
    }
}
