package Bank;

import java.net.InetAddress;
import java.net.Socket;

import static Bank.BankServer.activeBank;

public class LoginProtocol {
    //STATE
    private static final int LOGIN = 0;
    private static final int NEW_USER = 1;
    private static final int init = 3;
    private int state = 3;

    private Socket clientSocket;

    public LoginProtocol(Socket clientSocket){
        this.clientSocket = clientSocket;
    }

    public String processInput(String input){
        String output = null;

        switch (state){
            case 0:
                if(Integer.parseInt(input) == 0){
                    output="Please enter name";
                    state = 1;
                    //initialize as new client

                }
                else{
                    Client client = Bank.getClient(Integer.parseInt(input));
                    output = "hello "+ client.getName();
                }
                break;
            case 1:
                int clientPort = clientSocket.getPort();
                InetAddress clientHost = clientSocket.getInetAddress();
                Bank.addNewClient(clientHost, clientPort, input);
                output = "Hello " + input;
                break;
            case 3:
                output = "Please enter ID or enter 0 to create new";
                state = 0;
                break;
        }

        return output;
    }
}
