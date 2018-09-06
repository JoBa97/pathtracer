package pathtracer.render;

import java.util.Map.Entry;
import java.util.Optional;
import pathtracer.Camera;
import pathtracer.Element;
import pathtracer.Intersection;
import pathtracer.PTRandom;
import pathtracer.Ray;
import pathtracer.Scene;

/**
 *
 * @author balsfull
 */
public class Tracer {
    
    public static int PHOTON_COUNT = 1024 * 512;
    
    public class Photon {
        public double x, y;
        public double probability;
        public double wavelength;
    }
    
    private double aspectRatio;
    private Photon[] photons;
    private int progress;
    
    public Tracer(int width, int height) {
        this.aspectRatio = (double)width / (double)height;
        this.photons = new Photon[PHOTON_COUNT];
    }
    
    public int getProgress() {
        return progress;
    }
    
    public Photon[] getPhotons() {
        return photons;
    }
    
    private double renderRay(Scene scene, Ray ray) {
        double continueChance = 1;
        double intensity = 1;
        while(true) {
            Optional<Entry<Element, Intersection>> e = scene.intersect(ray);
            if(!e.isPresent()) {
                return 0;
            }
            Element element = e.get().getKey();
            Intersection inter = e.get().getValue();
            if(element.isRadiator()) {
                return intensity * element.getRadiator().getIntensity(ray.getWavelength());
            }
            else {
                ray = element.getMaterial().getNextRay(ray, inter);
                intensity = intensity * ray.getProbability();
            }
            ray.setPosition(ray.getPosition().add(ray.getDirection().scale(0.0001)));
            continueChance *= 0.96;
            if(PTRandom.getUnit() * 0.85 > continueChance * (1 - Math.exp(intensity * -20))) {
                break;
            }
        }
        return 0;
    }
    
    private double renderCameraRay(Scene scene, double x, double y, double wavelength) {
        Camera camera = scene.getCamera();
        return renderRay(scene, camera.getRay(x, y, wavelength));
    }
    
    public void render(Scene scene) {
        for (int i = 0; i < photons.length; i++, progress++) {
            double wavelength = PTRandom.getWavelength();
            double x = PTRandom.getBiUnit();
            double y = PTRandom.getBiUnit();
            photons[i] = new Photon();
            photons[i].wavelength = wavelength;
            photons[i].x = x;
            photons[i].y = y;
            photons[i].probability = renderCameraRay(scene, x, y, wavelength);
        }
    }
}
