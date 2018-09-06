package me.joba.pathtracercluster.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.SimpleJSAP;
import com.martiansoftware.jsap.Switch;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.joba.pathtracercluster.NetworkRegistration;
import me.joba.pathtracercluster.pathtracer.Camera;
import me.joba.pathtracercluster.pathtracer.Element;
import me.joba.pathtracercluster.pathtracer.Quaternion;
import me.joba.pathtracercluster.pathtracer.Scene;
import me.joba.pathtracercluster.pathtracer.Vector3;
import me.joba.pathtracercluster.pathtracer.material.BlackBodyRadiator;
import me.joba.pathtracercluster.pathtracer.material.ColoredGlassMaterial;
import me.joba.pathtracercluster.pathtracer.material.DiffuseColoredMaterial;
import me.joba.pathtracercluster.pathtracer.material.DiffuseGrayMaterial;
import me.joba.pathtracercluster.pathtracer.material.GlassMaterial;
import me.joba.pathtracercluster.pathtracer.material.GlossyMaterial;
import me.joba.pathtracercluster.pathtracer.material.Material;
import me.joba.pathtracercluster.pathtracer.material.Radiator;
import me.joba.pathtracercluster.pathtracer.render.Plotter;
import me.joba.pathtracercluster.pathtracer.surface.Plane;
import me.joba.pathtracercluster.pathtracer.surface.Sphere;
import me.joba.pathtracercluster.pathtracer.surface.Triangle;
import me.joba.pathtracercluster.serializers.ArraySerializer;
import me.joba.pathtracercluster.serializers.InetAddressSerializer;
import me.joba.pathtracercluster.serializers.PlotterSerializer;
import me.joba.pathtracercluster.serializers.ServerStateSerializer;

/**
 *
 * @author balsfull
 */
public class ServerMain {
    
    public static ServerState state;
    
