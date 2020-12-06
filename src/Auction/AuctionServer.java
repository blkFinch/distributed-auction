package Auction;

import shared.ConnectionReqs;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class AuctionServer {
    private static boolean running = true;

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println(
                    "Usage: java BankServer <port number>");
            System.exit(1);
        }
        /*try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            ip = socket.getLocalAddress().getHostAddress();
        }*/
        String ip = "localHost";
        int port = 5600;
        //setIPAddress reference auctionspecs
        //send registration
        ConnectionReqs reqs = new ConnectionReqs(ip, port);
        BankActions.getActive().registerBank(reqs , "Auction1");
        int portNumber = Integer.parseInt(args[0]);
        ServerSocket auctionSocket = new ServerSocket(portNumber);
        System.out.println("listening...");

        /*while(true) {
            try {
                while(running) {
                    Socket clientSocket  = auctionSocket.accept();
                    //processess inputs
                    AH_AgentThread bt = new AH_AgentThread(clientSocket);
                    bt.start();
                }
            } catch (IOException e) {
                running = false;
            }
        }*/
    }
}
