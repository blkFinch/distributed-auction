package Bank;

import shared.Message;

import java.io.ObjectOutputStream;
import java.net.Socket;

public class CommandProtocol {
    private final Message message;
    private final Socket hostSocket;

    public CommandProtocol(Socket host, Message message) {
        this.message = message;
        this.hostSocket = host;
    }

    public Message proccessCommand(){
        Message.Command command = message.getCommand();
        Message response = null;
        switch (command) {
            case LOGIN:
                Client user = Bank.getActive().getClient(message.getSenderId());
                if (user != null) {
                    response = new Message.Builder()
                            .response(Message.Response.SUCCESS)
                            .senderId(000);

                    Bank.getActive().clients.add(user);

                }else{
                    response  = new Message.Builder()
                            .response(Message.Response.FAILURE)
                            .arguments(new String[]{"no user found"})
                            .senderId(000);
                }
                break;

            case OPENACCOUNT:
                Client newClient = new ClientBuilder()
                        .setName(message.getAccountName())
                        .setHost(hostSocket.getInetAddress())
                        .setBalance(0) //TODO: table needs to be double
                        .setAuctionHouse(false)
                        .build();

                int newUserId = Bank.getActive().createClient(newClient);
                if( newUserId != 999){
                    Bank.getActive().clients.add(newClient);
                    response = new Message.Builder()
                            .response(Message.Response.SUCCESS)
                            .accountId(newUserId)
                            .senderId(000);
                }


                break;

            case GETHOUSES:
                Message res = new Message.Builder().houses(Bank.getActive().getHouses()).nullId();
                System.out.println("sending auction houses");
                break;

            case REGISTERHOUSE:
                Client house = new ClientBuilder()
                        .setName(message.getAccountName())
                        .setHost(hostSocket.getInetAddress()) //TODO: rewrite to use conreqs
                        .setPortNumber(message.getConnectionReqs().get(0).getPort())
                        .setBalance(0) //TODO: table needs to be double
                        .setAuctionHouse(true)
                        .build();

                int newAHId = Bank.getActive().createClient(house);
                if( newAHId != 999){
                    Bank.getActive().registerAuctionHouse(house);
                    response = new Message.Builder()
                            .response(Message.Response.SUCCESS)
                            .accountId(newAHId)
                            .senderId(000);
                }


        }

        return response;
    }
}
