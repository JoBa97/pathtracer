package me.joba.pathtracercluster.server;

import com.esotericsoftware.kryonet.Connection;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author balsfull
 */
public class ConnectionData {
    
    private AtomicInteger queueSize = new AtomicInteger(0);
    private AtomicInteger totalRayCount = new AtomicInteger(0);
    private final Connection connection;

    public ConnectionData(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }
    
    public void setTotalRayCount(int v) {
        totalRayCount.updateAndGet(i -> i < v ? v : i);
    }

    public void incrementQueueSize(int delta) {
        queueSize.addAndGet(delta);
    }
    
    public void setQueueSize(int size) {
        queueSize.set(size);
    }
    
    public int getQueueSize() {
        return queueSize.get();
    }

    public int getTotalRayCount() {
        return totalRayCount.get();
    }
}
