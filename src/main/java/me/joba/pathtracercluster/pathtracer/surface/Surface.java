package me.joba.pathtracercluster.pathtracer.surface;

import java.util.Optional;
import me.joba.pathtracercluster.pathtracer.Intersection;
import me.joba.pathtracercluster.pathtracer.Ray;

/**
 *
 * @author balsfull
 */
public interface Surface {
    
    Optional<Intersection> intersect(Ray ray);
}
