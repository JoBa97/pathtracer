package me.joba.pathtracercluster.serializers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import me.joba.pathtracercluster.pathtracer.Vector3;
import me.joba.pathtracercluster.pathtracer.surface.Plane;

/**
 *
 * @author balsfull
 */
public class PlaneSerializer extends Serializer<Plane> {

    @Override
    public void write(Kryo kryo, Output output, Plane t) {
        kryo.writeObject(output, t.getNormal());
        kryo.writeObject(output, t.getOffset());
    }

    @Override
    public Plane read(Kryo kryo, Input input, Class<? extends Plane> type) {
        Vector3 norm = kryo.readObject(input, Vector3.class);
        Vector3 off = kryo.readObject(input, Vector3.class);
        return new Plane(off, norm);
    }
    
}
