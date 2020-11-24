package Bank;

import java.net.InetAddress;

//DEPRECATED -just need account
public class Client {
    Account account;
    private int portNumber;
    private InetAddress host;

    public int getPort(){
        return portNumber;
    }

    public InetAddress getHost(){
        return host;
    }

    public int getBalance(){
        return account.getBalance();
    }

    public Client(InetAddress host,int port){
        this.portNumber = port;
        this.host = host;
        this.account = new Account();
    }
}
