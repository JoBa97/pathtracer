/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pathtracer.master;

import java.io.File;
import java.net.URL;
import pathtracer.PathTracer;

/**
 *
 * @author jonas
 */
public class PathTracerMaster {
    
    public static void start(String[] sshList) throws Exception {
        URL location = PathTracer.class.getProtectionDomain().getCodeSource().getLocation();
        File file = new File(location.toURI());
        System.out.println("Transfering " + location);
        for(String ssh : sshList) {
            String command = "scp " + file + " " + ssh + ":" + file.getName();
        }
    }
}
