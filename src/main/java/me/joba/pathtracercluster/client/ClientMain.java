package me.joba.pathtracercluster.client;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.SimpleJSAP;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.joba.pathtracercluster.NetworkRegistration;
import me.joba.pathtracercluster.pathtracer.PathTracer;

/**
 *
 * @author balsfull
 */
public class ClientMain {
    
    public static void main(String[] args) throws JSAPException, IOException {
        SimpleJSAP jsap = new SimpleJSAP(
                "PathTracer",
                "Realistic rendering",
                new Parameter[]{
                    new FlaggedOption("rays", JSAP.INTEGER_PARSER, "100000", JSAP.NOT_REQUIRED, JSAP.NO_SHORTFLAG, "rays", "Minimal ray count per shard"),
                    new FlaggedOption("threads", JSAP.INTEGER_PARSER, Integer.toString(Runtime.getRuntime().availableProcessors()), JSAP.NOT_REQUIRED, JSAP.NO_SHORTFLAG, "threads", "Thread count")
                }
        );
        JSAPResult config = jsap.parse(args);
        if (!config.success()) {
            System.err.println("                " + jsap.getUsage());
            System.exit(1);
        }
        System.out.println("Starting Client with:");
        System.out.println("Threads: " + config.getInt("threads"));
        System.out.println("MinRays: " + config.getInt("rays"));
        PathTracer tracer = new PathTracer(config.getInt("rays"), config.getInt("threads"));
        NetworkListener network = new NetworkListener(NetworkRegistration.DEFAULT_PORT, tracer);
        new Thread() {
            @Override
            public void run() {
                long lastStatus = 0;
                while(true) {
                    if(lastStatus < System.currentTimeMillis() - 5000) {
                        tracer.printStats();
                        lastStatus = System.currentTimeMillis();
                    }
                    network.sendInfo(tracer.getQueueSize(), 0);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }.start();
        tracer.start();
    }
}
