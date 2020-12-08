package Bank;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BankServer {
    public static Bank activeBank;

    public static void main(String[] args) throws IOException {
        int portNumber;
        if (args.length != 1) {
            portNumber = 6000;
        }else{
            //I'm setting this port 6000 in my config -gh
            portNumber = Integer.parseInt(args[0]);//6000
        }


        ServerSocket serverSocket = new ServerSocket(portNumber);
        System.out.println("port open: " + portNumber);
        System.out.println("listening...");

        //While bank server is running
        while(true){
            //Listen for socket connections
            try {
                Socket clientSocket = serverSocket.accept();
                //create new thread for each new client
                BankThread bt = new BankThread(clientSocket);
                bt.start();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
