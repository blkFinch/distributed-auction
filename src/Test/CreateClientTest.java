package Test;

import shared.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class CreateClientTest {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket socket = new Socket("localhost", 6000);

        String name = "jegg";

        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        Message req = new Message.Builder()
                                .command(Message.Command.OPENACCOUNT)
                                .accountName(name)
                                .balance(201)
                                .nullId();

        out.writeObject(req);

        Message res = (Message) in.readObject();

        System.out.println(res.toString());

    }
}
