package pathtracer.material;

import pathtracer.Intersection;
import pathtracer.Ray;

/**
 *
 * @author balsfull
 */
public class DiffuseColoredMaterial extends DiffuseGrayMaterial {

    private final double wavelength, deviation;

    public DiffuseColoredMaterial(double grayscale, double wavelength, double deviation) {
        super(grayscale);
        this.wavelength = wavelength;
        this.deviation = deviation;
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
