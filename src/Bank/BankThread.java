package Bank;

import shared.BankMessages;

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
            BankMessages message = readMessage();
            CommandProtocol cp = new CommandProtocol(socket, message); //TODO:refactor remove need for passing socket
            BankMessages res = cp.proccessCommand();
            objOut.writeObject(res);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BankMessages readMessage() throws IOException, ClassNotFoundException {
        BankMessages message = (BankMessages) objIn.readObject();
        return message;
    }
}
