package pathtracer;

/**
 *
 * @author balsfull
 */
public class Vector3Mutable {
    
    public double x, y, z;
    
    public Vector3Mutable(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vector3 immutable() {
        return new Vector3(x, y, z);
    }
    
    public Vector3Mutable add(Vector3Mutable v) {
        x += v.x;
        y += v.y;
        z += v.z;
        return this;
    }
    
    public Vector3Mutable subtract(Vector3Mutable v) {
        x -= v.x;
        y -= v.y;
        z -= v.z;
        return this;
    }
    
    public Vector3Mutable scale(double d) {
        x *= d;
        y *= d;
        z *= d;
        return this;
    }
    
    public double length() {
        return Math.sqrt(lengthSquared());
    }
    
    public double lengthSquared() {
        return x*x + y*y + z*z;
    }
    
    public Vector3Mutable normalize() {
        return scale(1 / length());
    }
    
    public double dot(Vector3Mutable v) {
        return x * v.x + y * v.y + z * v.z;
    }
    
    public Vector3Mutable cross(Vector3Mutable b) {
        x = y * b.z - z * b.y;
        y = z * b.x - x * b.z;
        z = x * b.y - y * b.x;
        return this;
    }
    
    public Vector3Mutable rotateTowards(Vector3Mutable v) {
        double dot = v.z;
        if(dot > 0.9999) return this;
        if(dot < -0.9999) {
            z = -z;
            return this;
        }
        Vector3Mutable a1 = new Vector3Mutable(0, 0, 1).cross(v).normalize();
        Vector3Mutable a2 = a1.cross(v).normalize();
        a1 = a1.scale(x).add(a2.scale(y)).add(v.scale(z));
        this.x = a1.x;
        this.y = a1.y;
        this.z = a1.z;
        return this;
    }
    
    public Vector3Mutable rotate(Quaternion q) {
        Quaternion p = new Quaternion(x, y, z, 0);
        Quaternion r = q.multiply(p).multiply(q.conjugate());
        x = r.x;
        y = r.y;
        z = r.z;
        return this;
    }
    
    public Vector3Mutable reflect(Vector3Mutable n) {
        return subtract(n.scale(n.dot(this) * 2));
    }

    @Override
    public String toString() {
        return "Vector3Mutable{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
    }
}
