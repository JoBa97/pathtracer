package me.joba.pathtracercluster.packets;

import me.joba.pathtracercluster.pathtracer.Vector3;

/**
 *
 * @author balsfull
 */
public class PacketClient05TaskCompleted {
    
    private final Vector3[] cieVector;

    public PacketClient05TaskCompleted(Vector3[] cieVector) {
        this.cieVector = cieVector;
    }

    public Vector3[] getCIEVector() {
        return cieVector;
    }
}
