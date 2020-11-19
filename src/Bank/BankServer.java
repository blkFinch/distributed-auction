package Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class BankServer {
    private Bank bank = new Bank();

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println(
                    "Usage: java BankServer <port number>");
            System.exit(1);
        }

        //I'm setting this port 6000 in my config -gh
        int portNumber = Integer.parseInt(args[0]);//6000
        System.out.println("listening...");

        try (
                ServerSocket serverSocket = new ServerSocket(portNumber);
                Socket clientSocket = serverSocket.accept();
                PrintWriter out =
                    new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
        ){
            out.println("connected to bank!");
        }
    }
}
