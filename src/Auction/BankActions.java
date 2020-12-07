package Auction;

import shared.ConnectionReqs;
import shared.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class BankActions {
    private static BankActions active;
    public static BankActions getActive() {
        if(active == null) {
            active = new BankActions();
        }
        return active;
    }
    //Methods actions block, register, unblock

    public Message registerBank(List<ConnectionReqs> reqsList, String name) {
        Message message = new Message.Builder().command(Message.Command.REGISTERHOUSE)
                .connectionReqs(reqsList).accountName(name).nullId();
        Message response = sendToBank(message);
        if(response.getResponse() == Message.Response.SUCCESS) {
            System.out.println("Connection Success");
        }
        return response;
    }

    public static Message sendToBank(Message message) {
        try {
            Socket bankSocket = new Socket("localHost", 6000);
            ObjectOutputStream out = new ObjectOutputStream(bankSocket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(bankSocket.getInputStream());
            out.writeObject(message);
            System.out.println("out written");
            Message response = (Message) in.readObject();
            return response;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}