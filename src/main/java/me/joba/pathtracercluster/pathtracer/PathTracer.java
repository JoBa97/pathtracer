package me.joba.pathtracercluster.pathtracer;

import java.awt.image.BufferedImage;
import static java.awt.image.ImageObserver.HEIGHT;
import static java.awt.image.ImageObserver.WIDTH;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import me.joba.pathtracercluster.Task;
import me.joba.pathtracercluster.pathtracer.render.Plotter;
import me.joba.pathtracercluster.pathtracer.render.ToneMapper;
import me.joba.pathtracercluster.pathtracer.render.Tracer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jonas
 */
public class PathTracer {
    
    private final Map<UUID, Scene> sceneMap;
    private final int minRayCount;
    private final PriorityBlockingQueue<Task> taskQueue;
    private final RenderThread[] threads;
    
    public PathTracer(int minRayCount, int threadCount) {
        this.sceneMap = new HashMap<>();
        this.minRayCount = minRayCount;
        this.taskQueue = new PriorityBlockingQueue<>();
        threads = new RenderThread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new RenderThread(taskQueue);
        }
    }
    
    public void registerScene(UUID uuid, Scene scene) {
        sceneMap.put(uuid, scene);
    }
    
    public Scene getScene(UUID uuid) {
        return sceneMap.get(uuid);
    }
    
    public void queueTask(Task task) {
        if(minRayCount < 0) {
            taskQueue.add(task);
            return;
        }
        int cpus = Runtime.getRuntime().availableProcessors();
        int shardCount = Math.min(cpus, task.getRayCount() / minRayCount);
        if(shardCount == 1) {
            taskQueue.add(task);
            return;
        }
        Task[] shards = task.split(shardCount);
        for (Task shard : shards) {
            taskQueue.add(shard);
        }
    }
    
    public void start() {
        for(RenderThread rt : threads) {
            rt.start();
        }
    }
    
    public void stop() throws InterruptedException {
        for(RenderThread rt : threads) {
            rt.signalEnd();
        }
        for(RenderThread rt : threads) {
            rt.join();
        }
    }
    
    public static void render(Scene scene) throws Exception {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void start() {
                try {
                    ToneMapper mapper = new ToneMapper(WIDTH, HEIGHT);
                    mapper.tonemap(plotter.getCIEVector());
                    int[] rgb = mapper.getRGB();
                    BufferedImage bi = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
                    bi.setRGB(0, 0, WIDTH, HEIGHT, rgb, 0, WIDTH);
                    ImageIO.write(bi, "png", new File("rendered.png"));
                } catch (IOException ex) {
                    Logger.getLogger(PathTracer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
}
