package me.joba.pathtracercluster.serializers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import me.joba.pathtracercluster.pathtracer.Camera;
import me.joba.pathtracercluster.pathtracer.Element;
import me.joba.pathtracercluster.pathtracer.Scene;

/**
 *
 * @author balsfull
 */
public class SceneSerializer extends Serializer<Scene> {

    public SceneSerializer() {
        setImmutable(true);
    }

    @Override
    public void write(final Kryo kryo, final Output output, final Scene scene) {
        output.writeInt(scene.getWidth());
        output.writeInt(scene.getHeight());
        kryo.writeObject(output, scene.getCamera());
        kryo.writeObject(output, scene.getElements());
    }

    @Override 
    public Scene read(final Kryo kryo, final Input input, final Class<? extends Scene> sceneClass) {
        int width = input.readInt();
        int height = input.readInt();
        Camera camera = kryo.readObject(input, Camera.class);
        Element[] elements = kryo.readObject(input, Element[].class);
        return new Scene(width, height, camera, elements);
    }
}