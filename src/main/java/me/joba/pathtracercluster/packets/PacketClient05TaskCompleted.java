package me.joba.pathtracercluster.packets;

import me.joba.pathtracercluster.pathtracer.Vector3;

/**
 *
 * @author balsfull
 */
public class PacketClient05TaskCompleted {
    
    private Vector3[] cieVector;
    private int offset;
    
    public PacketClient05TaskCompleted() {
        
    }

    public PacketClient05TaskCompleted(Vector3[] cieVector, int offset) {
        this.cieVector = cieVector;
        this.offset = offset;
    }

    public Vector3[] getCIEVector() {
        return cieVector;
    }

    public int getOffset() {
        return offset;
    }
}
