package Database;

import Bank.Client;
import Database.Tasks.CreateClient;
import Database.Tasks.ReadClient;
import Database.Tasks.ReadItem;
import Database.Tasks.UpdateClient;
import shared.DBMessage;
import shared.Items.Item;
import shared.Message;

import java.sql.SQLOutput;

public class DBCommandProtocol {
    private final DBMessage message;

    public DBCommandProtocol(DBMessage message) {
        this.message = message;
    }

    public DBMessage processCommand() {
        DBMessage.Command command = message.getCommand();
        DBMessage response = null;

        switch (command){
            //TODO: only bank should get client
            case GET:
                if(message.getTable() == DBMessage.Table.CLIENT){
                    response = getClient();
                }
                if(message.getTable() == DBMessage.Table.ITEM){
                    response = getItem();
                }

                break;

            case PUT:
                Client putClient = (Client) message.getPayload();
                CreateClient cc = new CreateClient(putClient);

                int newID = (int) SyncInjector.getActive().executeInjection(cc);
                if(newID != -999){
                    response = new DBMessage.Builder()
                            .response(Message.Response.SUCCESS)
                            .accountId(newID)
                            .build();
                }else{
                    response = failureResponse(-999);
                }

                break;

            case UPDATE:
                Client updateClient = (Client) message.getPayload();
                UpdateClient uc = new UpdateClient(updateClient);
                int uID = (int) SyncInjector.getActive().executeInjection(uc);
                response = new DBMessage.Builder()
                                .response(Message.Response.SUCCESS)
                                .accountId(uID)
                                .build();
                break;

        }

        return response;
    }

    public DBMessage getItem(){
        DBMessage response;
        int id = message.getAccountId();
        ReadItem ri = new ReadItem(id);
        Item item = (Item) SyncInjector.getActive().executeInjection(ri);
        System.out.println(item.toString());
        if(item != null){
            response = new DBMessage.Builder()
                            .response(Message.Response.SUCCESS)
                            .accountId(id)
                            .payload(item)
                            .build();
        }else{
            response = new DBMessage.Builder()
                            .response(Message.Response.FAILURE)
                            .build();
        }
        return response;
    }

    public DBMessage getClient() {
        DBMessage response;
        int id = message.getAccountId();
        ReadClient rc = new ReadClient(id);
        try {
            Client getClient = (Client) SyncInjector.getActive().executeInjection(rc);
            response = new DBMessage.Builder()
                    .response(Message.Response.SUCCESS)
                    .accountId(id)
                    .payload(getClient)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            response = failureResponse(-999);
        }
        return response;
    }

    private DBMessage failureResponse(int errCode){
        DBMessage err = new DBMessage.Builder()
                                    .response(Message.Response.FAILURE)
                                    .accountId(errCode)
                                    .build();
        return err;
    }
}
