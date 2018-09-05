package me.joba.pathtracercluster.pathtracer;

import me.joba.pathtracercluster.pathtracer.material.Material;
import me.joba.pathtracercluster.pathtracer.material.Radiator;
import me.joba.pathtracercluster.pathtracer.surface.Surface;

/**
 *
 * @author balsfull
 */
public class Element {
    
    private final Surface surface;
    private final Material material;
    private final Radiator radiator;

    public Element(Surface surface, Material material) {
        this.surface = surface;
        this.material = material;
        this.radiator = null;
    }

    public Element(Surface surface, Radiator radiator) {
        this.surface = surface;
        this.radiator = radiator;
        this.material = null;
    }

    public Surface getSurface() {
        return surface;
    }

    public Material getMaterial() {
        return material;
    }

    public Radiator getRadiator() {
        return radiator;
    }
    
    public boolean isRadiator() {
        return radiator != null;
    }
}
