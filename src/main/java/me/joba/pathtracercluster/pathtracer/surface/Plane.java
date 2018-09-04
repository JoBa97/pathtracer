package me.joba.pathtracercluster.pathtracer.surface;

import java.util.Optional;
import me.joba.pathtracercluster.pathtracer.Intersection;
import me.joba.pathtracercluster.pathtracer.Ray;
import me.joba.pathtracercluster.pathtracer.Vector3;

/**
 *
 * @author balsfull
 */
public class Plane implements Surface{

    private final Vector3 normal, offset;

    public Plane(Vector3 offset, Vector3 normal) {
        this.normal = normal;
        this.offset = offset;
    }
    
    @Override
    public Optional<Intersection> intersect(Ray ray) {
        Vector3 origin = ray.getPosition().subtract(offset);
        double d = normal.dot(ray.getDirection());
        if(d == 0) {
            return Optional.empty();
        }
        double t = -normal.dot(origin) / d;
        if(t <= 0) {
            return Optional.empty();
        }
        Vector3 pos = ray.getPosition().add(ray.getDirection().scale(t));
        Vector3 normal = this.normal;
        if(d >= 0) {
            normal = normal.scale(-1);
        }
        return Optional.of(new Intersection(pos, normal, new Vector3(0, 0, 0), t));
    }
}
