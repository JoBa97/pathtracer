package me.joba.pathtracercluster.pathtracer.render;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import me.joba.pathtracercluster.pathtracer.Camera;
import me.joba.pathtracercluster.pathtracer.Element;
import me.joba.pathtracercluster.pathtracer.Intersection;
import me.joba.pathtracercluster.pathtracer.PTRandom;
import me.joba.pathtracercluster.pathtracer.Ray;
import me.joba.pathtracercluster.pathtracer.Scene;

/**
 *
 * @author balsfull
 */
public class Tracer {
    
    public class Photon {
        public double x, y;
        public double probability;
        public double wavelength;
    }
    
    private final ArrayList<Photon> photons;
    private AtomicInteger rayCount = new AtomicInteger(0);
    
    
    public Tracer() {
        this.photons = new ArrayList<>();
    }
    
    public List<Photon> getPhotons() {
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
    
    public void render(Scene scene, int photonCount) {
        render(scene, -1, 1, -1, 1, photonCount);
    }
    
    public void render(Scene scene, double minX, double maxX, double minY, double maxY, int photonCount) {
        rayCount.set(0);
        photons.clear();
        photons.ensureCapacity(photonCount);
        for (int i = 0; i < photonCount; i++) {
            double wavelength = PTRandom.getWavelength();
            double x = PTRandom.getUnit() * (maxX - minX) + minX;
            double y = PTRandom.getUnit() * (maxY - minY) + minY;
            Photon photon = new Photon();
            photon.wavelength = wavelength;
            photon.x = x;
            photon.y = y;
            photon.probability = renderCameraRay(scene, x, y, wavelength);
            photons.add(photon);
            rayCount.incrementAndGet();
        }
    }

    public int getCurrentRayCount() {
        return rayCount.intValue();
    }
}
