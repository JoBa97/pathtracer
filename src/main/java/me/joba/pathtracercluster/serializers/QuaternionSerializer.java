package me.joba.pathtracercluster.serializers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import me.joba.pathtracercluster.pathtracer.Quaternion;

/**
 *
 * @author balsfull
 */
public class QuaternionSerializer extends Serializer<Quaternion> {

    @Override
    public void write(Kryo kryo, Output output, Quaternion t) {
        output.writeDouble(t.x);
        output.writeDouble(t.y);
        output.writeDouble(t.z);
        output.writeDouble(t.w);
    }

    @Override
    public Quaternion read(Kryo kryo, Input input, Class<? extends Quaternion> type) {
        double x = input.readDouble();
        double y = input.readDouble();
        double z = input.readDouble();
        double w = input.readDouble();
        return new Quaternion(x, y, z, w);
    }
}
