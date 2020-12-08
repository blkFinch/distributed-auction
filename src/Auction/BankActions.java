/**
 * Ryan Cooper
 * rycooper
 */
package Auction;

import shared.ConnectionReqs;
import shared.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

/**
 * Bank Actions
 */
public class BankActions {
    private static BankActions active;
    public static BankActions getActive() {
        if(active == null) {
            active = new BankActions();
        }
        return active;
    }

    public Message registerBank(List<ConnectionReqs> reqsList, String name) {
        Message message = new Message.Builder().command(Message.Command.REGISTERHOUSE)
                .connectionReqs(reqsList).accountName(name).nullId();
        Message response = sendToBank(message);
        assert response != null;
        AuctionServer.auctionId = response.getAccountId();
        List<ConnectionReqs> dBReqs = response.getConnectionReqs();
        ConnectionReqs reqs = dBReqs.get(0);
        AuctionServer.dBPort = reqs.getPort();
        AuctionServer.dBIp = reqs.getIp();
        CountDown count = new CountDown();
        Thread timer = new Thread(count);
        timer.start();
        if(response.getResponse() == Message.Response.SUCCESS) {
            System.out.println("Connection Success");
        }
        return response;
    }

    public static Message sendToBank(Message message) {
        try {
            Socket bankSocket = new Socket(AuctionServer.bankIp, AuctionServer.bankPort);
            ObjectOutputStream out = new ObjectOutputStream(bankSocket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(bankSocket.getInputStream());
            out.writeObject(message);
            return (Message) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
