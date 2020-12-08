package Test;

import com.github.javafaker.Faker;
import shared.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class LoginTest {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket socket = new Socket("localhost", 6000);


        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        Message req = new Message.Builder()
                .command(Message.Command.LOGIN)
                .senderId(1);

        out.writeObject(req);

        Message res = (Message) in.readObject();

        System.out.println(res.toString());

        req = new Message.Builder()
                .command(Message.Command.DEREGISTER)
                .senderId(1);

        out.writeObject(req);
        res = (Message) in.readObject();

        System.out.println(res.toString());
    }
}
