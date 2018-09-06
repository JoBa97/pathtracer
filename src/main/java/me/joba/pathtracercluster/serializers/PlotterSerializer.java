package me.joba.pathtracercluster.serializers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import me.joba.pathtracercluster.pathtracer.Vector3;
import me.joba.pathtracercluster.pathtracer.render.Plotter;

/**
 *
 * @author balsfull
 */
public class PlotterSerializer extends Serializer<Plotter> {

    @Override
    public void write(Kryo kryo, Output output, Plotter t) {
        output.writeInt(t.getWidth());
        output.writeInt(t.getHeight());
        Vector3[] cieVector = t.getCIEVector();
        kryo.writeObject(output, cieVector);
    }

    @Override
    public Plotter read(Kryo kryo, Input input, Class<? extends Plotter> type) {
        int width = input.readInt();
        int height = input.readInt();
        Vector3[] cieVector = kryo.readObject(input, Vector3[].class);
        Plotter plotter = new Plotter(width, height);
        plotter.addCIEVector(cieVector);
        return plotter;
    }
}
