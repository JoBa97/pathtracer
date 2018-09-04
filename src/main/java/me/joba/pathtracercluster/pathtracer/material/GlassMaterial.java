package me.joba.pathtracercluster.pathtracer.material;

import me.joba.pathtracercluster.pathtracer.Intersection;
import me.joba.pathtracercluster.pathtracer.PTRandom;
import me.joba.pathtracercluster.pathtracer.Ray;
import me.joba.pathtracercluster.pathtracer.Vector3;

/**
 *
 * @author balsfull
 */
public class GlassMaterial implements Material {
    //Magic
    private double getRefractionIndex(double wavelength) {
        double w2 = (wavelength * wavelength * 1.0e-6);
        return Math.sqrt(1.0
            + 1.737596950 * w2 / (w2 - 0.0131887070)
            + 0.313747346 * w2 / (w2 - 0.0623068142)
            + 1.898781010 * w2 / (w2 - 155.23629000)
        );
    }
    
    private double clamp(double v) {
        if(v < -1) return -1;
        if(v > 1) return 1;
        return v;
    }
    
    private double getFresnel(Vector3 in, Vector3 normal, double ior) { 
        double cosi = clamp(in.dot(normal)); 
        double etai = 1, etat = ior; 
        if (cosi > 0) { 
            etai = ior;
            etat = 1;
        } 
        // Compute sini using Snell's law
        double sint = etai / etat * Math.sqrt(Math.max(0, 1 - cosi * cosi)); 
        // Total internal reflection
        if (sint >= 1) { 
            return 1;
        } 
        else { 
            double cost = Math.sqrt(Math.max(0, 1 - sint * sint)); 
            cosi = Math.abs(cosi); 
            double Rs = ((etat * cosi) - (etai * cost)) / ((etat * cosi) + (etai * cost)); 
            double Rp = ((etai * cosi) - (etat * cost)) / ((etai * cosi) + (etat * cost)); 
            return (Rs * Rs + Rp * Rp) / 2; 
        }
    }
    
    @Override
    public Ray getNextRay(Ray incoming, Intersection intersection) {
        double cosi = -incoming.getDirection().dot(intersection.getNormal());
        double ior = getRefractionIndex(incoming.getWavelength());
        double fresnel = getFresnel(incoming.getDirection(), intersection.getNormal(), ior);
        double path = PTRandom.getUnit();
        Vector3 direction;
        if(path < fresnel) {
            Vector3 normal = intersection.getNormal();
            if(cosi > 0) {
                ior = 1.0 / ior;
            }
            else {
                normal = normal.scale(-1);
                cosi = -cosi;
            }
            double sinTsqr = ior * ior * (1 - cosi * cosi);
            if(sinTsqr > 1) {
                direction = incoming.getDirection().reflect(normal);
            }
            else {
                direction = incoming.getDirection().scale(ior).add(normal.scale(ior * cosi - Math.sqrt(1.0 - sinTsqr)));
            }
        }
        else {
            direction = incoming.getDirection().reflect(intersection.getNormal());
        }
        return new Ray(intersection.getPosition(), direction, incoming.getWavelength());
    }
}
