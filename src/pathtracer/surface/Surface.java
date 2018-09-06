package pathtracer.surface;

import java.util.Optional;
import pathtracer.Intersection;
import pathtracer.Ray;

/**
 *
 * @author balsfull
 */
public interface Surface {
    
    Optional<Intersection> intersect(Ray ray);
}
