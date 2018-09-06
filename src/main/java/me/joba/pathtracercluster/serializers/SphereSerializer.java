package me.joba.pathtracercluster.serializers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import me.joba.pathtracercluster.pathtracer.Vector3;
import me.joba.pathtracercluster.pathtracer.surface.Plane;
import me.joba.pathtracercluster.pathtracer.surface.Sphere;

/**
 *
 * @author balsfull
 */
public class SphereSerializer extends Serializer<Sphere> {

    @Override
    public void write(Kryo kryo, Output output, Sphere t) {
        kryo.writeObject(output, t.getPosition());
        output.writeDouble(Math.sqrt(t.getRadiusSquared()));
    }

    @Override
    public Sphere read(Kryo kryo, Input input, Class<? extends Sphere> type) {
        Vector3 pos = kryo.readObject(input, Vector3.class);
        double radius = input.readDouble();
        return new Sphere(pos, radius);
    }
}
