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
