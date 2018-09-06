package pathtracer.material;

import pathtracer.Intersection;
import pathtracer.Ray;

/**
 *
 * @author balsfull
 */
public interface Material {
    
    Ray getNextRay(Ray incoming, Intersection intersection);
}
