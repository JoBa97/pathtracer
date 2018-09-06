package me.joba.pathtracercluster.pathtracer.material;

import me.joba.pathtracercluster.pathtracer.Intersection;
import me.joba.pathtracercluster.pathtracer.Ray;

/**
 *
 * @author balsfull
 */
public class ColoredGlassMaterial extends GlassMaterial {
    
    private final double wavelength, deviation;
    
    public ColoredGlassMaterial(double wavelength, double deviation) {
        this.wavelength = wavelength;
        this.deviation = deviation;
    }

    public double getWavelength() {
        return wavelength;
    }

    public double getDeviation() {
        return deviation;
    }
    
    @Override
    public Ray getNextRay(Ray incoming, Intersection intersection) {
        Ray ray = super.getNextRay(incoming, intersection);
        double p = (wavelength - ray.getWavelength()) / deviation;
        double q = Math.exp(-0.5 * p * p);
        ray.setProbability(ray.getProbability() * q);
        return ray;
    }
}
