package me.joba.pathtracercluster;

import com.martiansoftware.jsap.JSAPException;
import java.io.IOException;
import me.joba.pathtracercluster.client.ClientMain;
import me.joba.pathtracercluster.server.ServerMain;

/**
 *
 * @author balsfull
 */
public class Main {
    
    public static void main(String[] args) throws Exception {
        if(args.length >= 1 && args[0].equals("--server")) {
            ServerMain.main(args);
        }
        else {
            ClientMain.main(args);
        }
    }
}
