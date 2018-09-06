package pathtracer;

/**
 *
 * @author balsfull
 */
public class Ray {
    
    private Vector3 position, direction;
    private double wavelength, probability;
    
    public Ray(Vector3 position, Vector3 direction, double wavelength) {
        this.position = position;
        this.direction = direction;
        this.wavelength = wavelength;
        this.probability = 1.0;
    }

    public Ray(Vector3 position, Vector3 direction, double wavelength, double probability) {
        this.position = position;
        this.direction = direction;
        this.wavelength = wavelength;
        this.probability = probability;
    }

    public Vector3 getPosition() {
        return position;
    }

    public Vector3 getDirection() {
        return direction;
    }

    public double getWavelength() {
        return wavelength;
    }

    public double getProbability() {
        return probability;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public void setDirection(Vector3 direction) {
        this.direction = direction;
    }

    public void setWavelength(double wavelength) {
        this.wavelength = wavelength;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    @Override
    public String toString() {
        return "Ray{" + "position=" + position + ", direction=" + direction + '}';
    }
}
