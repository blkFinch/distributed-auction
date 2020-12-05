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
        objOut = new ObjectOutputStream(socket.getOutputStream());
        objOut.flush();
        objIn = new ObjectInputStream(socket.getInputStream());
    }

    public void run() {
        System.out.println("running new bank thread");
        try {
            Message message = readMessage();
            CommandProtocol cp = new CommandProtocol(socket, message); //TODO:refactor remove need for passing socket
            Message res = cp.proccessCommand();
            objOut.writeObject(res);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Message readMessage() throws IOException, ClassNotFoundException {
        Message message = (Message) objIn.readObject();
        return message;
    }
}
