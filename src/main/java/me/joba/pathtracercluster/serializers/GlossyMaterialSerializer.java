package me.joba.pathtracercluster.serializers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import me.joba.pathtracercluster.pathtracer.material.GlossyMaterial;
import me.joba.pathtracercluster.pathtracer.material.Material;

/**
 *
 * @author balsfull
 */
public class GlossyMaterialSerializer extends Serializer<GlossyMaterial> {

    @Override
    public void write(Kryo kryo, Output output, GlossyMaterial t) {
        output.writeDouble(t.getGlossiness());
        kryo.writeClassAndObject(output, t.getBase());
    }

    @Override
    public GlossyMaterial read(Kryo kryo, Input input, Class<? extends GlossyMaterial> type) {
        double gloss = input.readDouble();
        Material base = (Material)kryo.readClassAndObject(input);
        return new GlossyMaterial(base, gloss);
    }
}
