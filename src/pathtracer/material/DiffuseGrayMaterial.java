package pathtracer.material;

import pathtracer.Intersection;
import pathtracer.PTRandom;
import pathtracer.Ray;
import pathtracer.Vector3;

/**
 *
 * @author balsfull
 */
public class DiffuseGrayMaterial implements Material {

    private final double grayScale;

    public DiffuseGrayMaterial(double grayScale) {
        if(grayScale < 0 || grayScale > 1) throw new AssertionError("GrayScale outside of range [0,1]. Found: " + grayScale);
        this.grayScale = grayScale;
    }
    
    @Override
    public Ray getNextRay(Ray incoming, Intersection intersection) {
        Vector3 hemi = PTRandom.getHemisphereVector();
        Vector3 normal;
        if(incoming.getDirection().dot(intersection.getNormal()) < 0) {
            normal = intersection.getNormal();
        }
        else {
            normal = intersection.getNormal().scale(-1);
        }
        Vector3 direction = hemi.rotateTowards(normal);
        return new Ray(intersection.getPosition(), direction, incoming.getWavelength(), grayScale);
    }
    
}
