package Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class BankThread extends Thread {
    protected Socket socket;

    public BankThread(Socket clientSocket){
        this.socket = clientSocket;
    }

    public void run(){
        PrintWriter out = null;
        BufferedReader in = null;

        try{
             out =
                    new PrintWriter(socket.getOutputStream(), true);
             in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            out.println("connected to bank!");
        }catch (IOException e){
            System.out.println("errr");
            System.out.println(e);
            return;
        }
        String fromClient;
        //while connected to bank read in and print to console
        while(true){
            try{
                fromClient = in.readLine();
                out.println(fromClient);
            }catch (IOException e){
                e.printStackTrace();
            }return;
        }
    }
}
