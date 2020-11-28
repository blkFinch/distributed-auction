package Database;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class TaskRunner  extends Thread{
    public static BlockingQueue<Task> jobQueue;

    public TaskRunner(){
        jobQueue = new LinkedBlockingDeque<>();
    }

    @Override
    public void run() {
        Task current;

        while(true){
            try {
                current = jobQueue.take();
                current.Execute();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
