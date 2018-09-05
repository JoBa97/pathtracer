package me.joba.pathtracercluster.pathtracer.material;

import me.joba.pathtracercluster.pathtracer.Intersection;
import me.joba.pathtracercluster.pathtracer.Ray;
import me.joba.pathtracercluster.pathtracer.Vector3;

/**
 *
 * @author balsfull
 */
public class GlossyMaterial implements Material {

    private final double glossiness;
    private final Material base;
    
    public GlossyMaterial(Material base, double glossiness) {
        this.base = base;
        this.glossiness = glossiness;
    }

    public double getGlossiness() {
        return glossiness;
    }

    public Material getBase() {
        return base;
    }
    
    @Override
    public Ray getNextRay(Ray incoming, Intersection intersection) {
        Ray ray = base.getNextRay(incoming, intersection);
        Vector3 reflection = incoming.getDirection().reflect(intersection.getNormal());
        Vector3 direction = ray.getDirection().scale(glossiness)
                .add(reflection.scale(1 - glossiness)).normalize();
        ray.setDirection(direction);
        ray.setProbability((ray.getProbability() * (1 - glossiness) + glossiness));
        return ray;
    }
}
