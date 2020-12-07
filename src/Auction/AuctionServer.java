package Auction;

import shared.A_AH_Messages;
import shared.ConnectionReqs;
import Auction.AuctionHouseSpecs;
import shared.Message;

import javax.management.AttributeList;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class AuctionServer {
    private static int auctionId = -1;
    private static boolean running = true;
    private static List<AH_AgentThread> activeAgents = new LinkedList<>();

    public static void main(String[] args) throws IOException {
        AuctionHouseSpecs.createAuctionList(3);
        String ip = "localHost";
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            ip = socket.getLocalAddress().getHostAddress();
        }

        int port = 5600;
        //setIPAddress reference auctionspecs
        //send registration
        ConnectionReqs reqs = new ConnectionReqs(ip, port);
        List<ConnectionReqs> reqsList = new ArrayList();
        reqsList.add(reqs);
        Message message = BankActions.getActive().registerBank(reqsList , "Auction1");
        //auctionId = shared.Message.getAccountId();
        ServerSocket auctionSocket = new ServerSocket(port);
        System.out.println("listening...");
        //System.exit(1);


        while(true) {
            try {
                while(running) {
                    Socket clientSocket = auctionSocket.accept();
                    //processess inputs
                    AH_AgentThread at = new AH_AgentThread(clientSocket);
                    activeAgents.add(at);
                    at.start();
                }
            } catch (IOException e) {
                running = false;
            }
        }
    }

    /**
     * getAuctionId returns auctionId
     *
     * @return auctionId int
     */
    public static int getAuctionId() {
        return auctionId;
    }

    /**
     * searches for agent in active agent list
     * @param id id of agent we want
     * @return returns the agentProxy we want, null otherwise
     */
    static AH_AgentThread agentSearch(int id) {
        for(AH_AgentThread agent: activeAgents) {
            if(agent.agentId == id) {
                return agent;
            }
        }
        return null;
    }
}
