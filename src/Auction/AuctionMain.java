package Auction;

import shared.BankMessages;
import shared.ConnectionReqs;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.LinkedList;
import java.util.List;

public class AuctionMain {
    private Socket             bankSocket; //for bank
    private static ServerSocket       auctionSocket; //the socket connected to agents
    private String             ip; //Auction server ip address
    private int                port;  //Auction server port number
    private ObjectInputStream  ioIn;
    private ObjectOutputStream bankOut;

    Socket getBankSocket() {
        return bankSocket;
    }

    static ServerSocket getAuctionSocket() {
        return auctionSocket;
    }

    public AuctionMain(String address, int bankPort, int auctionPort) throws IOException {
        ItemSpecsList.createItemSpecsList();

        bankSocket = new Socket();
        bankSocket.connect(new InetSocketAddress(address, bankPort),
                3000);
        bankOut = new ObjectOutputStream(bankSocket.getOutputStream());
        auctionSocket = new ServerSocket(auctionPort);
        Thread auctionThread = new Thread(new AuctionAgents.AHServer());
        auctionThread.start();

        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            ip = socket.getLocalAddress().getHostAddress();
        }
        port = auctionSocket.getLocalPort();
        ConnectionReqs bankReqs = new ConnectionReqs(ip, port);
        List<ConnectionReqs> ahInfo = new LinkedList<>();
        ahInfo.add(bankReqs);
        BankMessages register = new BankMessages.Builder().command(
                BankMessages.Command.REGISTERHOUSE)
                .connectionReqs(ahInfo).nullId();
        sendToBank(register);
        Thread inThread = new Thread(new AuctionBank.AuctionIn());
        inThread.start();
    }

    /**
     * Sends the given message to the bank and adds it to the log for display.
     * Looped messages (GET_AVAILABLE) are ignored when adding to log.
     * @param message message being sent to the bank.
     */
    private synchronized void sendToBank(BankMessages message) {
        try {
            BankMessages.Command temp = message.getCommand();
            if(temp != BankMessages.Command.GETBALANCE) {
                System.err.println("Bank: " + message);
            }
            bankOut.reset();
            bankOut.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
