package Test;

import shared.Message;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class auctionHouseTest {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        int portNumber = Integer.parseInt(args[0]);
        Socket bankSocket = new Socket("localhost",6000);
//        ObjectInputStream in = new ObjectInputStream(bankSocket.getInputStream());
        ObjectOutputStream out = new ObjectOutputStream(bankSocket.getOutputStream());

        System.out.println("running on port: " + portNumber);

//            //Build simple request
        Message loginRequest = new Message.Builder()
                .command(Message.Command.LOGIN)
                .senderId(1);

        Message newUserRequest = new Message.Builder()
                                            .command(Message.Command.OPENACCOUNT)
                                            .accountName("Gregg")
                                            .nullId();

        Message newAhRequest = new Message.Builder()
                .command(Message.Command.OPENACCOUNT)
                .accountName("AH-100")
                .arguments(new String[]{"auction"})
                .nullId();
        //Send request to Bank
        out.writeObject(newUserRequest);

//        while(true){
//            Message messageIn = (Message) in.readObject();
//
//            if(messageIn.getResponse() == Message.Response.SUCCESS){
//                System.out.println("SUCCESS!");
//            }
//        }

    }
}
