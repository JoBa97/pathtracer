/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.joba.pathtracercluster.serializers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import me.joba.pathtracercluster.pathtracer.Vector3;
import me.joba.pathtracercluster.pathtracer.surface.Triangle;

/**
 *
 * @author jonas
 */
public class TriangleSerializer extends Serializer<Triangle> {

    @Override
    public void write(Kryo kryo, Output output, Triangle t) {
        kryo.writeObject(output, t.getP0());
        kryo.writeObject(output, t.getP1());
        kryo.writeObject(output, t.getP2());
    }

    @Override
    public Triangle read(Kryo kryo, Input input, Class<? extends Triangle> type) {
        Vector3 p0 = kryo.readObject(input, Vector3.class);
        Vector3 p1 = kryo.readObject(input, Vector3.class);
        Vector3 p2 = kryo.readObject(input, Vector3.class);
        return new Triangle(p0, p1, p2);
    }
}
