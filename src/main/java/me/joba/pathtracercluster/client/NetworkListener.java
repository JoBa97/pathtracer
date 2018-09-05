package me.joba.pathtracercluster.client;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import me.joba.pathtracercluster.NetworkRegistration;
import me.joba.pathtracercluster.Task;
import me.joba.pathtracercluster.client.NetworkListener.PathTracerConnection.PathTracerState;
import me.joba.pathtracercluster.packets.*;
import me.joba.pathtracercluster.pathtracer.PathTracer;
import me.joba.pathtracercluster.pathtracer.Scene;
import me.joba.pathtracercluster.pathtracer.render.Plotter;

/**
 *
 * @author balsfull
 */
public class NetworkListener extends Listener {
    
    private final Server server;
    private final PathTracer tracer;
    private final Map<UUID, PriorityBlockingQueue<PacketServer02SendTask>> taskPacketQueue;
    private final Map<UUID, PriorityBlockingQueue<PacketClient05TaskCompleted>> resultPacketQueue;
    
    public NetworkListener(int port, PathTracer tracer) throws IOException {
        server = new Server();
        server.bind(port);
        NetworkRegistration.register(server);
        server.addListener(this);
        this.tracer = tracer;
        this.taskPacketQueue = new ConcurrentHashMap<>();
        this.resultPacketQueue = new ConcurrentHashMap<>();
    }

    public void sendUpdate(Task task) {
        PacketClient05TaskCompleted packet = new PacketClient05TaskCompleted(task.getPlotter().getCIEVector());        
        for(Connection con : server.getConnections()) {
            PathTracerConnection ptc = (PathTracerConnection)con;
            if(ptc.isConnected() && ptc.serverId == task.getServerId()) { 
                if(ptc.sendTCP(packet) > 0) {
                    return;
                }
                else {
                    break;
                }
            }
        }
        PriorityBlockingQueue<PacketClient05TaskCompleted> queue = resultPacketQueue.putIfAbsent(task.getServerId(), new PriorityBlockingQueue<>());
        queue.put(packet);
    }
    
    static class PathTracerConnection extends Connection {
        
        public PathTracerState state = PathTracerState.PRE_INIT;
        public UUID serverId;
        
        static enum PathTracerState {
            PRE_INIT,
            READY;
        }
    }
    
    private void queuePacket(PacketServer02SendTask packet) {
        PriorityBlockingQueue<PacketServer02SendTask> queue = taskPacketQueue.putIfAbsent(packet.getSceneId(), new PriorityBlockingQueue<>());
        if(queue == null) {
            queue = new PriorityBlockingQueue<>();
            taskPacketQueue.put(packet.getSceneId(), queue);
        }
        queue.add(packet);
    }
    
    @Override
    public void received(Connection c, Object o) {
        PathTracerConnection ptc = (PathTracerConnection)c;
        if(o instanceof PacketServer01Hello) {
            PacketServer01Hello packet = (PacketServer01Hello)o;
            if(ptc.state != PathTracerState.PRE_INIT) {
                c.close();
                return;
            }
            ptc.serverId = packet.getServerId();
            ptc.state = PathTracerState.PRE_INIT;
            drainResultPacketQueue(ptc.serverId, c);
        }
        if(ptc.state == PathTracerState.PRE_INIT) {
            c.close();
            return;
        }
        if(o instanceof PacketServer02SendTask) {
            PacketServer02SendTask packet = (PacketServer02SendTask)o;
            Scene scene = tracer.getScene(packet.getSceneId());
            if(scene != null) {
                Plotter plotter = new Plotter(scene.getWidth(), scene.getHeight());
                Task task = new Task(scene, plotter, ptc.serverId, this, packet);
                tracer.queueTask(task);
            }
            else {
                queuePacket(packet);
            }
        }
        else if(o instanceof PacketServer04SendScene) {
            PacketServer04SendScene packet = (PacketServer04SendScene)o;
            tracer.registerScene(packet.getSceneId(), packet.getScene());
            drainTaskPacketQueue(packet.getSceneId(), ptc.serverId, packet.getScene());
        }
    }
    
    private void drainResultPacketQueue(UUID serverId, Connection connection) {
        Queue<PacketServer02SendTask> queue = taskPacketQueue.get(serverId);
        if(queue == null) return;
        for(PacketServer02SendTask packet : queue) {
            if(connection.sendTCP(packet) == 0) {
                queue.add(packet);
                break;
            }
        }
    }
    
    private void drainTaskPacketQueue(UUID sceneId, UUID serverId, Scene scene) {
        Queue<PacketServer02SendTask> queue = taskPacketQueue.get(sceneId);
        if(queue == null) return;
        for(PacketServer02SendTask packet : queue) {
            Plotter plotter = new Plotter(scene.getWidth(), scene.getHeight());
            Task task = new Task(scene, plotter, serverId, this, packet);
            tracer.queueTask(task);
        }
    }
}
