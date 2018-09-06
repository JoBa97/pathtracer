/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.joba.pathtracercluster.pathtracer.surface;

import me.joba.pathtracercluster.pathtracer.Intersection;
import me.joba.pathtracercluster.pathtracer.Ray;
import me.joba.pathtracercluster.pathtracer.Vector3;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author jonas
 */
public class TriangleTest {

    /**
     * Test of intersect method, of class Triangle.
     */
    @Test
    public void testIntersect() {
        Triangle t;
        Ray ray;
        t = new Triangle(new Vector3(0, 0, 0), new Vector3(0, 1, 0), new Vector3(0, 0, 1));
        ray = new Ray(new Vector3(1, 0.2, 0.2), new Vector3(-1, 0, 0), 0);
        assertTrue(t.intersect(ray).isPresent());
        ray = new Ray(new Vector3(-1, 0.2, 0.2), new Vector3(1, 0, 0), 0);
        assertTrue(t.intersect(ray).isPresent());
        
        t = new Triangle(new Vector3(0, 0, 0), new Vector3(1, 0, 0), new Vector3(0, 0, 1));
        ray = new Ray(new Vector3(0.2, 1, 0.2), new Vector3(0, -1, 0), 0);
        assertTrue(t.intersect(ray).isPresent());
        ray = new Ray(new Vector3(0.2, -1, 0.2), new Vector3(0, 1, 0), 0);
        assertTrue(t.intersect(ray).isPresent());
        
        t = new Triangle(new Vector3(0, 0, 0), new Vector3(1, 0, 0), new Vector3(0, 1, 0));
        ray = new Ray(new Vector3(0.2, 0.2, 1), new Vector3(0, 0, -1), 0);
        assertTrue(t.intersect(ray).isPresent());
        ray = new Ray(new Vector3(0.2, 0.2, -1), new Vector3(0, 0, 1), 0);
        assertTrue(t.intersect(ray).isPresent());
    }
    
    @Test
    public void testDontIntersectBehind() {
        Triangle t;
        Ray ray;
        t = new Triangle(new Vector3(0, 0, 0), new Vector3(0, 1, 0), new Vector3(0, 0, 1));
        ray = new Ray(new Vector3(-1, 0.2, 0.2), new Vector3(-1, 0, 0), 0);
        assertFalse(t.intersect(ray).isPresent());
        ray = new Ray(new Vector3(1, 0.2, 0.2), new Vector3(1, 0, 0), 0);
        assertFalse(t.intersect(ray).isPresent());
        
        t = new Triangle(new Vector3(0, 0, 0), new Vector3(1, 0, 0), new Vector3(0, 0, 1));
        ray = new Ray(new Vector3(0.2, -1, 0.2), new Vector3(0, -1, 0), 0);
        assertFalse(t.intersect(ray).isPresent());
        ray = new Ray(new Vector3(0.2, 1, 0.2), new Vector3(0, 1, 0), 0);
        assertFalse(t.intersect(ray).isPresent());
        
        t = new Triangle(new Vector3(0, 0, 0), new Vector3(1, 0, 0), new Vector3(0, 1, 0));
        ray = new Ray(new Vector3(0.2, 0.2, -1), new Vector3(0, 0, -1), 0);
        assertFalse(t.intersect(ray).isPresent());
        ray = new Ray(new Vector3(0.2, 0.2, 1), new Vector3(0, 0, 1), 0);
        assertFalse(t.intersect(ray).isPresent());
    }
    
    @Test
    public void testDontIntersectParallel() {
        Triangle t;
        Ray ray;
        t = new Triangle(new Vector3(0, 0, 0), new Vector3(0, 1, 0), new Vector3(0, 0, 1));
        ray = new Ray(new Vector3(-1, 0, 0), new Vector3(0, 1, 1), 0);
        assertFalse(t.intersect(ray).isPresent());
        ray = new Ray(new Vector3(1, 0.2, 0.2), new Vector3(1, -1, -1), 0);
        assertFalse(t.intersect(ray).isPresent());
    }
    
