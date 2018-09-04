package me.joba.pathtracercluster.pathtracer;

/**
 *
 * @author balsfull
 */
public class Intersection {
    
    private final Vector3 position, tangent, normal;
    private final double distance;

    public Intersection(Vector3 position, Vector3 normal, Vector3 tangent, double distance) {
        this.position = position;
        this.tangent = tangent;
        this.normal = normal;
        this.distance = distance;
    }

    public Vector3 getPosition() {
        return position;
    }

    public Vector3 getTangent() {
        return tangent;
    }

    public Vector3 getNormal() {
        return normal;
    }

    public double getDistance() {
        return distance;
    }
}