    public static void main(String[] args) throws JSAPException, IOException, InterruptedException {
        Kryo kryo = new Kryo();
        NetworkRegistration.registerKryo(kryo);
        kryo.register(ServerState.class);
        kryo.register(InetAddress.class, new InetAddressSerializer());
        kryo.register(Inet4Address.class, new InetAddressSerializer());
        kryo.register(Inet6Address.class, new InetAddressSerializer());
        kryo.register(InetAddress[].class, new ArraySerializer<>(InetAddress.class));
        kryo.register(Inet4Address[].class, new ArraySerializer<>(Inet4Address.class));
        kryo.register(Inet6Address[].class, new ArraySerializer<>(Inet6Address.class));
        kryo.register(Plotter.class, new PlotterSerializer());
        kryo.register(ServerState.class, new ServerStateSerializer());
        
        SimpleJSAP jsap = new SimpleJSAP(
                "PathTracer",
                "Realistic rendering",
                new Parameter[]{
                    new FlaggedOption("height", JSAP.INTEGER_PARSER, "512", JSAP.NOT_REQUIRED, 'h', "height", "Image height"),
                    new FlaggedOption("width", JSAP.INTEGER_PARSER, "512", JSAP.NOT_REQUIRED, 'w', "width", "Image width"),
                    new FlaggedOption("scene", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.NOT_REQUIRED, JSAP.NO_SHORTFLAG, "scene", "Scene file location"),
                    new FlaggedOption("servers", JSAP.INETADDRESS_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, JSAP.NO_SHORTFLAG, "servers", "Rendering servers").setList(true).setListSeparator(','),
                    new FlaggedOption("autosave", JSAP.INTEGER_PARSER, "-1", JSAP.NOT_REQUIRED, JSAP.NO_SHORTFLAG, "autosave", "Autosave period"),
                    new FlaggedOption("writeImage", JSAP.INTEGER_PARSER, "-1", JSAP.NOT_REQUIRED, JSAP.NO_SHORTFLAG, "writeImage", "Rendering period"),
                    new Switch("server", JSAP.NO_SHORTFLAG, "server", "Start Process as a master node"),
                    new Switch("continue", JSAP.NO_SHORTFLAG, "continue", "Continue execution")
                }
        );
        JSAPResult config = jsap.parse(args);
        if (!config.success()) {
            System.err.println("                " + jsap.getUsage());
            System.exit(1);
        }
        state = new ServerState();
        if(config.userSpecified("continue")) {
            try {
                File file = new File("pathtracer.continue");
                if(!file.exists()) {
                    System.err.println("Continue file not found!");
                    System.exit(1);
                }   
                Input input = new Input(new FileInputStream(file));
                state = kryo.readObject(input, ServerState.class);
                input.close();
                if(config.userSpecified("autosave")) {
                    state.autosave = config.getInt("autosave");
                }
                if(config.userSpecified("writeImage")) {
                    state.writeImage = config.getInt("writeImage");
                }
                if(config.userSpecified("servers")) {
                    state.servers = config.getInetAddressArray("servers");
                }
            } catch (Exception ex) {
                Logger.getLogger(ServerMain.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println("Could not resume operation!");
                System.exit(1);
            }
        }
        else {
            state = new ServerState();
            state.height = config.getInt("height");
            state.width = config.getInt("width");
            state.autosave = config.getInt("autosave");
            state.writeImage = config.getInt("writeImage");
            state.servers = config.getInetAddressArray("servers");
            if(!config.contains("scene")) {
                state.scene = new Scene(state.width, state.height);
                createSceneManu(state.scene);
//                populateScene(state.scene);
            }
            else {
                try {
                    File file = new File("pathtracer.scene");
                    if(!file.exists()) {
                        System.err.println("Scene file not found!");
                        System.exit(1);
                    }   
                    Input input = new Input(new FileInputStream(file));
                    state.scene = kryo.readObject(input, Scene.class);
                    input.close();
                } catch (Exception ex) {
                    Logger.getLogger(ServerMain.class.getName()).log(Level.SEVERE, null, ex);
                    System.err.println("Could not resume operation!");
                    System.exit(1);
                }
            }
            state.sceneId = UUID.randomUUID();
            state.imageState = new Plotter(state.width, state.height);
            state.serverId = UUID.randomUUID();
        }
        System.out.println("Starting Server with:");
        System.out.println("ServerID: " + state.serverId);
        System.out.println("Servers: " + Arrays.toString(state.servers));
        System.out.println("SceneID: " + state.sceneId);
        
        TaskScheduler scheduler = new TaskScheduler(state.sceneId, state.width, state.height, state.imageState, 20000);
        NetworkListener network = new NetworkListener(state.scene, state.serverId, state.sceneId, scheduler, state.servers);
        Lock lock = new ReentrantLock();
        Thread saveThread = new Thread(() -> {
            while(true) {
                try {
                    lock.lock();
                    try {
                        save(kryo);
                    } finally {
                        lock.unlock();
                    }
                    Thread.sleep(state.autosave);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(ServerMain.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ServerMain.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerMain.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        if(state.autosave > 0) {
            saveThread.start();
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                System.out.println("Shutting down...");
                scheduler.shutdown();
                lock.lock();
                save(kryo);
            } catch (IOException ex) {
                Logger.getLogger(ServerMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }));
        scheduler.start();
    }
    
    private static void save(Kryo kryo) throws FileNotFoundException, IOException {
        File saveFile = new File("pathtracer.continue");
        File saveFileTmp = new File("pathtracer.continue.tmp");
        Output output = new Output(new FileOutputStream(saveFileTmp));
        kryo.writeObject(output, state);
        output.close();
        if(saveFile.exists()) {
            saveFile.delete();
        }
        Files.move(saveFileTmp.toPath(), saveFile.toPath());
    }
    
    public static Camera createCamera() {
        Vector3 position = new Vector3(0, -9, 0);
        Quaternion orientation = new Quaternion(0, 10, 3, 0);
        return new Camera(
                position,
                orientation,
                Math.PI * 0.35,
                4,
                Double.MAX_VALUE,
                0.01
        );
    }
    
    public static void createSceneManu(Scene scene) {
        //The room
        Triangle bottom1 = new Triangle(new Vector3(-10, -10, -10), new Vector3(10, -10, -10), new Vector3(-10, 10, -10));
        Triangle bottom2 = new Triangle(new Vector3(10, 10, -10), new Vector3(10, -10, -10), new Vector3(-10, 10, -10));
        
        Triangle top1 = new Triangle(new Vector3(-10, -10, 10), new Vector3(10, -10, 10), new Vector3(-10, 10, 10));
        Triangle top2 = new Triangle(new Vector3(10, 10, 10), new Vector3(10, -10, 10), new Vector3(-10, 10, 10));
        
        Triangle left1 = new Triangle(new Vector3(10, -10, -10), new Vector3(10, -10, 10), new Vector3(10, 10, -10));
        Triangle left2 = new Triangle(new Vector3(10, 10, 10), new Vector3(10, -10, 10), new Vector3(10, 10, -10));
        
        Triangle right1 = new Triangle(new Vector3(-10, -10, -10), new Vector3(-10, -10, 10), new Vector3(-10, 10, -10));
        Triangle right2 = new Triangle(new Vector3(-10, 10, 10), new Vector3(-10, -10, 10), new Vector3(-10, 10, -10));
        
        Triangle back1 = new Triangle(new Vector3(10, 10, 10), new Vector3(-10, 10, 10), new Vector3(10, 10, -10));
        Triangle back2 = new Triangle(new Vector3(-10, 10, -10), new Vector3(-10, 10, 10), new Vector3(10, 10, -10));
        
        Triangle light1 = new Triangle(new Vector3(-2.5, -2.5, 9.9), new Vector3(-2.5, 2.5, 9.9), new Vector3(2.5, -2.5, 9.9));
        Triangle light2 = new Triangle(new Vector3(2.5, 2.5, 9.9), new Vector3(-2.5, 2.5, 9.9), new Vector3(2.5, -2.5, 9.9));
        
        Material diffuseWhite = new DiffuseGrayMaterial(0.8);
        Material diffuseGreen = new DiffuseColoredMaterial(0.8, 535, 35);
        Material diffuseRed = new DiffuseColoredMaterial(0.8, 685, 65);
        Material semiGlossyBlue = new GlossyMaterial(new DiffuseColoredMaterial(0.8, 475, 20), 0.2);
        Radiator light = new BlackBodyRadiator(8000, 5);
        
        scene.addElement(new Element(bottom1, diffuseWhite));
        scene.addElement(new Element(bottom2, diffuseWhite));
        scene.addElement(new Element(back1, diffuseWhite));
        scene.addElement(new Element(back2, diffuseWhite));
        scene.addElement(new Element(top1, diffuseWhite));
        scene.addElement(new Element(top2, diffuseWhite));
        scene.addElement(new Element(left1, diffuseRed));
        scene.addElement(new Element(left2, diffuseRed));
        scene.addElement(new Element(right1, diffuseGreen));
        scene.addElement(new Element(right2, diffuseGreen));
        scene.addElement(new Element(light1, light));
        scene.addElement(new Element(light2, light));
        
        //The objects
        Sphere ball1 = new Sphere(new Vector3(0, -2, -8), 2);
        Sphere ball2 = new Sphere(new Vector3(-3, 3, -8), 2);
        Sphere ball3 = new Sphere(new Vector3(3, 3, -8), 2);
        
        Material glass = new GlassMaterial();
        Material mirror = new GlossyMaterial(new DiffuseGrayMaterial(1), 0);
        
        scene.addElement(new Element(ball1, glass));
        scene.addElement(new Element(ball2, mirror));
        scene.addElement(new Element(ball3, semiGlossyBlue));
        
        Vector3 position = new Vector3(0, -30, 0);
        Quaternion orientation = Quaternion.rotation(0, 1, 0, 0);
        Camera camera = new Camera(
                position,
                orientation,
                Math.PI * 0.35,
                4,
                Double.MAX_VALUE,
                0.01
        );
        scene.setCamera(camera);
    }
    
    public static void populateScene(Scene scene) {
        Plane top = new Plane(new Vector3(0, 0, -10), new Vector3(0, 0, 1));
        Plane bottom = new Plane(new Vector3(0, 0, 10), new Vector3(0, 0, 1));
        Plane front = new Plane(new Vector3(0, -10, 0), new Vector3(0, 1, 0));
        Plane back = new Plane(new Vector3(0, 10, 0), new Vector3(0, 1, 0));
        Plane left = new Plane(new Vector3(10, 0, 0), new Vector3(1, 0, 0));
        Plane right = new Plane(new Vector3(-10, 0, 0), new Vector3(1, 0, 0));
        
        Material color1 = new DiffuseColoredMaterial(1, 400, 20);
        Material color2 = new DiffuseColoredMaterial(1, 600, 40);
        Material color3 = new DiffuseColoredMaterial(1, 500, 10);
        Material diffuseFloor = new DiffuseGrayMaterial(0.8);
        Material mirror = new GlossyMaterial(new DiffuseGrayMaterial(1), 0);
        Material glossy = new GlossyMaterial(new DiffuseGrayMaterial(1), 0.8);
                
        scene.addElement(new Element(top, mirror));
        scene.addElement(new Element(bottom, diffuseFloor));
        scene.addElement(new Element(front, color3));
        scene.addElement(new Element(back, glossy));
        scene.addElement(new Element(left, color1));
        scene.addElement(new Element(right, color2));
        
        //radiators
        Sphere sunOne = new Sphere(new Vector3(0, 0, 0), 1);
        Sphere sunTwo = new Sphere(new Vector3(7,5, -8), 2);
        Radiator radiatorOne = new BlackBodyRadiator(6800, 8);
        Radiator radiatorTwo =  new BlackBodyRadiator(3000, 5);
        scene.addElement(new Element(sunOne, radiatorOne));
        scene.addElement(new Element(sunTwo, radiatorTwo));
        
        //glass
        Sphere glassOne = new Sphere(new Vector3(3, 9, 5), 0.5);
        Sphere glassTwo = new Sphere(new Vector3(-2, 7, 4), 1.5);
        Material glassGreen = new ColoredGlassMaterial(540, 40);
        Material glassYellow = new ColoredGlassMaterial(570, 50);
        scene.addElement(new Element(glassOne, glassGreen));
        scene.addElement(new Element(glassTwo, glassYellow));
        
        //mirrors
        Sphere mirrorOne = new Sphere(new Vector3(-8, -4, -7), 0.8);
        Sphere mirrorTwo = new Sphere(new Vector3(5, 3, -5), 3);
        scene.addElement(new Element(mirrorOne, mirror));
        scene.addElement(new Element(mirrorTwo, glossy));
        
        //diffuse
        Sphere diffuseOne = new Sphere(new Vector3(2, 0,-5), 2.5);
        Material cyanDiffuse = new DiffuseColoredMaterial(1, 500, 30);
        scene.addElement(new Element(diffuseOne, cyanDiffuse));
        scene.setCamera(createCamera());
    }
}
