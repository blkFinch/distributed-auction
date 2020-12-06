package Auction;

import Bank.CommandProtocol;
import shared.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class AH_BankThread extends Thread {
    protected Socket socket;
    private final ObjectInputStream objIn;
    private final ObjectOutputStream objOut;

    public AH_BankThread(Socket clientSocket) throws IOException {
        this.socket = clientSocket;
        objOut = new ObjectOutputStream(socket.getOutputStream());
        objOut.flush();
        objIn = new ObjectInputStream(socket.getInputStream());
    }

    private Message readMessage() throws IOException, ClassNotFoundException {
        Message message = (Message) objIn.readObject();
        return message;
    }
}
