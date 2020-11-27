package Agent;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class AgentProxy{
    private Socket socket;
    private List<String> inMessages;
    private List<String> outMessages;

    public AgentProxy(String type, String host, int port) throws IOException{
        inMessages = new ArrayList<>();
        outMessages = new ArrayList<>();
        if(type.equals("bank")){ bankClient(host, port); }
        else if(type.equals("auction")){ auctionClient(host, port); }
        else{ throw new IOException(); }
    }

    private void bankClient(String host, int port) throws IOException{
        socket = new Socket(host, port);
    }

    private void auctionClient(String host, int port) throws IOException{
        socket = new Socket(host, port);
    }

    public void sendMessage(String message){ outMessages.add(message); }
}
