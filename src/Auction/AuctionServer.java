/**
 * Ryan Cooper
 * rycooper
 */
package Auction;

import shared.ConnectionReqs;
import shared.Message;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AuctionServer {
    public static int bankPort;
    public static String bankIp;
    public static int dBPort;
    public static String dBIp;
    private static String name;
    static int port;
    public static int auctionId;
    static ServerSocket auctionSocket;
    public static ConnectionReqs reqs;
    static Message message;
    private static boolean running = true;
    static List<AH_AgentThread> activeAgents = new LinkedList<>();

    public static void main(String[] args, int argc) throws IOException {
        if(argc != 3) {
            System.out.println("Arg number wrong.");
            System.exit(1);
        }
        String ip;
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            ip = socket.getLocalAddress().getHostAddress();
        }

        port = Integer.parseInt(args[0]);
        bankPort = Integer.parseInt(args[1]);
        bankIp = args[2];
        name = args[3];

        reqs = new ConnectionReqs(ip, port);
        List<ConnectionReqs> reqsList = new ArrayList<>();
        reqsList.add(reqs);
        message = BankActions.getActive().registerBank(reqsList , name);
        auctionSocket = new ServerSocket(port);
        System.out.println("listening...");

        while(running) {
            try {
                    Socket clientSocket = auctionSocket.accept();
                    //processess inputs
                    AH_AgentThread at = new AH_AgentThread(clientSocket);
                    activeAgents.add(at);
                    at.start();
            } catch (IOException e) {
                e.printStackTrace();
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
