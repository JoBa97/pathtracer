package me.joba.pathtracercluster.serializers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.UUID;
import me.joba.pathtracercluster.pathtracer.Scene;
import me.joba.pathtracercluster.pathtracer.render.Plotter;
import me.joba.pathtracercluster.server.ServerState;

/**
 *
 * @author balsfull
 */
public class ServerStateSerializer extends Serializer<ServerState>{

    @Override
    public void write(Kryo kryo, Output output, ServerState t) {
        output.writeInt(t.width);
        output.writeInt(t.height);
        output.writeInt(t.port);
        output.writeInt(t.autosave);
        output.writeInt(t.writeImage);
        kryo.writeObject(output, t.serverId);
        kryo.writeObject(output, t.sceneId);
        kryo.writeObject(output, t.scene);
        kryo.writeObject(output, t.servers);
        kryo.writeObject(output, t.imageState);
    }

    @Override
    public ServerState read(Kryo kryo, Input input, Class<? extends ServerState> type) {
        ServerState ss = new ServerState();
        ss.width = input.readInt();
        ss.height = input.readInt();
        ss.port = input.readInt();
        ss.autosave = input.readInt();
        ss.writeImage = input.readInt();
        ss.serverId = kryo.readObject(input, UUID.class);
        ss.sceneId = kryo.readObject(input, UUID.class);
        ss.scene = kryo.readObject(input, Scene.class);
        ss.servers = kryo.readObject(input, InetAddress[].class);
        ss.imageState = kryo.readObject(input, Plotter.class);
        return ss;
    }
}
