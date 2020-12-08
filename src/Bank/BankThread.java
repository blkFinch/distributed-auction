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
        boolean running = true;
        System.out.println(
                "running new bank thread from " + socket.getInetAddress()
        );

        try {
            Message message;

            while(running){
                message = readMessage();
                Logger.logMessage(message);

                if (message.getCommand() == Message.Command.DEREGISTER){
                    //Breaks loop after final return message
                    System.out.println(
                            "deregister requested by " + message.getSenderId());

                    running = false;
                }

                CommandProtocol cp = new CommandProtocol(message);
                Message res = cp.processCommand();
                objOut.writeObject(res);

            }


        } catch (Exception e) {
            System.out.println("Thread closed roughly >_< use deregister please!");
            e.printStackTrace();
        }
    }

    private Message readMessage() throws IOException, ClassNotFoundException {
        Message message = (Message) objIn.readObject();
        return message;
    }
}
