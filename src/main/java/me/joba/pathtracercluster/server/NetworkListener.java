package me.joba.pathtracercluster.server;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.joba.pathtracercluster.NetworkRegistration;
import me.joba.pathtracercluster.packets.PacketClient03GetScene;
import me.joba.pathtracercluster.packets.PacketClient05TaskCompleted;
import me.joba.pathtracercluster.packets.PacketClient06Info;
import me.joba.pathtracercluster.packets.PacketServer01Hello;
import me.joba.pathtracercluster.packets.PacketServer04SendScene;
import me.joba.pathtracercluster.pathtracer.Scene;

/**
 *
 * @author balsfull
 */
public class NetworkListener extends Listener {
    
    private final Map<Connection, ConnectionData> clients;
    private final UUID serverId;
    private final UUID sceneId;
    private final Scene scene;
    private final TaskScheduler scheduler;
    
    public NetworkListener(Scene scene, UUID serverId, UUID sceneId, TaskScheduler scheduler, InetAddress[] servers) throws IOException {
        clients = new ConcurrentHashMap<>();
        for (int i = 0; i < servers.length; i++) {
            Client client = new Client(16384, 16384);
            client.start();
            NetworkRegistration.register(client);
            client.addListener(this);
            client.connect(10000, servers[i], NetworkRegistration.DEFAULT_PORT);
        }
        this.scene = scene;
        this.sceneId = sceneId;
        this.serverId = serverId;
        this.scheduler = scheduler;
    }
    
    static class PathTracerConnection extends Connection {
        
    }
    
    @Override
    public void connected(Connection c) {
        System.out.println("Sending ID: " + serverId);
        c.sendTCP(new PacketServer01Hello(serverId));
        ConnectionData cdata = new ConnectionData(c);
        clients.put(c, cdata);
        scheduler.addConnection(cdata);
    }
    
    @Override
    public void disconnected(Connection c) {
        scheduler.removeConnection(clients.remove(c));
    }
    
    @Override
    public void received(Connection c, Object o) {
        if(o instanceof PacketClient03GetScene) {
            System.out.println("Get scene " + c.getRemoteAddressTCP());
            if(((PacketClient03GetScene) o).getSceneId().equals(sceneId)) {
                c.sendTCP(new PacketServer04SendScene(sceneId, scene));
            }
        }
        else if(o instanceof PacketClient05TaskCompleted) {
            PacketClient05TaskCompleted packet = (PacketClient05TaskCompleted)o;
            scheduler.acceptPlotterData(packet.getCIEVector(), packet.getOffset());
        }
        else if(o instanceof PacketClient06Info) {
            PacketClient06Info packet = (PacketClient06Info)o;
            ConnectionData data = clients.get(c);
            data.setQueueSize(packet.getQueueSize());
            data.setTotalRayCount(packet.getTotalRayCount());
        }
    }
}
