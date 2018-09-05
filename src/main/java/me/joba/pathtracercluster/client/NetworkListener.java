package me.joba.pathtracercluster.client;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.util.TcpIdleSender;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import me.joba.pathtracercluster.NetworkRegistration;
import me.joba.pathtracercluster.Task;
import me.joba.pathtracercluster.packets.*;
import me.joba.pathtracercluster.pathtracer.PathTracer;
import me.joba.pathtracercluster.pathtracer.Scene;
import me.joba.pathtracercluster.pathtracer.Vector3;
import me.joba.pathtracercluster.pathtracer.render.Plotter;
import me.joba.pathtracercluster.pathtracer.render.ToneMapper;

/**
 *
 * @author balsfull
 */
public class NetworkListener extends Listener {
    
    private final Server server;
    private final PathTracer tracer;
    private final Map<UUID, BlockingQueue<PacketServer02SendTask>> taskPacketQueue;
    private final Map<UUID, BlockingQueue<PacketClient05TaskCompleted>> resultPacketQueue;
    
    public NetworkListener(int port, PathTracer tracer) throws IOException {
        server = new Server(16384, 16384) {
            @Override
            protected Connection newConnection () {
                return new PathTracerConnection();
            }
        };
        server.start();
        server.bind(port);
        NetworkRegistration.register(server);
        server.addListener(this);
        this.tracer = tracer;
        this.taskPacketQueue = new ConcurrentHashMap<>();
        this.resultPacketQueue = new ConcurrentHashMap<>();
    }

    public void sendCompletion(Task task) {
        int size = 100;
        int offset = 0;
        List<PacketClient05TaskCompleted> taskFragments = new ArrayList<>();
        Vector3[] cieVector = task.getPlotter().getCIEVector();
        while(offset < cieVector.length) {
            Vector3[] fragment = new Vector3[Math.min(size, cieVector.length - offset)];
            System.arraycopy(cieVector, offset, fragment, 0, fragment.length);
            taskFragments.add(new PacketClient05TaskCompleted(fragment, offset));
            offset += fragment.length;
        }
        Connection connection = null;
        for(Connection con : server.getConnections()) {
            PathTracerConnection ptc = (PathTracerConnection)con;
            if(ptc.isConnected() && ptc.serverId == task.getServerId()) {
                connection = con;
                break;
            }
        }
        if(connection != null) {
            Iterator<PacketClient05TaskCompleted> iter = taskFragments.iterator();
            connection.addListener(new TcpIdleSender() {
                @Override
                protected Object next() {
                    if(!iter.hasNext()) return null;
                    return iter.next();
                }
            });
            return;
        }
        BlockingQueue<PacketClient05TaskCompleted> queue = new LinkedBlockingQueue<>();
        BlockingQueue<PacketClient05TaskCompleted> tmp = resultPacketQueue.putIfAbsent(task.getServerId(), queue);
        if(tmp != null) {
            queue = tmp;
        }
        queue.addAll(taskFragments);
    }

    public void sendInfo(int queueSize, int rayCount) {
        server.sendToAllTCP(new PacketClient06Info(queueSize, rayCount));
    }
    
    static class PathTracerConnection extends Connection {
        
        public UUID serverId;
    }
    
    private void queuePacket(PacketServer02SendTask packet) {
        BlockingQueue<PacketServer02SendTask> queue = new LinkedBlockingQueue<>();
        BlockingQueue<PacketServer02SendTask> tmp = taskPacketQueue.putIfAbsent(packet.getSceneId(), queue);
        if(tmp != null) {
            queue = tmp;
        }
        queue.add(packet);
    }
    
    @Override
    public void connected(Connection c) {
        System.out.println("Connected with " + c.getRemoteAddressTCP());
        c.sendTCP(new PacketServer01Hello(null));
    }
    
    @Override
    public void received(Connection c, Object o) {
        PathTracerConnection ptc = (PathTracerConnection)c;
        if(o instanceof PacketServer01Hello) {
            PacketServer01Hello packet = (PacketServer01Hello)o;
            if(ptc.serverId != null) {
                c.close();
                return;
            }
            ptc.serverId = packet.getServerId();
            System.out.println("Received hello " + c.getRemoteAddressTCP() + ". UUID: " + ptc.serverId);
            drainResultPacketQueue(ptc.serverId, c);
        }
        if(ptc.serverId == null) {
            c.close();
            return;
        }
        if(o instanceof PacketServer02SendTask) {
            System.out.println("Received task " + c.getRemoteAddressTCP());
            PacketServer02SendTask packet = (PacketServer02SendTask)o;
            Scene scene = tracer.getScene(packet.getSceneId());
            if(scene != null) {
                Plotter plotter = new Plotter(scene.getWidth(), scene.getHeight());
                Task task = new Task(scene, plotter, ptc.serverId, this, packet);
                tracer.queueTask(task);
            }
            else {
                queuePacket(packet);
                c.sendTCP(new PacketClient03GetScene(packet.getSceneId()));
            }
        }
        else if(o instanceof PacketServer04SendScene) {
            System.out.println("Received scene " + c.getRemoteAddressTCP());
            PacketServer04SendScene packet = (PacketServer04SendScene)o;
            tracer.registerScene(packet.getSceneId(), packet.getScene());
            drainTaskPacketQueue(packet.getSceneId(), ptc.serverId, packet.getScene());
        }
    }
    
    private void drainResultPacketQueue(UUID serverId, Connection connection) {
        Queue<PacketClient05TaskCompleted> queue = resultPacketQueue.get(serverId);
        if(queue == null) return;
        Iterator<PacketClient05TaskCompleted> iter = queue.iterator();
        connection.addListener(new TcpIdleSender() {
            @Override
            protected Object next() {
                if(!iter.hasNext()) return null;
                return iter.next();
            }
        });
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
