package pathtracer.surface;

import java.util.Optional;
import pathtracer.Intersection;
import pathtracer.Ray;
import pathtracer.Vector3;

/**
 *
 * @author balsfull
 */
public class Sphere implements Surface, Volume {

    private final Vector3 position;
    private final double radiusSquared;

    public Sphere(Vector3 offset, double radius) {
        this.position = offset;
        this.radiusSquared = radius * radius;
    }
    
    @Override
    public Optional<Intersection> intersect(Ray ray) {
        
        double a = ray.getDirection().lengthSquared();
        Vector3 coffset = ray.getPosition().subtract(position);
//        System.out.println(coffset);
//        System.out.println(a);
        double b = 2.0 * ray.getDirection().dot(coffset);
//        System.out.println(b);
        double c = coffset.lengthSquared() - radiusSquared;
//        System.out.println(c);
        double discriminant = b * b - 4.0 * a * c;
//        System.out.println(discriminant);
        if(discriminant < 0) {
            return Optional.empty();
        }
        double d = Math.sqrt(discriminant);
        double t1 = 0.5 * (-b + d) / a;
        double t2 = 0.5 * (-b - d) / a;
//        System.out.println(t1);
//        System.out.println(t2);
        double t = Math.min(t1, t2);
        if(t <= 0) {
            return Optional.empty();
        }
        Vector3 position = ray.getPosition().add(ray.getDirection().scale(t));
        Vector3 normal = (position.subtract(this.position)).normalize();
        Vector3 tangent = new Vector3(0, 1, 0).cross(normal).normalize();
        Intersection inter = new Intersection(position, normal, tangent, t);
        return Optional.of(inter);
    }

    @Override
    public boolean isInside(Vector3 position) {
        return position.subtract(this.position).lengthSquared() < radiusSquared;
    }
}

