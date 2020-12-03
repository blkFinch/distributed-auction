package Bank;

import java.net.InetAddress;

public class ClientBuilder {
    private int ID;
    private int portNumber;
    private InetAddress host;
    private int balance;
    private String name;
    private boolean isAuctionHouse;


    public ClientBuilder(int portNumber, InetAddress host, int balance,
                         String name, boolean isAuctionHouse) {
        this.portNumber = portNumber;
        this.host = host;
        this.balance = balance;
        this.name = name;
        this.isAuctionHouse = isAuctionHouse;
    }

    public ClientBuilder() {

    }

    public ClientBuilder setId(int id){
        this.ID = id;
        return this;
    }

    public ClientBuilder setPortNumber(int portNumber) {
        this.portNumber = portNumber;
        return this;
    }

    public ClientBuilder setHost(InetAddress host) {
        this.host = host;
        return this;
    }

    public ClientBuilder setBalance(int balance) {
        this.balance = balance;
        return this;
    }

    public ClientBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ClientBuilder setAuctionHouse(boolean auctionHouse) {
        isAuctionHouse = auctionHouse;
        return this;
    }

    public Client build(){
        return new Client(ID, portNumber, host, balance, name, isAuctionHouse);
    }
}
