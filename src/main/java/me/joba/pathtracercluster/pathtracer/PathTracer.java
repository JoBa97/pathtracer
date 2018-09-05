package me.joba.pathtracercluster.pathtracer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.PriorityBlockingQueue;
import me.joba.pathtracercluster.Task;
import me.joba.pathtracercluster.pathtracer.RenderThread.RenderState;

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
            threads[i].setName("RenderThread #" + i);
        }
    }
    
    public int getQueueSize() {
        int size = taskQueue.size();
        if(size == 0) {
            return -(int)Arrays
                    .stream(threads)
                    .filter(rt -> rt.getRenderState() == RenderState.IDLE)
                    .count();
        }
        return size;
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
        int shardCount = Math.max(1, Math.min(threads.length, task.getRayCount() / minRayCount));
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
    
    public void printStats() {
        int blocks = 20;
        System.out.println("[======== INFO ========]");
        System.out.println("Queue size: " + taskQueue.size());
        for (int i = 0; i < threads.length; i++) {
            RenderState state = threads[i].getRenderState();
            System.out.print(threads[i].getName() + ": ");
            if(state == RenderState.TRACING) {
                int cRays = threads[i].getCurrentRayCount();
                int tRays = threads[i].getTotalRayCount();
                float percentage = (float)cRays / (float)tRays;
                int filledBlocks = (int)Math.floor(percentage * blocks);
                char[] bar = new char[blocks];
                Arrays.fill(bar, 0, filledBlocks, '#');
                Arrays.fill(bar, filledBlocks, blocks, ' ');
                System.out.print("[" + new String(bar) + "] " + (int)Math.floor(percentage * 100) + "%");
                System.out.print("(" + cRays + "/" + tRays + ")");
            }
            else if(state == RenderState.PLOTTING) {
                System.out.print("Plotting...");
            }
            else if(state == RenderState.IDLE) {
                System.out.print("Idle");
            }
            System.out.println();
        }
    }
}