    @Test
    public void testDontIntersectMiss() {
        Triangle t;
        Ray ray;
        t = new Triangle(new Vector3(0, 0, 0), new Vector3(0, 1, 0), new Vector3(0, 0, 1));
        ray = new Ray(new Vector3(1, 2, 0.2), new Vector3(-1, 0, 0), 0);
        assertFalse(t.intersect(ray).isPresent());
        ray = new Ray(new Vector3(-1, 0, -3), new Vector3(1, 0, 0), 0);
        assertFalse(t.intersect(ray).isPresent());
        
        t = new Triangle(new Vector3(0, 0, 0), new Vector3(1, 0, 0), new Vector3(0, 0, 1));
        ray = new Ray(new Vector3(0.2, -2, -2), new Vector3(0, -1, 0), 0);
        assertFalse(t.intersect(ray).isPresent());
        ray = new Ray(new Vector3(0.2, 1, 3), new Vector3(0, 1, 0), 0);
        assertFalse(t.intersect(ray).isPresent());
        
        t = new Triangle(new Vector3(0, 0, 0), new Vector3(1, 0, 0), new Vector3(0, 1, 0));
        ray = new Ray(new Vector3(0.2, 2, 2), new Vector3(0, 0, -1), 0);
        assertFalse(t.intersect(ray).isPresent());
        ray = new Ray(new Vector3(0.2, 3, -1), new Vector3(0, 0, 1), 0);
        assertFalse(t.intersect(ray).isPresent());
    }
    
    @Test
    public void testNormalVector() {
        Triangle t;
        Vector3 n;
        Ray ray;
        double epsilon = 1e-8;
        t = new Triangle(new Vector3(0, 0, 0), new Vector3(0, 1, 0), new Vector3(0, 0, 1));
        ray = new Ray(new Vector3(1, 0.2, 0.2), new Vector3(-1, 0, 0), 0);
        n = t.intersect(ray).map(Intersection::getNormal).get();
        assertTrue(n.dot(new Vector3(1, 0, 0)) > 0 && n.cross(new Vector3(1, 0, 0)).length() < epsilon);
        ray = new Ray(new Vector3(-1, 0.2, 0.2), new Vector3(1, 0, 0), 0);
        n = t.intersect(ray).map(Intersection::getNormal).get();
        assertTrue(n.dot(new Vector3(-1, 0, 0)) > 0 && n.cross(new Vector3(-1, 0, 0)).length() < epsilon);
        
        t = new Triangle(new Vector3(0, 0, 0), new Vector3(1, 0, 0), new Vector3(0, 0, 1));
        ray = new Ray(new Vector3(0.2, 1, 0.2), new Vector3(0, -1, 0), 0);
        n = t.intersect(ray).map(Intersection::getNormal).get();
        assertTrue(n.dot(new Vector3(0, 1, 0)) > 0 && n.cross(new Vector3(0, 1, 0)).length() < epsilon);
        ray = new Ray(new Vector3(0.2, -1, 0.2), new Vector3(0, 1, 0), 0);
        n = t.intersect(ray).map(Intersection::getNormal).get();
        assertTrue(n.dot(new Vector3(0, -1, 0)) > 0 && n.cross(new Vector3(0, -1, 0)).length() < epsilon);
        
        t = new Triangle(new Vector3(0, 0, 0), new Vector3(1, 0, 0), new Vector3(0, 1, 0));
        ray = new Ray(new Vector3(0.2, 0.2, 1), new Vector3(0, 0, -1), 0);
        n = t.intersect(ray).map(Intersection::getNormal).get();
        assertTrue(n.dot(new Vector3(0, 0, 1)) > 0 && n.cross(new Vector3(0, 0, 1)).length() < epsilon);
        ray = new Ray(new Vector3(0.2, 0.2, -1), new Vector3(0, 0, 1), 0);
        n = t.intersect(ray).map(Intersection::getNormal).get();
        assertTrue(n.dot(new Vector3(0, 0, -1)) > 0 && n.cross(new Vector3(0, 0, -1)).length() < epsilon);
    }
}
