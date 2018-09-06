package me.joba.pathtracercluster.serializers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import me.joba.pathtracercluster.pathtracer.Vector3;

/**
 *
 * @author balsfull
 */
public class Vector3Serializer extends Serializer<Vector3> {

    public Vector3Serializer() {
        setImmutable(true);
    }

    @Override
    public void write(final Kryo kryo, final Output output, final Vector3 v) {
        output.writeDouble(v.x);
        output.writeDouble(v.y);
        output.writeDouble(v.z);
    }

    @Override 
    public Vector3 read(final Kryo kryo, final Input input, final Class<? extends Vector3> vClass) {
        return new Vector3(input.readDouble(), input.readDouble(), input.readDouble());
    }
}
