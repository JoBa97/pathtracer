package pathtracer.slave;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import pathtracer.Scene;
import static pathtracer.PathTracer.HEIGHT;
import static pathtracer.PathTracer.THREADS;
import static pathtracer.PathTracer.WIDTH;
import pathtracer.render.Tracer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jonas
 */
public class PathTracerSlave {
    
    private static SlaveRenderThread[] slaveThreads;
    
    public static void start(Scene scene) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while(running) {
            String command = scanner.nextLine();
            if(command.startsWith("set")) {
                setSetting(command.substring(4));
                ok();
                continue;
            }
            switch(command) {
                case "terminate": {
                    ok();
                    System.exit(0);
                    break;
                }
                case "start": {
                    render(scene);
                    ok();
                    break;
                }
                case "stop": {
                    stop();
                    ok();
                    break;
                }
            }
        }
    }
    
    private static void ok() {
        System.out.println("ok");
    }
    
    private static void render(Scene scene) {
        System.out.println("Thread count: " + THREADS);
        slaveThreads = new SlaveRenderThread[THREADS];
        for (int i = 0; i < THREADS; i++) {
            Tracer tracer = new Tracer(WIDTH, HEIGHT);
            slaveThreads[i] = new SlaveRenderThread(scene, tracer);
        }
        for (int i = 0; i < THREADS; i++) {
            slaveThreads[i].start();
        }
    }
    
    private static void stop() {
        if(slaveThreads == null) return;
        for (SlaveRenderThread srt : slaveThreads) {
            srt.signalEnd();
        }
        for (SlaveRenderThread srt : slaveThreads) {
            try {
                srt.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(PathTracerSlave.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void setSetting(String setting) {
        String[] split = setting.split(" ");
        switch(split[0]) {
            case "threads": {
                THREADS = Integer.parseInt(split[1]);
                break;
            }
            case "width": {
                WIDTH = Integer.parseInt(split[1]);
                break;
            }
            case "height": {
                HEIGHT = Integer.parseInt(split[1]);
                break;
            }
            case "batchsize": {
                Tracer.PHOTON_COUNT = Integer.parseInt(split[1]);
                break;
            }
        }
    }
}
