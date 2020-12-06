package Auction;

import shared.A_AH_Messages;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class AH_AgentThread extends Thread {
    protected static Socket agentSocket;
    private static ObjectInputStream agentIn;
    private static ObjectOutputStream agentOut;
    static BlockingQueue<Boolean> bankSignOff = new LinkedBlockingDeque<>();
    /**
     * Constructor for an AgentReqs. Takes socket from AuctionHouseServer,
     * opens in and out streams for it and begins communication.
     *
     * @param socket the accepted socket from the server variable
     */
    public AH_AgentThread(Socket socket) throws IOException {
        this.agentSocket = socket;
        agentOut = new ObjectOutputStream(socket.getOutputStream());
        agentOut.flush();
        agentIn = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * The run method is dedicating to reading message from an agent.
     * The method also adds the incoming message to the log
     */
    @Override
    public void run() {
        A_AH_Messages message;
        do {
            try{
                message = (A_AH_Messages) agentIn.readObject();
                A_AH_Messages.A_AH_MTopic topic = message.getTopic();
                if(topic != A_AH_Messages.A_AH_MTopic.UPDATE) {
                    System.err.println("From a client: " + message);
                }
                switch(topic) {
                    case BID:
                        AgentActions.bid(message);
                        break;
                    case REGISTER:
                        AgentActions.register(message);
                        break;
                    case DEREGISTER:
                        agentShutdown();
                        break;
                    case UPDATE:
                        AgentActions.update();
                        break;
                }
            } catch (IOException|ClassNotFoundException e) {
                agentShutdown();
                message = null;
            }
        } while(message != null);// && running);
    }

    /**
     * This method is given an AuctionMessage and writes/sends it to
     * agentSocket.
     * @param message the message being sent
     */
    static void sendOut(A_AH_Messages message) {
        try{
            if(message.getTopic() != A_AH_Messages.A_AH_MTopic.UPDATE) {
                System.err.println("To Agent: " + message);
            }
            agentOut.reset();
            agentOut.writeObject(message);
        } catch(IOException e) {
            agentShutdown();
        }
    }

    static void agentShutdown() {
        A_AH_Messages message = AgentActions.deRegister();
        try {
            agentOut.reset();

            agentOut.writeObject(message);
            if(!agentSocket.isClosed()){
                agentOut.close();
                agentIn.close();
                agentSocket.close();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
