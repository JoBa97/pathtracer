/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.joba.pathtracercluster.pathtracer.surface;

import java.util.Optional;
import me.joba.pathtracercluster.pathtracer.Intersection;
import me.joba.pathtracercluster.pathtracer.Ray;
import me.joba.pathtracercluster.pathtracer.Vector3;

/**
 *
 * @author jonas
 */
public class Triangle implements Surface {
    
    private final Vector3 p0, p1, p2, normal, reverseNormal;
    
    public Triangle(Vector3 p0, Vector3 p1, Vector3 p2) {
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
        this.normal = p1.subtract(p0).cross(p2.subtract(p0)).normalize();
        this.reverseNormal = normal.scale(-1);
    }

    public Vector3 getP0() {
        return p0;
    }

    public Vector3 getP1() {
        return p1;
    }

    public Vector3 getP2() {
        return p2;
    }
    
    @Override
    public Optional<Intersection> intersect(Ray ray) {
        double epsilon = 1e-8;
        Vector3 edge1 = p1.subtract(p0);
        Vector3 edge2 = p2.subtract(p0);
        Vector3 h = ray.getDirection().cross(edge2);
        double a = edge1.dot(h);
        if(a > -epsilon && a < epsilon) {
            return Optional.empty();
        }
        double f = 1/a;
        Vector3 s = ray.getPosition().subtract(p0);
        double u = f * s.dot(h);
        if(u < 0 || u > 1) {
            return Optional.empty();
        }
        Vector3 q = s.cross(edge1);
        double v = f * ray.getDirection().dot(q);
        if(v < 0 || u + v > 1) {
            return Optional.empty();
        }
        double t = f * edge2.dot(q);
        if(t > epsilon) {
            return Optional.of(new Intersection(ray.getPosition().add(ray.getDirection().scale(t)), a > 0 ? normal : reverseNormal, new Vector3(0, 0, 0), t));
        }
        else {
            return Optional.empty();
        }
    }    
}
