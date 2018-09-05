package me.joba.pathtracercluster.pathtracer;

/**
 *
 * @author balsfull
 */
public class Quaternion {
    
    public final double x, y, z, w;
    
    public Quaternion(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public static Quaternion rotation(double x, double y, double z, double angle) {
        return new Quaternion(
                Math.sin(angle * 0.5) * x,
                Math.sin(angle * 0.5) * y,
                Math.sin(angle * 0.5) * z,
                Math.cos(angle * 0.5)
        );
    }
    
    public Quaternion conjugate() {
        return new Quaternion(-x, -y, -z, w);
    }
    
    public Quaternion add(Quaternion q) {
        return new Quaternion(x + q.x, y + q.y, z + q.z, w + q.w);
    }
    
    public Quaternion subtract(Quaternion q) {
        return new Quaternion(x - q.x, y - q.y, z - q.z, w - q.w);
    }
    
    public Quaternion scale(double d) {
        return new Quaternion(x * d, y * d, z * d, w * d);
    }
    
    public Quaternion multiply(Quaternion q) {
        return new Quaternion(
                w * q.x + x * q.w + y * q.z - z * q.y,
                w * q.y - x * q.z + y * q.w + z * q.x,
                w * q.z + x * q.y - y * q.x + z * q.w,
                w * q.w - x * q.x - y * q.y - z * q.z
        );
    }
    
}
