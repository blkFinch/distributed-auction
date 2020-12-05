package shared;

import java.io.Serializable;

public class ConnectionReqs implements Serializable {
    private int    port;
    private String ip;
    private String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ServerSpecs{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", name= " + name +
                '}';
    }
}
