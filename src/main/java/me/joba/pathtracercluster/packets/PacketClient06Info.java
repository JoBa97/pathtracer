package me.joba.pathtracercluster.packets;

/**
 *
 * @author balsfull
 */
public class PacketClient06Info {
    
    private final int queueSize;

    public PacketClient06Info(int queueSize) {
        this.queueSize = queueSize;
    }

    public int getQueueSize() {
        return queueSize;
    }
}
