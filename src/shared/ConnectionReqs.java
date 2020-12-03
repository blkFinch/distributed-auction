package shared;

import java.io.Serializable;

public class ConnectionReqs implements Serializable {
    private int    port;
    private String ip;

    public ConnectionReqs(String ip, int port) {
        this.ip   = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "ServerSpecs{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
