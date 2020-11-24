package Bank;

import java.net.InetAddress;
import java.util.ArrayList;

public class Bank {
    public static Bank active;
    ArrayList<Client> clients;


    public Bank(){
        clients = new ArrayList<Client>();
    }

    public void addNewClient(InetAddress host, int portnumber){
        Client newClient = new Client(host, portnumber);
        clients.add(newClient);
        System.out.println("added a new client at " + newClient.getHost() + " : " + newClient.getPort());
        System.out.println("balance of " + newClient.getBalance());
    }


}
