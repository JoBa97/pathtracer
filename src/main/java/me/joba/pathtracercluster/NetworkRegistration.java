package me.joba.pathtracercluster;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import java.util.UUID;
import me.joba.pathtracercluster.packets.*;
import me.joba.pathtracercluster.pathtracer.Camera;
import me.joba.pathtracercluster.pathtracer.Element;
import me.joba.pathtracercluster.pathtracer.Quaternion;
import me.joba.pathtracercluster.pathtracer.Scene;
import me.joba.pathtracercluster.pathtracer.Vector3;
import me.joba.pathtracercluster.pathtracer.material.*;
import me.joba.pathtracercluster.pathtracer.surface.*;
import me.joba.pathtracercluster.serializers.ArraySerializer;
import me.joba.pathtracercluster.serializers.BlackBodyRadiatorSerializer;
import me.joba.pathtracercluster.serializers.CameraSerializer;
import me.joba.pathtracercluster.serializers.ColoredGlassMaterialSerializer;
import me.joba.pathtracercluster.serializers.DiffuseColoredMaterialSerializer;
import me.joba.pathtracercluster.serializers.DiffuseGrayMaterialSerializer;
import me.joba.pathtracercluster.serializers.ElementSerializer;
import me.joba.pathtracercluster.serializers.GlossyMaterialSerializer;
import me.joba.pathtracercluster.serializers.PlaneSerializer;
import me.joba.pathtracercluster.serializers.QuaternionSerializer;
import me.joba.pathtracercluster.serializers.SceneSerializer;
import me.joba.pathtracercluster.serializers.SphereSerializer;
import me.joba.pathtracercluster.serializers.UUIDSerializer;
import me.joba.pathtracercluster.serializers.Vector3Serializer;

/**
 *
 * @author balsfull
 */
public class NetworkRegistration {
    
    public static final int DEFAULT_PORT = 58124;
    
    public static void registerKryo(Kryo kryo) {
        kryo.register(PacketServer01Hello.class);
        kryo.register(PacketServer02SendTask.class);
        kryo.register(PacketClient03GetScene.class);
        kryo.register(PacketServer04SendScene.class);
        kryo.register(PacketClient05TaskCompleted.class);
        kryo.register(PacketClient06Info.class);
        
        kryo.register(UUID.class, new UUIDSerializer());
        kryo.register(Vector3.class, new Vector3Serializer());
        kryo.register(Vector3[].class, new ArraySerializer<>(Vector3.class));
        
        kryo.register(Scene.class, new SceneSerializer());
            kryo.register(Camera.class, new CameraSerializer());
                kryo.register(Quaternion.class, new QuaternionSerializer());
            kryo.register(Element.class, new ElementSerializer());
            kryo.register(Element[].class, new ArraySerializer<>(Element.class));
                kryo.register(Material.class);
                    kryo.register(ColoredGlassMaterial.class, new ColoredGlassMaterialSerializer());
                    kryo.register(DiffuseColoredMaterial.class, new DiffuseColoredMaterialSerializer());
                    kryo.register(DiffuseGrayMaterial.class, new DiffuseGrayMaterialSerializer());
                    kryo.register(GlassMaterial.class);
                    kryo.register(GlossyMaterial.class, new GlossyMaterialSerializer());
                    
                kryo.register(Radiator.class);
                    kryo.register(BlackBodyRadiator.class, new BlackBodyRadiatorSerializer());
                kryo.register(Surface.class);
                    kryo.register(Plane.class, new PlaneSerializer());
                    kryo.register(Sphere.class, new SphereSerializer());
    }
    
    public static void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        registerKryo(kryo);
    }
}
