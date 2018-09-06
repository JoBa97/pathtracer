package pathtracer;

import java.util.Random;

/**
 *
 * @author balsfull
 */
public class PTRandom {
    private static ThreadLocal<Random> rand = ThreadLocal.withInitial(() -> new Random());;
    
    public static void setSeed(long seed) {
        rand = ThreadLocal.withInitial(() -> new Random(seed));
    }
    
    public static Random getRand() {
        return rand.get();
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
