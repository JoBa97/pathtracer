package pathtracer;

import pathtracer.render.Tracer;

/**
 *
 * @author balsfull
 */
public class ProgressTracker extends Thread{
    
    private final Tracer[] tracers;
    private boolean running = true;
    
    public ProgressTracker(Tracer... tracers) {
        this.tracers = tracers;
    }
    
    @Override
    public void run() {
        long progress = 0;
        while(running) {
            progress = 0;
            for(Tracer tracer : tracers) {
                progress += tracer.getProgress();
            }
            System.out.println("Rays: " + Math.floor(progress / 1e04) / 100 + "M");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }
        }
        progress = 0;
        long total = 0;
        for(Tracer tracer : tracers) {
            total += (tracer.getProgress() + Tracer.PHOTON_COUNT - 1) / Tracer.PHOTON_COUNT;
        }
        total *= Tracer.PHOTON_COUNT;
        while(progress < total) {
            progress = 0;
            for(Tracer tracer : tracers) {
                progress += tracer.getProgress();
            }
            System.out.println("Rays: " + Math.floor(progress / 1e04) / 100 + "M (" + (total - progress) + " left)");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }
        }
        System.out.println("Total rays cast: " + total);
    }
    
    public void signalEnd() {
        running = false;
    }
}
