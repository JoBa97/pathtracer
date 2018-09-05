package me.joba.pathtracercluster;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import me.joba.pathtracercluster.client.NetworkListener;
import me.joba.pathtracercluster.packets.PacketServer02SendTask;
import me.joba.pathtracercluster.pathtracer.Scene;
import me.joba.pathtracercluster.pathtracer.render.Plotter;

/**
 *
 * @author balsfull
 */
public class Task implements Comparable<Task> {
    
    private final Scene scene;
    private final Plotter plotter;
    private final double minX, maxX;
    private final double minY, maxY;
    private final int rayCount;
    private final int priority; //Higher value <=> Higher priority
    private final UUID serverId;
    private final NetworkListener networkListener;
    private AtomicInteger count = new AtomicInteger(1);
    
    public Task(Scene scene, Plotter plotter, UUID serverId, NetworkListener networkListener, PacketServer02SendTask packet) {
        this(scene, plotter, serverId, networkListener, packet.getMinX(), packet.getMaxX(), packet.getMinY(), packet.getMaxY(), packet.getRayCount(), packet.getPriority());
    }
    
    private Task(Scene scene, Plotter plotter, UUID serverId, NetworkListener networkListener, double minX, double maxX, double minY, double maxY, int rayCount, int priority) {
        this.scene = scene;
        this.plotter = plotter;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.rayCount = rayCount;
        this.priority = priority;
        this.serverId = serverId;
        this.networkListener = networkListener;
    }

    public Scene getScene() {
        return scene;
    }

    public Plotter getPlotter() {
        return plotter;
    }

    public double getMinX() {
        return minX;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxY() {
        return maxY;
    }

    public int getRayCount() {
        return rayCount;
    }

    public int getPriority() {
        return priority;
    }

    public void signalDone() {
        if(count.decrementAndGet() == 0) {
            networkListener.sendCompletion(this);
        }
    }

    public UUID getServerId() {
        return serverId;
    }
    
    public Task[] split(int shards) {
        count = null;
        Task[] tasks = new Task[shards];
        int raysPerShard = rayCount / shards;
        AtomicInteger count = new AtomicInteger(shards);
        for (int i = 0; i < shards - 1; i++) {
            tasks[i] = new Task(scene, plotter, serverId, networkListener, minX, maxX, minY, maxY, raysPerShard, priority);
            tasks[i].count = count;
        }
        tasks[shards - 1] = new Task(scene, plotter, serverId, networkListener, minX, maxX, minY, maxY, rayCount - raysPerShard * (shards - 1), priority);
        tasks[shards - 1].count = count;
        return tasks;
    }
    
    @Override
    public int compareTo(Task o) {
        return Integer.compare(priority, o.priority);
    }
}
