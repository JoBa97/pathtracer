package pathtracer;

/**
 *
 * @author balsfull
 */
public class Vector3 {
    
    public final double x, y, z;
    
    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vector3Mutable mutable() {
        return new Vector3Mutable(x, y, z);
    }
    
    public Vector3 add(Vector3 v) {
        return new Vector3(x + v.x, y + v.y, z + v.z);
    }
    
    public Vector3 subtract(Vector3 v) {
        return new Vector3(x - v.x, y - v.y, z - v.z);
    }
    
    public Vector3 scale(double d) {
        return new Vector3(x * d, y * d, z * d);
    }
    
    public double length() {
        return Math.sqrt(lengthSquared());
    }
    
    public double lengthSquared() {
        return x*x + y*y + z*z;
    }
    
    public Vector3 normalize() {
        return scale(1 / length());
    }
    
    public double dot(Vector3 v) {
        return x * v.x + y * v.y + z * v.z;
    }
    
    public Vector3 cross(Vector3 b) {
        return new Vector3(
            y * b.z - z * b.y,
            z * b.x - x * b.z,
            x * b.y - y * b.x
        );
    }
    
    public Vector3 rotateTowards(Vector3 v) {
        double dot = v.z;
        if(dot > 0.9999) return this;
        if(dot < -0.9999) return new Vector3(x, y, -z);
        Vector3 a1 = new Vector3(0, 0, 1).cross(v).normalize();
        Vector3 a2 = a1.cross(v).normalize();
        return a1.scale(x).add(a2.scale(y)).add(v.scale(z));
    }
    
    public Vector3 rotate(Quaternion q) {
        Quaternion p = new Quaternion(x, y, z, 0);
        Quaternion r = q.multiply(p).multiply(q.conjugate());
        return new Vector3(r.x, r.y, r.z);
    }
    
    public Vector3 reflect(Vector3 n) {
        return subtract(n.scale(n.dot(this) * 2));
    }

    @Override
    public String toString() {
        return "Vector3{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
    }
}
