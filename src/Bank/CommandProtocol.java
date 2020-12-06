package Bank;

import shared.DBMessage;
import shared.Message;

import java.io.ObjectOutputStream;
import java.net.Socket;

public class CommandProtocol {
    private final Message message;

    public CommandProtocol(Message message) {
        this.message = message;
    }

    public Message processCommand(){
        Message.Command command = message.getCommand();
        Message response = null;
        switch (command) {
            case LOGIN:
                Client user = Bank.getActive().getClient(message.getSenderId());
                if (user != null) {
                    response = new Message.Builder()
                            .accountId(user.getID())
                            .connectionReqs(Bank.getActive().getHouses())
                            .response(Message.Response.SUCCESS)
                            .senderId(0);

                    Bank.getActive().clients.add(user);

                }else{
                    response  = failureResponse(-999);
                }
                break;

            case OPENACCOUNT:
                Client newClient = new ClientBuilder()
                        .setName(message.getAccountName())
                        .setBalance(message.getBalance())
                        .setAuctionHouse(false)
                        .build();

                int newUserId = Bank.getActive().createClient(newClient);
                if( newUserId != -999){
                    Bank.getActive().clients.add(newClient);
                    response = new Message.Builder()
                            .response(Message.Response.SUCCESS)
                            .accountId(newUserId)
                            .connectionReqs(Bank.getActive().getHouses())
                            .senderId(0);
                }else{
                    response = failureResponse(-999);
                }
                break;

            case GETHOUSES:
                response = new Message.Builder().connectionReqs(Bank.getActive().getHouses()).nullId();
                System.out.println("sending auction houses");
                break;

            case REGISTERHOUSE:
                Client house = new ClientBuilder()
                        .setName(message.getAccountName())
                        .setHost(message.getConnectionReqs().get(0).getIp()) //TODO: rewrite to use conreqs
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
                            .senderId(0);
                }else{
                    response = failureResponse(-999);
                }
                break;

            case BLOCK:
                int blockID = message.getAccountId();
                Client blockClient = Bank.getActive().findActiveClient(blockID);
               if(blockClient.holdFunds(message.getBalance())){
                   response = new Message.Builder()
                                        .response(Message.Response.SUCCESS)
                                        .accountId(blockID)
                                        .accountName(blockClient.getName())
                                        .senderId(0);
               }else{
                   response = failureResponse(-888);
               }
                break;

            case DEPOSIT:
                int depID = message.getAccountId();
                Client depClient = Bank.getActive().findActiveClient(depID);
                Bank.getActive().depositInto(depClient, message.getBalance());
                response = new Message.Builder()
                                    .response(Message.Response.SUCCESS)
                                    .accountName(depClient.getName())
                                    .accountId(depID)
                                    .cost(depClient.getBalance())
                                    .senderId(0);
                break;


        }

        return response;
    }

    private Message failureResponse(int errCode){
        Message err = new Message.Builder()
                .response(Message.Response.FAILURE)
                .accountId(errCode)
                .senderId(0);
        return err;
    }
}
