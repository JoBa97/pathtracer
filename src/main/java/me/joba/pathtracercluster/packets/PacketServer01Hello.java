package me.joba.pathtracercluster.packets;

import java.util.UUID;

/**
 *
 * @author balsfull
 */
public class PacketServer01Hello {
    
    private UUID serverId;
    
    public PacketServer01Hello() {
        
    }
    
    public PacketServer01Hello(UUID serverId) {
        this.serverId = serverId;
    }

    public UUID getServerId() {
        return serverId;
    }
}
