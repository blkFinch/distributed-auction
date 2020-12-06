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
        Client target = null;
        int targetId = 0;

        switch (command) {
            case LOGIN:
                target = Bank.getActive().getClient(message.getSenderId());
                if (target != null) {
                    response = new Message.Builder()
                            .accountId(target.getID())
                            .connectionReqs(Bank.getActive().getHouses())
                            .response(Message.Response.SUCCESS)
                            .senderId(0);

                    Bank.getActive().loginUser(target);

                }else{
                    response  = failureResponse(-999);
                }
                break;

            case DEREGISTER:
                target  = Bank.getActive().findActiveClient(message.getSenderId());
                Bank.getActive().deregisterUser(target);
                response = new Message.Builder()
                                        .response(Message.Response.SUCCESS)
                                        .accountId(target.getID())
                                        .arguments(new String[]{"LOGOUT successful"})
                                        .senderId(0);
                break;

            case OPENACCOUNT:
                target = new ClientBuilder()
                        .setName(message.getAccountName())
                        .setBalance(message.getBalance())
                        .setAuctionHouse(false)
                        .build();

                int newUserId = Bank.getActive().createClient(target);
                if( newUserId != -999){
                    Bank.getActive().clients.add(target);
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
                response = new Message.Builder()
                        .connectionReqs(Bank.getActive().getHouses()).nullId();
                break;

            case REGISTERHOUSE:
                target= new ClientBuilder()
                        .setName(message.getAccountName())
                        .setHost(message.getConnectionReqs().get(0).getIp())
                        .setPortNumber(message.getConnectionReqs().get(0).getPort())
                        .setBalance(0) //TODO: table needs to be double
                        .setAuctionHouse(true)
                        .build();

                int newAHId = Bank.getActive().createClient(target);
                if( newAHId != 999){
                    Bank.getActive().registerAuctionHouse(target);
                    response = new Message.Builder()
                            .response(Message.Response.SUCCESS)
                            .accountId(newAHId)
                            .senderId(0);
                }else{
                    response = failureResponse(-999);
                }
                break;

            case GETBALANCE:
                target = Bank.getActive().getClient(message.getAccountId());
                int balance = target.getBalance();
                response = new Message.Builder().response(Message.Response.SUCCESS)
                                                .balance(balance)
                                                .accountId(target.getID())
                                                .senderId(0);
                break;

            case BLOCK:
                targetId = message.getAccountId();
                target = Bank.getActive().findActiveClient(targetId);
               if(target.holdFunds(message.getBalance())){
                   response = new Message.Builder()
                                        .response(Message.Response.SUCCESS)
                                        .accountId(targetId)
                                        .accountName(target.getName())
                                        .senderId(0);
               }else{
                   response = failureResponse(-888);
               }
                break;

            case DEPOSIT:
                targetId = message.getAccountId();
                target = Bank.getActive().findActiveClient(targetId);
                Bank.getActive().depositInto(target, message.getBalance());
                response = new Message.Builder()
                                    .response(Message.Response.SUCCESS)
                                    .accountName(target.getName())
                                    .accountId(targetId)
                                    .balance(target.getBalance())
                                    .senderId(0);
                break;

            case UNBLOCK:
                targetId = message.getAccountId();
                target = Bank.getActive().findActiveClient(targetId);
                target.releaseFunds(message.getBalance());
                response = new Message.Builder()
                                    .response(Message.Response.SUCCESS)
                                    .senderId(0);
                break;

            case TRANSFER:
                targetId = message.getAccountId();
                target = Bank.getActive().findActiveClient(targetId);
                int funds = Bank.getActive().withdrawFunds(target, message.getBalance());
                int recieverId = message.getSenderId();
                Client reciever = Bank.getActive().findActiveClient(recieverId);
                reciever.deposit(funds);
                response = new Message.Builder()
                                    .response(Message.Response.SUCCESS)
                                    .balance(reciever.getBalance())
                                    .senderId(0);
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
