package me.joba.pathtracercluster;

import com.esotericsoftware.kryonet.Connection;
import java.util.concurrent.atomic.AtomicInteger;
import me.joba.pathtracercluster.packets.PacketClient05TaskCompleted;
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
    private final double minZ, maxZ;
    private final int rayCount;
    private final int priority; //Higher value <=> Higher priority
    private AtomicInteger count = new AtomicInteger(1);
    private Connection serverId;
    
    public Task(Scene scene, Plotter plotter, Connection owner, PacketServer02SendTask packet) {
        this(scene, plotter, owner, packet.getMinX(), packet.getMaxX(), packet.getMinZ(), packet.getMaxZ(), packet.getRayCount(), packet.getPriority());
    }
    
    private Task(Scene scene, Plotter plotter, UUID serverId, double minX, double maxX, double minZ, double maxZ, int rayCount, int priority) {
        this.scene = scene;
        this.plotter = plotter;
        this.minX = minX;
        this.maxX = maxX;
        this.minZ = minZ;
        this.maxZ = maxZ;
        this.rayCount = rayCount;
        this.priority = priority;
        this.serverId = serverId;
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

    public double getMinZ() {
        return minZ;
    }

    public double getMaxZ() {
        return maxZ;
    }

    public int getRayCount() {
        return rayCount;
    }

    public int getPriority() {
        return priority;
    }

    public void signalDone() {
        if(count.decrementAndGet() == 0 && serverId.isConnected()) {
            PacketClient05TaskCompleted packet = new PacketClient05TaskCompleted(plotter.getCIEVector());
            serverId.sendTCP(packet);
        }
    }

    public Connection getOwner() {
        return serverId;
    }
    
    public Task[] split(int shards) {
        count = null;
        Task[] tasks = new Task[shards];
        int raysPerShard = rayCount / shards;
        AtomicInteger count = new AtomicInteger(shards);
        for (int i = 0; i < shards - 1; i++) {
            tasks[i] = new Task(scene, plotter, serverId, minX, maxX, minZ, maxZ, raysPerShard, priority);
            tasks[i].count = count;
        }
        tasks[shards - 1] = new Task(scene, plotter, serverId, minX, maxX, minZ, maxZ, rayCount - raysPerShard * (shards - 1), priority);
        tasks[shards - 1].count = count;
        return tasks;
    }
    
    @Override
    public int compareTo(Task o) {
        return Integer.compare(priority, o.priority);
    }
}
