package me.joba.pathtracercluster.pathtracer.render;

import java.util.Arrays;
import me.joba.pathtracercluster.pathtracer.Vector3;

/**
 *
 * @author balsfull
 */
public class ToneMapper {
    
    private final int width, height;
    
    private final int[] rgb;
    
    public ToneMapper(int width, int height) {
        this.width = width;
        this.height = height;
        this.rgb = new int[width * height];
    }

    public int[] getRGB() {
        return rgb;
    }
    
    private double clamp(double v) {
        if(v < 0) return 0;
        if(v > 1) return 1;
        return v;
    }
    
    private double findExposure(Vector3[] cieVector) {
        double mean = Arrays.stream(cieVector)
                .mapToDouble(v -> v.y)
                .average()
                .orElse(0);
        double sqrMean = Arrays.stream(cieVector)
                .mapToDouble(v -> v.y * v.y)
                .average()
                .orElse(0);
        double variance = sqrMean - mean * mean;
        return mean + Math.sqrt(variance);
    }
    
    public void tonemap(Vector3[] cieVector) {
        double maxIntensity = findExposure(cieVector);
        double ln_4 = Math.log(4);
        for (int i = 0; i < rgb.length; i++) {
            Vector3 cie = new Vector3(
                    Math.log(cieVector[i].x / maxIntensity + 1) / ln_4,
                    Math.log(cieVector[i].y / maxIntensity + 1) / ln_4,
                    Math.log(cieVector[i].z / maxIntensity + 1) / ln_4
            );
            Vector3 rgbVec = transform(cie);
            double r = clamp(rgbVec.x);
            double g = clamp(rgbVec.y);
            double b = clamp(rgbVec.z);
            this.rgb[i] = 0;
            this.rgb[i] |= ((int)(r * 255)) << 16;
            this.rgb[i] |= ((int)(g * 255)) << 8;
            this.rgb[i] |= ((int)(b * 255));
        }
    }
    
    private Vector3 transform(Vector3 cie) {
        double r =  3.2406 * cie.x - 1.5372 * cie.y - 0.4986 * cie.z;
        double g = -0.9689 * cie.x + 1.8758 * cie.y + 0.0415 * cie.z;
        double b =  0.0557 * cie.x - 0.2040 * cie.y + 1.0570 * cie.z;
        return new Vector3(gammaCorrect(r), gammaCorrect(g), gammaCorrect(b));
    }
    
    private double gammaCorrect(double d) {
        if(d < 0.0031308) {
            return 12.92 * d;
        }
        return 1.055 * Math.pow(d, 1/2.4) - 0.055;
    }
}
