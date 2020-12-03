package Bank;

import java.io.*;
import java.net.Socket;

public class BankThread extends Thread {
    protected Socket socket;
    private PrintWriter out = null;
    private BufferedReader in = null;

    public BankThread(Socket clientSocket) throws IOException {
        this.socket = clientSocket;
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void run() {
        String inputLine = null;
        String outputLine = "hello";
        LoginProtocol login = new LoginProtocol(socket);

        do {
            outputLine = login.processInput(inputLine);
            System.out.println("printing out : " + outputLine);
            out.println(outputLine);

            if (outputLine.equals("Bye.")) {
                break;
            }

            try {
                inputLine = in.readLine();
            } catch (IOException e) {
                inputLine = null;
            }
        } while (inputLine != null);
    }
}
=======
package Bank;

import Database.PrintToConsole;
import Database.Task;
import Database.TaskRunner;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

import static Bank.BankServer.activeBank; //TODO: remove after bank DB is set up

public class BankThread extends Thread {
    protected Socket socket;
    private TaskRunner runner;
    private PrintWriter out = null;
    private BufferedReader in = null;

    public BankThread(Socket clientSocket, TaskRunner runner) throws IOException {
        this.socket = clientSocket;
        this.runner = runner;
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void run() {
        String inputLine = null;
        String outputLine = "hello";
        LoginProtocol login = new LoginProtocol(socket);

        do {
            outputLine = login.processInput(inputLine);
            System.out.println("printing out : " + outputLine);
            out.println(outputLine);

            if (outputLine.equals("Bye.")) {
                break;
            }

            try {
                inputLine = in.readLine();
            } catch (IOException e) {
                inputLine = null;
            }
        } while (inputLine != null);
    }
}