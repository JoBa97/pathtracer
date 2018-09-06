package me.joba.pathtracercluster.serializers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import me.joba.pathtracercluster.pathtracer.Camera;
import me.joba.pathtracercluster.pathtracer.Quaternion;
import me.joba.pathtracercluster.pathtracer.Vector3;

/**
 *
 * @author balsfull
 */
public class CameraSerializer extends Serializer<Camera>{
    
    @Override
    public void write(Kryo kryo, Output output, Camera t) {
        kryo.writeObject(output, t.getPosition());
        kryo.writeObject(output, t.getOrientation());
        output.writeDouble(t.getChromaticAberration());
        output.writeDouble(t.getDepthOfField());
        output.writeDouble(t.getFieldOfView());
        output.writeDouble(t.getFocalDistance());
    }

    @Override
    public Camera read(Kryo kryo, Input input, Class<? extends Camera> type) {
        Vector3 pos = kryo.readObject(input, Vector3.class);
        Quaternion orient = kryo.readObject(input, Quaternion.class);
        double chrom = input.readDouble();
        double dof = input.readDouble();
        double fov = input.readDouble();
        double fdist = input.readDouble();
        return new Camera(pos, orient, fov, fdist, dof, chrom);
    }
}
