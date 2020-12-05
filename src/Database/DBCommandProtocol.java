package Database;

import Bank.Client;
import Database.Tasks.CreateClient;
import Database.Tasks.ReadClient;
import shared.DBMessage;
import shared.Message;

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
                int id = message.getAccountId();
                ReadClient rc = new ReadClient(id);
                try {
                    Client getClient = rc.inject();
                    response = new DBMessage.Builder()
                            .response(Message.Response.SUCCESS)
                            .accountId(id)
                            .payload(getClient)
                            .build();

                } catch (Exception e) {
                    e.printStackTrace();
                    response = failureResponse(-999);
                }
                break;

            case PUT:
                Client putClient = (Client) message.getPayload();
                CreateClient cc = new CreateClient(putClient);
                try {
                    int newID = cc.inject();
                    response = new DBMessage.Builder()
                            .response(Message.Response.SUCCESS)
                            .accountId(newID)
                            .build();
                } catch (Exception e) {
                    e.printStackTrace();
                    response = failureResponse(-999);
                }

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
