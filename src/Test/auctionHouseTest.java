package Test;

import shared.ConnectionReqs;
import shared.BankMessages;

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
        BankMessages loginRequest = new BankMessages.Builder()
                .command(BankMessages.Command.LOGIN)
                .senderId(1);

        BankMessages newUserRequest = new BankMessages.Builder()
                                            .command(BankMessages.Command.OPENACCOUNT)
                                            .accountName("Jimmy")
                                            .nullId();

        ConnectionReqs req = new ConnectionReqs("localhost", 9999);
        List<ConnectionReqs> reqs = new ArrayList<>();
        reqs.add(req);

        BankMessages newAhRequest = new BankMessages.Builder()
                .command(BankMessages.Command.REGISTERHOUSE)
                .accountName("AH-320")
                .connectionReqs(reqs)
                .nullId();
        //Send request to Bank
        out.writeObject(newAhRequest);

        while(true){
            BankMessages messageIn = (BankMessages) in.readObject();

            if(messageIn.getResponse() == BankMessages.Response.SUCCESS){
                System.out.println("SUCCESS!");
                System.out.println("user id: " + messageIn.getAccountId());
            }
        }

    }
}
