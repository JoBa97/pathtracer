package me.joba.pathtracercluster.pathtracer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import me.joba.pathtracercluster.Task;
import me.joba.pathtracercluster.pathtracer.render.Tracer;

/**
 *
 * @author balsfull
 */
public class RenderThread extends Thread {
    
    private final BlockingQueue<Task> taskQueue;
    private final Tracer tracer;
    private boolean running = true;
    
    public RenderThread(BlockingQueue<Task> taskQueue) {
        this.taskQueue = taskQueue;
        this.tracer = new Tracer();
    }
    
    @Override
    public void run() {
        while(running) {
            try {
                Task task = taskQueue.poll(10, TimeUnit.SECONDS);
                if(task == null) continue;
                runTask(task);
            } catch (InterruptedException ex) {}
        }
    }
    
    private void runTask(Task task) {
        tracer.render(task.getScene(), task.getRayCount());
        task.getPlotter().plot(tracer.getPhotons());
        task.signalDone();
    }
    
    public void signalEnd() {
        running = false;
    }
}
