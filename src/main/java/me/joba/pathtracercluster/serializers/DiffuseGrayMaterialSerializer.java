package me.joba.pathtracercluster.serializers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import me.joba.pathtracercluster.pathtracer.material.DiffuseGrayMaterial;

/**
 *
 * @author balsfull
 */
public class DiffuseGrayMaterialSerializer extends Serializer<DiffuseGrayMaterial>{

    @Override
    public void write(Kryo kryo, Output output, DiffuseGrayMaterial t) {
        output.writeDouble(t.getGrayScale());
    }

    @Override
    public DiffuseGrayMaterial read(Kryo kryo, Input input, Class<? extends DiffuseGrayMaterial> type) {
        double gray = input.readDouble();
        return new DiffuseGrayMaterial(gray);
    }
}