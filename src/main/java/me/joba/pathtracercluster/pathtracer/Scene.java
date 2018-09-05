package me.joba.pathtracercluster.pathtracer;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;

/**
 *
 * @author balsfull
 */
public class Scene {
    
    private final int width, height;
    private Element[] elements;
    private Camera camera;
    
    public Scene(int width, int height) {
        this.width = width;
        this.height = height;
        this.elements = new Element[0];
    }
    
    public Scene(int width, int height, Camera camera, Element[] elements) {
        this(width, height);
        this.camera = camera;
        this.elements = elements;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Element[] getElements() {
        return elements;
    }
    
    public Optional<Entry<Element, Intersection>> intersect(Ray ray) {
        Entry<Element, Intersection> result = null;
        double distance = Double.POSITIVE_INFINITY;
        for(Element e : elements) {
            Optional<Intersection> inter = e.getSurface().intersect(ray);
            if(inter.isPresent()) {
                double d = inter.get().getDistance();
                if(d < distance) {
                    result = new SimpleEntry(e, inter.get());
                    distance = d;
                }
            }
        }
        return Optional.ofNullable(result);
    }
    
    public void setCamera(Camera camera) {
        this.camera = camera;
    }
    
    public Camera getCamera() {
        return camera;
    }
    
    public void addElement(Element element) {
        ArrayList<Element> list = new ArrayList<>(Arrays.asList(elements));
        list.add(element);
        elements = list.toArray(new Element[0]);
    }
}
