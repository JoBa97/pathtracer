package me.joba.pathtracercluster.packets;

import java.util.UUID;

/**
 *
 * @author balsfull
 */
public class PacketServer02SendTask {
    
    private UUID sceneId;
    private double minX = -1, maxX = 1;
    private double minY = -1, maxY = 1;
    private int rayCount = 1000;
    private int priority = 0; //Higher value <=> Higher priority
    
    public PacketServer02SendTask() {
        
    }
    
    public PacketServer02SendTask(UUID sceneId) {
        this.sceneId = sceneId;
    }
    
    public void setSceneId(UUID sceneId) {
        this.sceneId = sceneId;
    }

    public void setMinX(double minX) {
        this.minX = minX;
    }

    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    public void setMinY(double minZ) {
        this.minY = minZ;
    }

    public void setMaxY(double maxZ) {
        this.maxY = maxZ;
    }

    public void setRayCount(int rayCount) {
        this.rayCount = rayCount;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public UUID getSceneId() {
        return sceneId;
    }

    public double getMinX() {
        return minX;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxY() {
        return maxY;
    }

    public int getRayCount() {
        return rayCount;
    }

    public int getPriority() {
        return priority;
    }
}
