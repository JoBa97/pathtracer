package me.joba.pathtracercluster.serializers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import me.joba.pathtracercluster.pathtracer.material.DiffuseColoredMaterial;

/**
 *
 * @author balsfull
 */
public class DiffuseColoredMaterialSerializer extends Serializer<DiffuseColoredMaterial>{

    @Override
    public void write(Kryo kryo, Output output, DiffuseColoredMaterial t) {
        output.writeDouble(t.getGrayScale());
        output.writeDouble(t.getWavelength());
        output.writeDouble(t.getDeviation());
        
    }

    @Override
    public DiffuseColoredMaterial read(Kryo kryo, Input input, Class<? extends DiffuseColoredMaterial> type) {
        double gray = input.readDouble();
        double wav = input.readDouble();
        double dev = input.readDouble();
        return new DiffuseColoredMaterial(gray, wav, dev);
    }
}