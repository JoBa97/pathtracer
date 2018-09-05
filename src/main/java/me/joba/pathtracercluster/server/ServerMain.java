package me.joba.pathtracercluster.server;

import com.martiansoftware.jsap.JSAPException;
import java.util.UUID;
import me.joba.pathtracercluster.pathtracer.Camera;
import me.joba.pathtracercluster.pathtracer.Element;
import me.joba.pathtracercluster.pathtracer.Quaternion;
import me.joba.pathtracercluster.pathtracer.Scene;
import me.joba.pathtracercluster.pathtracer.Vector3;
import me.joba.pathtracercluster.pathtracer.material.BlackBodyRadiator;
import me.joba.pathtracercluster.pathtracer.material.ColoredGlassMaterial;
import me.joba.pathtracercluster.pathtracer.material.DiffuseColoredMaterial;
import me.joba.pathtracercluster.pathtracer.material.DiffuseGrayMaterial;
import me.joba.pathtracercluster.pathtracer.material.GlossyMaterial;
import me.joba.pathtracercluster.pathtracer.material.Material;
import me.joba.pathtracercluster.pathtracer.material.Radiator;
import me.joba.pathtracercluster.pathtracer.surface.Plane;
import me.joba.pathtracercluster.pathtracer.surface.Sphere;

/**
 *
 * @author balsfull
 */
public class ServerMain {
    
    public static UUID serverId;
    
    public static void main(String[] args) throws JSAPException {
//        SimpleJSAP jsap = new SimpleJSAP(
//                "PathTracer",
//                "Realistic rendering",
//                new Parameter[]{
//                    new FlaggedOption("port", JSAP.INTEGER_PARSER, "1-65535", JSAP.NOT_REQUIRED, JSAP.NO_SHORTFLAG, "port", "TCP Port"),
//                    new FlaggedOption("height", JSAP.INTEGER_PARSER, "512", JSAP.REQUIRED, 'h', "height", "Image height"),
//                    new FlaggedOption("width", JSAP.INTEGER_PARSER, "512", JSAP.REQUIRED, 'w', "width", "Image width"),
//                    new FlaggedOption("scene", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.NOT_REQUIRED, JSAP.NO_SHORTFLAG, "scene", "Scene file location"),
//                    new FlaggedOption("servers", JSAP.INETADDRESS_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, JSAP.NO_SHORTFLAG, "servers", "Rendering servers"),
//                    new FlaggedOption("continue", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.NOT_REQUIRED, JSAP.NO_SHORTFLAG, "continue", "Continue execution")
//                }
//        );
//        JSAPResult config = jsap.parse(args);
//        if (!config.success()) {
//            System.err.println("                " + jsap.getUsage());
//            System.exit(1);
//        }
//        serverId = UUID.randomUUID();
        Scene scene = new Scene(512, 512);
        populateScene(scene);
        scene.setCamera(createCamera());
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
    }
}
