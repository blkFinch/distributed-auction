package Test;

import shared.ConnectionReqs;
import shared.Message;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class auctionHouseTest {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        int portNumber = Integer.parseInt(args[0]);
        Socket bankSocket = new Socket("localhost",6000);
        ObjectOutputStream out = new ObjectOutputStream(bankSocket.getOutputStream());
        out.flush();
        ObjectInputStream in = new ObjectInputStream(bankSocket.getInputStream());

        System.out.println("running on port: " + portNumber);

//            //Build simple request
        Message loginRequest = new Message.Builder()
                .command(Message.Command.LOGIN)
                .senderId(1);

        Message newUserRequest = new Message.Builder()
                                            .command(Message.Command.OPENACCOUNT)
                                            .accountName("Jimmy")
                                            .nullId();

        ConnectionReqs req = new ConnectionReqs("localhost", 9999);
        List<ConnectionReqs> reqs = new ArrayList<>();
        reqs.add(req);

        Message newAhRequest = new Message.Builder()
                .command(Message.Command.REGISTERHOUSE)
                .accountName("AH-320")
                .connectionReqs(reqs)
                .nullId();
        //Send request to Bank
        out.writeObject(newAhRequest);

        while(true){
            Message messageIn = (Message) in.readObject();

            if(messageIn.getResponse() == Message.Response.SUCCESS){
                System.out.println("SUCCESS!");
                System.out.println("user id: " + messageIn.getAccountId());
            }
        }

    }
}
