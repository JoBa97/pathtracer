package me.joba.pathtracercluster.pathtracer.material;

import me.joba.pathtracercluster.pathtracer.Intersection;
import me.joba.pathtracercluster.pathtracer.Ray;

/**
 *
 * @author balsfull
 */
public interface Material {
    
    Ray getNextRay(Ray incoming, Intersection intersection);
}
