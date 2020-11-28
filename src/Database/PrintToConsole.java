package Database;

import java.io.PrintWriter;

public class PrintToConsole extends Task {
    private String text;

    public PrintToConsole(String text){
        this.text = text;
    }
    @Override
    public void Execute() {
        System.out.println(text);
    }
}
