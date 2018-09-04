package me.joba.pathtracercluster.pathtracer.material;

/**
 *
 * @author balsfull
 */
public class BlackBodyRadiator implements Radiator {
    
    private static final double PLANCKS_CONSTANT = 6.62606957e-34;
    private static final double BOLTZMANNS_CONSTANT =  1.3806488e-23;
    private static final double SPEED_OF_LIGHT = 299792458.0;
    private static final double WIENS_CONSTANT = 2.897772126e-3;
    
    
    private final double temperature, normalizationFactor;
    
    public BlackBodyRadiator(double temperature, double intensity) {
        this.temperature = temperature;
        this.normalizationFactor = intensity / boltzmannDistribution((WIENS_CONSTANT / temperature) * 1.0e9, temperature);
    }
    
    @Override
    public double getIntensity(double wavelength) {
        return boltzmannDistribution(wavelength, temperature) * normalizationFactor;
    }
    
    private double boltzmannDistribution(double wavelength, double temperature) {
        double f = SPEED_OF_LIGHT / (wavelength * 1.0e-9);
        return (2 * PLANCKS_CONSTANT * f * f * f) / 
                (SPEED_OF_LIGHT * SPEED_OF_LIGHT * ((Math.exp(PLANCKS_CONSTANT * f / (BOLTZMANNS_CONSTANT * temperature))) - 1));
    }
}
