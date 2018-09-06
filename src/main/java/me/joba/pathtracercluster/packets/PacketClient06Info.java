package me.joba.pathtracercluster.packets;

/**
 *
 * @author balsfull
 */
public class PacketClient06Info {
    
    private int queueSize;
    private int totalRayCount;
    
    public PacketClient06Info() {
        
    }
    
    public PacketClient06Info(int queueSize, int totalRayCount) {
        this.queueSize = queueSize;
        this.totalRayCount = totalRayCount;
    }

    public int getTotalRayCount() {
        return totalRayCount;
    }

    public int getQueueSize() {
        return queueSize;
    }
}
