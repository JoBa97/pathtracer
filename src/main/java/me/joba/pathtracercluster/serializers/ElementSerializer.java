package me.joba.pathtracercluster.serializers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import me.joba.pathtracercluster.pathtracer.Element;
import me.joba.pathtracercluster.pathtracer.material.Material;
import me.joba.pathtracercluster.pathtracer.material.Radiator;
import me.joba.pathtracercluster.pathtracer.surface.Surface;

/**
 *
 * @author balsfull
 */
public class ElementSerializer extends Serializer<Element> {

    @Override
    public void write(Kryo kryo, Output output, Element t) {
        kryo.writeClassAndObject(output, t.getSurface());
        output.writeBoolean(t.isRadiator());
        if(t.isRadiator()) {
            kryo.writeClassAndObject(output, t.getRadiator());
        }
        else {
            kryo.writeClassAndObject(output, t.getMaterial());
        }
    }

    @Override
    public Element read(Kryo kryo, Input input, Class<? extends Element> type) {
        Surface s = (Surface)kryo.readClassAndObject(input);
        boolean radiator = input.readBoolean();
        if(radiator) {
            return new Element(s, (Radiator)kryo.readClassAndObject(input));
        }
        else {
            return new Element(s, (Material)kryo.readClassAndObject(input));
        }
    }
}
