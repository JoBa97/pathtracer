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
    private int totalRayCount;
    private RenderState state = RenderState.IDLE;
    
    public RenderThread(BlockingQueue<Task> taskQueue) {
        this.taskQueue = taskQueue;
        this.tracer = new Tracer();
    }
    
    @Override
    public void run() {
        System.out.println("Started " + this.getName());
        while(running) {
            try {
                Task task = taskQueue.poll(10, TimeUnit.SECONDS);
                if(task == null) continue;
                runTask(task);
            } catch (InterruptedException ex) {}
        }
    }
    
    private void runTask(Task task) {
        state = RenderState.TRACING;
        totalRayCount = task.getRayCount();
        tracer.render(task.getScene(), task.getMinX(), task.getMaxX(), task.getMinY(), task.getMaxY(), task.getRayCount());
        state = RenderState.PLOTTING;
        task.getPlotter().plot(tracer.getPhotons());
        state = RenderState.IDLE;
        task.signalDone();
    }

    public int getTotalRayCount() {
        return totalRayCount;
    }
    
    public int getCurrentRayCount() {
        return tracer.getCurrentRayCount();
    }
    
    public void signalEnd() {
        running = false;
    }
    
    public RenderState getRenderState() {
        return state;
    }
    
    public static enum RenderState {
        IDLE,
        TRACING,
        PLOTTING
    }
}
