/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pathtracer.slave;

import java.nio.ByteBuffer;
import java.util.Base64.Encoder;
import pathtracer.Scene;
import pathtracer.render.Tracer;
import pathtracer.render.Tracer.Photon;

/**
 *
 * @author jonas
 */
public class SlaveRenderThread extends Thread {
    
    private final Scene scene;
    private final Tracer tracer;
    private boolean running = true;
    
    public SlaveRenderThread(Scene scene, Tracer tracer) {
        this.scene = scene;
        this.tracer = tracer;
    }
    
    @Override
    public void run() {
        Encoder encoder = java.util.Base64.getEncoder();
        while(running) {
            tracer.render(scene);
            Photon[] photons = tracer.getPhotons();
            ByteBuffer buffer = ByteBuffer.allocate(Double.BYTES * photons.length * 4);
            for(Photon photon : photons) {
                buffer.putDouble(photon.x);
                buffer.putDouble(photon.y);
                buffer.putDouble(photon.wavelength);
                buffer.putDouble(photon.probability);
            }
            String out = encoder.encodeToString(buffer.array());
            System.err.println(out);
        }
    }
    
    public void signalEnd() {
        running = false;
    }
}
