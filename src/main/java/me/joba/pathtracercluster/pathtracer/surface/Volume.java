package me.joba.pathtracercluster.pathtracer.surface;

import me.joba.pathtracercluster.pathtracer.Vector3;

/**
 *
 * @author balsfull
 */
public interface Volume {
    boolean isInside(Vector3 position);
}
