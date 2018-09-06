package pathtracer.standalone;

import pathtracer.Scene;
import pathtracer.render.Plotter;
import pathtracer.render.Tracer;

/**
 *
 * @author balsfull
 */
public class RenderThread extends Thread {
    
    private final Scene scene;
    private final Tracer tracer;
    private final Plotter plotter;
    private boolean running = true;
    
    public RenderThread(Scene scene, Tracer tracer, Plotter plotter) {
        this.scene = scene;
        this.tracer = tracer;
        this.plotter = plotter;
    }
    
    @Override
    public void run() {
        while(running) {
            tracer.render(scene);
            plotter.plot(tracer.getPhotons());
        }
    }
    
    public void signalEnd() {
        running = false;
    }
}
