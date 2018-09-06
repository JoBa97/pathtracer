package pathtracer;

/**
 *
 * @author balsfull
 */
public class Camera {
    
    private Vector3 position;
    private double fieldOfView, focalDistance, depthOfField, chromaticAberration;
    private Quaternion orientation;

    public Camera(Vector3 position, Quaternion orientation, double fieldOfView, double focalDistance, double depthOfField, double chromaticAberration) {
        this.position = position;
        this.fieldOfView = fieldOfView;
        this.focalDistance = focalDistance;
        this.depthOfField = depthOfField;
        this.chromaticAberration = chromaticAberration;
        this.orientation = orientation;
    }
    
    private Ray getScreenRay(double x, double y, double chromaFactor, double dofAngle, double dofRadius) {
        double screenDistance = 1 / Math.tan(fieldOfView * 0.5);
        double xy = x * chromaFactor;
        double ys = y * chromaFactor;
        
        Vector3 direction = new Vector3(xy, screenDistance, -ys).normalize();
        Vector3 focusPoint = direction.scale(focalDistance / direction.y);
        Vector3 lensPoint = new Vector3(Math.cos(dofAngle) * dofRadius, 0, Math.sin(dofAngle) * dofRadius);
        return new Ray(
                position.add(lensPoint.rotate(orientation)), 
                focusPoint.subtract(lensPoint).rotate(orientation).normalize(),
                0,
                1
        );
    }
    
    public Ray getRay(double x, double y, double wavelength) {
        double dofAngle = PTRandom.getLongitude();
        double dofRadius = PTRandom.getUnit() / depthOfField;
        double d = (wavelength - 580) / 200;
        double chromaZoom = 1 + d * chromaticAberration;
        Ray ray = getScreenRay(x, y, chromaZoom, dofAngle, dofRadius);
        ray.setWavelength(wavelength);
        return ray;
    }
}