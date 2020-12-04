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
        //Send request to Bank
        out.writeObject(loginRequest);

//        while(true){
//            Message messageIn = (Message) in.readObject();
//
//            if(messageIn.getResponse() == Message.Response.SUCCESS){
//                System.out.println("SUCCESS!");
//            }
//        }

    }
}
