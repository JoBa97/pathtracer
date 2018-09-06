package pathtracer;

import pathtracer.master.PathTracerMaster;
import pathtracer.slave.PathTracerSlave;
import pathtracer.standalone.PathTracerStandalone;
import pathtracer.material.BlackBodyRadiator;
import pathtracer.material.ColoredGlassMaterial;
import pathtracer.material.DiffuseColoredMaterial;
import pathtracer.material.DiffuseGrayMaterial;
import pathtracer.material.GlossyMaterial;
import pathtracer.material.Material;
import pathtracer.material.Radiator;
import pathtracer.render.Tracer;
import pathtracer.surface.Plane;
import pathtracer.surface.Sphere;

/**
 *
 * @author balsfull
 */
public class PathTracer {
    
    public static int WIDTH = 512, HEIGHT = 512, THREADS = Runtime.getRuntime().availableProcessors();
    
    public static void main(String[] args) throws Exception {
        int mode = 0;
        String[] sshList = new String[0];
        for (int i = 0; i < args.length; i++) {
            String key = args[i];
            switch(key) {
                case "--threads": {
                    THREADS = Integer.parseInt(args[i + 1]); 
                    i++;
                    break;
                }
                case "--width":
                case "-w": {
                    WIDTH = Integer.parseInt(args[i + 1]); 
                    i++;
                    break;
                }
                case "--height":
                case "-h": {
                    HEIGHT = Integer.parseInt(args[i + 1]);
                    i++;
                    break;
                }
                case "--batchsize": {
                    Tracer.PHOTON_COUNT = Integer.parseInt(args[i + 1]); 
                    i++;
                    break;
                }
                case "--standalone": {
                    mode = 0; break;
                }
                case "--slave": {
                    mode = 1; break;
                }
                case "--master": {
                    mode = 2; break;
                }
                case "--sshList": {
                    sshList = args[i + 1].split(";");
                    i++;
                    break;
                }
            }
        }
        Scene scene = new Scene();
        populateScene(scene);
        scene.setCamera(createCamera());
        scene.setTime(0);
        switch (mode) {
            case 0:
                PathTracerStandalone.render(scene);
                break;
            case 1:
                PathTracerSlave.start(scene);
                break;
            case 2:
                PathTracerMaster.start(sshList);
                break;
            default:
                break;
        }
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
