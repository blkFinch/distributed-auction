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
            BufferedReader stdIn =
                    new BufferedReader(new InputStreamReader(System.in));
            String fromServer;
            String fromUser;

            fromServer = in.readLine();
            while (fromServer != null) {
                System.out.println("Server: " + fromServer);

                if (fromServer.equals("Bye.")) {break;}

                fromUser = stdIn.readLine();
                if (fromUser != null) {
                    System.out.println("Client: " + fromUser);
                    out.println(fromUser);
                }
                fromServer = in.readLine();
            }

            System.out.println("from server null");
        }
    }
}
