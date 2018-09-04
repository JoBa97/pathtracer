package me.joba.pathtracercluster;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import me.joba.pathtracercluster.packets.*;

/**
 *
 * @author balsfull
 */
public class NetworkRegistration {
    
    public static void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        kryo.register(PacketServer01Hello.class);
        kryo.register(PacketServer02SendTask.class);
        kryo.register(PacketClient03GetScene.class);
        kryo.register(PacketServer04SendScene.class);
        kryo.register(PacketClient05TaskCompleted.class);
        kryo.register(PacketClient06Info.class);
    }
}
