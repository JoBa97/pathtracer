/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pathtracer.surface;

import java.util.Optional;
import pathtracer.Intersection;
import pathtracer.Ray;
import pathtracer.Vector3;

/**
 *
 * @author jonas
 */
public class Compound implements Surface, Volume {

    private final Surface s1, s2;
    private final Volume v1, v2;

    public <A extends Surface & Volume, B extends Surface & Volume> Compound(A s1, B s2) {
        this.s1 = s1;
        this.s2 = s2;
        this.v1 = s1;
        this.v2 = s2;
    }
    
    @Override
    public Optional<Intersection> intersect(Ray ray) {
        Optional<Intersection> i1 = s1.intersect(ray);
        if(!i1.isPresent()) return i1;
        Optional<Intersection> i2 = s2.intersect(ray);
        if(!i2.isPresent()) return i2;
        i1 = i1.filter(i -> v1.isInside(i.getPosition()));
        i2 = i2.filter(i -> v2.isInside(i.getPosition()));
        if(!i1.isPresent() || !i2.isPresent()) {
            return Optional.empty();
        }
        if(i1.get().getDistance() < i2.get().getDistance()) {
            return i1;
        }
        else {
            return i2;
        }
    }

    @Override
    public boolean isInside(Vector3 position) {
        return v1.isInside(position) && v2.isInside(position);
    }
}
