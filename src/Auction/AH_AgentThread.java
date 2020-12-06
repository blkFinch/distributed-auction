package Auction;

import shared.A_AH_Messages;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingDeque;

public class AH_AgentThread extends Thread {
    private final Socket agentSocket;
    /**
     * Constructor for an AgentReqs. Takes socket from AuctionHouseServer,
     * opens in and out streams for it and begins communication.
     *
     * @param socket the accepted socket from the server variable
     */
    public AH_AgentThread(Socket socket) {
        this.agentSocket = socket;
        bankSignOff = new LinkedBlockingDeque<>();
        try {
            agentIn = new ObjectInputStream(agentSocket.getInputStream());
            agentOut = new ObjectOutputStream(
                    agentSocket.getOutputStream());
            Thread client = new Thread(this);
            client.start();
        } catch(IOException e) {
            agentsList.remove(this);
        }
    }

    /**
     * The run method is dedicating to reading message from an agent.
     * The method also adds the incoming message to the log
     */
    @Override
    public void run() {
        do {
            try{
                message = (A_AH_Messages) agentIn.readObject();
                A_AH_Messages.A_AH_MTopic topic = message.getTopic();
                if(topic != A_AH_Messages.A_AH_MTopic.UPDATE) {
                    System.err.println("From a client: " + message);
                }
                switch(topic) {
                    case BID:
                        bid(message);
                        break;
                    case REGISTER:
                        register(message);
                        break;
                    case UPDATE:
                        update();
                        break;
                    case DEREGISTER:
                        agentShutdown(false);
                        break;
                }
            } catch (IOException|ClassNotFoundException e) {
                agentShutdown(false);
                message = null;
            }
        } while(message != null && running);
    }
}
