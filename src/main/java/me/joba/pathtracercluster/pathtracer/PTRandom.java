package me.joba.pathtracercluster.pathtracer;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author balsfull
 */
public class PTRandom {
    
    public static Random getRand() {
        return ThreadLocalRandom.current();
    }
    
    public static double getUnit() {
        return getRand().nextDouble();
    }
    
    public static double getBiUnit() {
        return getRand().nextDouble() * 2 - 1;
    }
    
    public static double getLongitude() {
        return getRand().nextDouble() * Math.PI * 2;
    }
    
    public static double getWavelength() {
        return getUnit() * 400 + 380;
    }
    
    public static Vector3 getHemisphereVector() {
        double phi = getLongitude();
        double rq = getUnit();
        double r = Math.sqrt(rq);
        return new Vector3(Math.cos(phi) * r, Math.sin(phi) * r, Math.sqrt(1 - rq));
    }
}
