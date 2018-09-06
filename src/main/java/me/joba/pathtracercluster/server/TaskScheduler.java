package me.joba.pathtracercluster.server;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import me.joba.pathtracercluster.packets.PacketServer02SendTask;
import me.joba.pathtracercluster.pathtracer.Vector3;
import me.joba.pathtracercluster.pathtracer.render.Plotter;
import me.joba.pathtracercluster.pathtracer.render.ToneMapper;

/**
 *
 * @author balsfull
 */
public class TaskScheduler {
    
    private final UUID sceneId;
    private final Plotter plotter;
    private final ToneMapper toneMapper;
    private final List<ConnectionData> endpoints;
    private final int autosavePeriod;
    private final int width, height;
    private boolean active = true;
    
    public TaskScheduler(UUID sceneId, int width, int height, Plotter plotter, int autosavePeriod) {
        this.width = width;
        this.height = height;
        this.sceneId = sceneId;
        this.plotter = plotter;
        this.toneMapper = new ToneMapper(width, height);
        this.endpoints = Collections.synchronizedList(new ArrayList<>());
        this.autosavePeriod = autosavePeriod;
    }
    
    public void acceptPlotterData(Vector3[] cieVector, int offset) {
        plotter.addCIEVector(cieVector, offset);
    }
    
    public void addConnection(ConnectionData data) {
        endpoints.add(data);
    }
    
    public void removeConnection(ConnectionData data) {
        endpoints.remove(data);
    }
    
    public void shutdown() {
        active = false;
    }
    
    public void start() throws InterruptedException {
        Thread autosave = new Thread(() -> {
            try {
                Thread.sleep(autosavePeriod);
            } catch (InterruptedException ex) {
                Logger.getLogger(TaskScheduler.class.getName()).log(Level.SEVERE, null, ex);
            }
            while(active) {
                try {
                    Vector3[] cieVector = plotter.getCIEVector();
                    toneMapper.tonemap(cieVector);
                    int[] rgb = toneMapper.getRGB();
                    BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                    bi.setRGB(0, 0, width, height, rgb, 0, width);
                    ImageIO.write(bi, "png", new File("rendered.png"));
                    System.out.println("Wrote output image"); 
                    Thread.sleep(autosavePeriod);
                } catch (InterruptedException ex) {
                } catch (IOException ex) {
                    Logger.getLogger(TaskScheduler.class.getName()).log(Level.SEVERE, null, ex);
                }
                   
            }
        });
        autosave.start();
        while(active) {
            synchronized(endpoints) {
                Collections.sort(endpoints, (cd1, cd2) -> Integer.compare(cd1.getQueueSize(), cd2.getQueueSize()));
                PacketServer02SendTask packet = new PacketServer02SendTask(sceneId);
                packet.setRayCount(10000000);
                for (ConnectionData data : endpoints) {
                    if(data.getQueueSize() > 3) continue;
                    if(!data.getConnection().isConnected()) continue;
                    data.getConnection().sendTCP(packet);
                }
            }
            Thread.sleep(5000);
        }
        autosave.join();
    }
}
