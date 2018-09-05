package me.joba.pathtracercluster.packets;

import java.util.UUID;

/**
 *
 * @author balsfull
 */
public class PacketClient03GetScene {

    private UUID sceneId;
    
    public PacketClient03GetScene() {
        
    }
    
    public PacketClient03GetScene(UUID sceneId) {
        this.sceneId = sceneId;
    }

    public UUID getSceneId() {
        return sceneId;
    }
}
