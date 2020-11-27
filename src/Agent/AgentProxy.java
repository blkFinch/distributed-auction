package Agent;

import java.io.IOException;
import java.net.Socket;

public class AgentProxy{
    private Socket socket;

    public AgentProxy(String type, String host, int port) throws IOException{
        if(type.equals("bank")){ bankClient(host, port); }
    }

    private void bankClient(String host, int port) throws IOException{
        socket = new Socket(host, port);
    }
}
