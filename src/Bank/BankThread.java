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
        System.out.println(
                "running new bank thread from " + socket.getInetAddress()
        );

        try {
            Message message;

            while(( message = readMessage() ) != null){
                CommandProtocol cp = new CommandProtocol(message);
                Message res = cp.processCommand();
                objOut.writeObject(res);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Message readMessage() throws IOException, ClassNotFoundException {
        Message message = (Message) objIn.readObject();
        return message;
    }
}
