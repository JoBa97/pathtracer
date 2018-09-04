package me.joba.pathtracercluster.server;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import java.util.UUID;
import me.joba.pathtracercluster.NetworkRegistration;
import me.joba.pathtracercluster.packets.PacketClient03GetScene;
import me.joba.pathtracercluster.packets.PacketClient05TaskCompleted;
import me.joba.pathtracercluster.packets.PacketServer04SendScene;
import me.joba.pathtracercluster.pathtracer.Scene;

/**
 *
 * @author balsfull
 */
public class NetworkListener extends Listener {
    
    private final Client client;
    private final UUID sceneId;
    private final Scene scene;
    
    public NetworkListener(int port, Scene scene) {
        client = new Client();
        NetworkRegistration.register(client);
        client.addListener(this);
        this.scene = scene;
    }
    
    static class PathTracerConnection extends Connection {
        
        public PathTracerState state = PathTracerState.READY;
        
        static enum PathTracerState {
            READY
        }
    }
    
    @Override
    public void received(Connection c, Object o) {
        if(o instanceof PacketClient03GetScene) {
            if(((PacketClient03GetScene) o).getSceneId() == sceneId) {
                c.sendTCP(new PacketServer04SendScene(sceneId, scene));
            }
        }
        if(o instanceof PacketClient05TaskCompleted) {
            if(((PacketClient03GetScene) o).getSceneId() == sceneId) {
                c.sendTCP(new PacketServer04SendScene(sceneId, scene));
            }
        }
    }
}
