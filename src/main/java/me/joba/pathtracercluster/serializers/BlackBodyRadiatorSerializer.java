package me.joba.pathtracercluster.serializers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import me.joba.pathtracercluster.pathtracer.material.BlackBodyRadiator;

/**
 *
 * @author balsfull
 */
public class BlackBodyRadiatorSerializer extends Serializer<BlackBodyRadiator>{

    @Override
    public void write(Kryo kryo, Output output, BlackBodyRadiator t) {
        output.writeDouble(t.getTemperature());
        output.writeDouble(t.getNormalizationFactor());
    }

    @Override
    public BlackBodyRadiator read(Kryo kryo, Input input, Class<? extends BlackBodyRadiator> type) {
        double temp = input.readDouble();
        double norm = input.readDouble();
        return new BlackBodyRadiator(temp, norm);
    }
}
