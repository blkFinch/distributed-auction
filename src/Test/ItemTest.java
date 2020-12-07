package Test;

import shared.DBMessage;
import shared.Items.Item;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;

/**
 * Test for getting random item from server.
 * Make sure DBServer.java is running
 */
public class ItemTest {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket dbSocket = new Socket("localhost", 6002);

        ObjectOutputStream out = new ObjectOutputStream(dbSocket.getOutputStream());
        out.flush();
        ObjectInputStream in = new ObjectInputStream(dbSocket.getInputStream());

        Random random = new Random();
        int randInt = random.nextInt(200);

        DBMessage itemReq = new DBMessage.Builder()
                                .command(DBMessage.Command.GET)
                                .table(DBMessage.Table.ITEM)
                                .accountId(randInt)
                                .build();

        out.writeObject(itemReq);

        DBMessage response = (DBMessage) in.readObject();

        Item item = (Item) response.getPayload();
        System.out.println(response.toString());
        System.out.println(item.getName());
        System.out.println(item.getDescription());

    }
}
