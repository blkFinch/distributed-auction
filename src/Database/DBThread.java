package Database;

import Bank.CommandProtocol;
import shared.DBMessage;
import shared.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class DBThread extends Thread{
    protected Socket socket;
    private final ObjectInputStream objIn;
    private final ObjectOutputStream objOut;

    public DBThread(Socket clientSocket) throws IOException {
        this.socket = clientSocket;
        objOut = new ObjectOutputStream(socket.getOutputStream());
        objOut.flush();
        objIn = new ObjectInputStream(socket.getInputStream());
    }

    public void run() {
        System.out.println("recieved req");
        try {
            DBMessage message = (DBMessage) objIn.readObject();
            DBCommandProtocol DBcp = new DBCommandProtocol(message);
            DBMessage res = DBcp.processCommand();
            objOut.writeObject(res);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
