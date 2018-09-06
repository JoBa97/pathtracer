package me.joba.pathtracercluster.packets;

import java.util.UUID;
import me.joba.pathtracercluster.pathtracer.Scene;

/**
 *
 * @author balsfull
 */
public class PacketServer04SendScene {
    
    private UUID sceneId;
    private Scene scene;
    
    public PacketServer04SendScene() {
        
    }

    public PacketServer04SendScene(UUID sceneId, Scene scene) {
        this.sceneId = sceneId;
        this.scene = scene;
    }
    
    public UUID getSceneId() {
        return sceneId;
    }
    
    public Scene getScene() {
        return scene;
    }
}
