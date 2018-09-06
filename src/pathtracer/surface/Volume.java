package pathtracer.surface;

import pathtracer.Vector3;

/**
 *
 * @author balsfull
 */
public interface Volume {
    boolean isInside(Vector3 position);
}
