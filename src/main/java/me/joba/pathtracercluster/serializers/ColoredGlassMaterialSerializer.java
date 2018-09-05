package me.joba.pathtracercluster.serializers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import me.joba.pathtracercluster.pathtracer.material.ColoredGlassMaterial;

/**
 *
 * @author balsfull
 */
public class ColoredGlassMaterialSerializer extends Serializer<ColoredGlassMaterial>{

    @Override
    public void write(Kryo kryo, Output output, ColoredGlassMaterial t) {
        output.writeDouble(t.getWavelength());
        output.writeDouble(t.getDeviation());
    }

    @Override
    public ColoredGlassMaterial read(Kryo kryo, Input input, Class<? extends ColoredGlassMaterial> type) {
        double wav = input.readDouble();
        double dev = input.readDouble();
        return new ColoredGlassMaterial(wav, dev);
    }
}