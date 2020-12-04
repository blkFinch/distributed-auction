package Bank;

import shared.Message;

import java.io.*;
import java.net.Socket;


public class BankThread extends Thread {
    protected Socket socket;
    private final ObjectInputStream objIn;
    private final ObjectOutputStream objOut;

    public BankThread(Socket clientSocket) throws IOException {
        this.socket = clientSocket;
        objIn = new ObjectInputStream(socket.getInputStream());
        objOut = new ObjectOutputStream(socket.getOutputStream());
    }

    public void run() {
        System.out.println("running new bank thread");
        try {
            Message message = readMessage();
            if(message == null){
                System.out.println("null message");
            }
            Message.Command command = message.getCommand();
            switch (command){
                case LOGIN:
                   if( Bank.getActive().getClient(message.getSenderId()) != null){
                       System.out.println("lookupsuccess");
                   }
                   break;
                case OPENACCOUNT:
                    Boolean isAuction = (message.getArguments() != null); //checks for auction arg
                    Client newClient = new ClientBuilder()
                                            .setName(message.getAccountName())
                                            .setHost(socket.getInetAddress())
                                            .setBalance(0) //TODO: table needs to be double
                                            .setAuctionHouse(isAuction)
                                            .build();
                    //CREATE client
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Message readMessage() throws IOException, ClassNotFoundException {
        Message message = (Message) objIn.readObject();
        return message;
    }
}
