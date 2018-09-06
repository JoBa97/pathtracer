package pathtracer.standalone;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import pathtracer.PathTracer;
import pathtracer.ProgressTracker;
import pathtracer.Scene;
import static pathtracer.PathTracer.HEIGHT;
import static pathtracer.PathTracer.WIDTH;
import pathtracer.render.Plotter;
import pathtracer.render.ToneMapper;
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
public class PathTracerStandalone {
    
    public static void render(Scene scene) throws Exception {
        int threadCount = 10;
        RenderThread[] rt = new RenderThread[threadCount];
        Tracer[] tracers = new Tracer[threadCount];
        Plotter plotter = new Plotter(WIDTH, HEIGHT);
        for (int i = 0; i < rt.length; i++) {
            Tracer tracer = new Tracer(WIDTH, HEIGHT);
            tracers[i] = tracer;
            rt[i] = new RenderThread(scene, tracer, plotter);
        }
        ProgressTracker progressThread = new ProgressTracker(tracers);
        progressThread.setPriority(10);
        progressThread.start();
        for (RenderThread r : rt) {
            r.start();
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void start() {
                try {
                    for(RenderThread t : rt) {
                        t.signalEnd();
                    }
                    progressThread.signalEnd();
                    for(RenderThread t : rt) {
                        t.join();
                    }
                    ToneMapper mapper = new ToneMapper(WIDTH, HEIGHT);
                    mapper.tonemap(plotter.getCIEVector());
                    int[] rgb = mapper.getRGB();
                    BufferedImage bi = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
                    bi.setRGB(0, 0, WIDTH, HEIGHT, rgb, 0, WIDTH);
                    ImageIO.write(bi, "png", new File("rendered.png"));
                } catch (Exception ex) {
                    Logger.getLogger(PathTracer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
}
