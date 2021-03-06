package pathtracer.render;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import pathtracer.Vector3;
import pathtracer.Vector3Mutable;
import pathtracer.render.Tracer.Photon;

/**
 *
 * @author balsfull
 */
public class Plotter {
    
    private final int width, height;
    private final double aspectRatio;
    private final Vector3Mutable[] buffer;
    private final Lock lock = new ReentrantLock();
    
    public Plotter(int width, int height) {
        this.width = width;
        this.height = height;
        this.aspectRatio = (double)width / (double)height;
        this.buffer = new Vector3Mutable[width * height];
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = new Vector3Mutable(0, 0, 0);
        }
    }

    public Vector3[] getCIEVector() {
        Vector3[] mutable = new Vector3[buffer.length];
        lock.lock();
        try {
            for (int i = 0; i < buffer.length; i++) {
                mutable[i] = buffer[i].immutable();
            }
            return mutable;
        } finally {
            lock.unlock();
        }
    }
    
    private void plotPixel(double x, double y, Vector3Mutable cie) {
        double px = (x * 0.5 + 0.5) * (width - 1);
        double py = (y * aspectRatio * 0.5 + 0.5) * (height - 1);
        int px1 = Math.max(0, Math.min(width - 1, (int)Math.floor(px)));
        int px2 = Math.max(0, Math.min(width - 1, (int)Math.ceil(px)));
        int py1 = Math.max(0, Math.min(height - 1, (int)Math.floor(py)));
        int py2 = Math.max(0, Math.min(height - 1, (int)Math.ceil(py)));
        
        double cx = px - px1;
        double cy = py - py1;
        double c11 = (1.0 - cx) * (1.0 - cy);
        double c12 = (1.0 - cx) * cy;
        double c21 = cx * (1.0 - cy);
        double c22 = cx * cy;
        int i11 = py1 * width + px1;
        int i12 = py1 * width + px2;
        int i21 = py2 * width + px1;
        int i22 = py2 * width + px2;
        
        buffer[i11].add(cie.scale(c11));
        buffer[i12].add(cie.scale(c12));
        buffer[i21].add(cie.scale(c21));
        buffer[i22].add(cie.scale(c22));
    }
    
    public void plot(Photon[] photons) {
        lock.lock();
        try {
            for(Photon photon : photons) {
                Vector3Mutable cie = getCIEVector(photon.wavelength);
                plotPixel(photon.x, photon.y, cie.scale(photon.probability));
            }
        } finally {
            lock.unlock();
        }
    }

    private Vector3Mutable getCIEVector(double wavelength) {
        double indexf = (wavelength - 380) / 5;
        int index = (int)indexf;
        double remainder = indexf - index;
        if(index < -1 || index > 80) {
            return new Vector3Mutable(0, 0, 0);//Wavelength invisible
        }
        else if(index == -1) {
            return new Vector3Mutable(
                    CIE_X[0] * remainder, 
                    CIE_Y[0] * remainder, 
                    CIE_Z[0] * remainder);
        }
        else if(index == 80) {
            return new Vector3Mutable(
                    CIE_X[80] * remainder, 
                    CIE_Y[80] * remainder, 
                    CIE_Z[80] * remainder);
        }
        else {
            return new Vector3Mutable(
                    CIE_X[index] * (1.0 - remainder) + CIE_X[index + 1] * remainder,
                    CIE_Y[index] * (1.0 - remainder) + CIE_Y[index + 1] * remainder,
                    CIE_Z[index] * (1.0 - remainder) + CIE_Z[index + 1] * remainder
            );
        }
    }
    
    private static double[] CIE_X = new double[]{
        0.001368,
        0.002236,
        0.004243,
        0.007650,
        0.014310,
        0.023190,
        0.043510,
        0.077630,
        0.134380,
        0.214770,
        0.283900,
        0.328500,
        0.348280,
        0.348060,
        0.336200,
        0.318700,
        0.290800,
        0.251100,
        0.195360,
        0.142100,
        0.095640,
        0.057950,
        0.032010,
        0.014700,
        0.004900,
        0.002400,
        0.009300,
        0.029100,
        0.063270,
        0.109600,
        0.165500,
        0.225750,
        0.290400,
        0.359700,
        0.433450,
        0.512050,
        0.594500,
        0.678400,
        0.762100,
        0.842500,
        0.916300,
        0.978600,
        1.026300,
        1.056700,
        1.062200,
        1.045600,
        1.002600,
        0.938400,
        0.854450,
        0.751400,
        0.642400,
        0.541900,
        0.447900,
        0.360800,
        0.283500,
        0.218700,
        0.164900,
        0.121200,
        0.087400,
        0.063600,
        0.046770,
        0.032900,
        0.022700,
        0.015840,
        0.011359,
        0.008111,
        0.005790,
        0.004109,
        0.002899,
        0.002049,
        0.001440,
        0.001000,
        0.000690,
        0.000476,
        0.000332,
        0.000235,
        0.000166,
        0.000117,
        0.000083,
        0.000059,
        0.000042
    };

/// CIE Y tristimulus values, at 5nm intervals, starting at 380 nm.
    private static double[] CIE_Y = new double[]{
        0.000039,
        0.000064,
        0.000120,
        0.000217,
        0.000396,
        0.000640,
        0.001210,
        0.002180,
        0.004000,
        0.007300,
        0.011600,
        0.016840,
        0.023000,
        0.029800,
        0.038000,
        0.048000,
        0.060000,
        0.073900,
        0.090980,
        0.112600,
        0.139020,
        0.169300,
        0.208020,
        0.258600,
        0.323000,
        0.407300,
        0.503000,
        0.608200,
        0.710000,
        0.793200,
        0.862000,
        0.914850,
        0.954000,
        0.980300,
        0.994950,
        1.000000,
        0.995000,
        0.978600,
        0.952000,
        0.915400,
        0.870000,
        0.816300,
        0.757000,
        0.694900,
        0.631000,
        0.566800,
        0.503000,
        0.441200,
        0.381000,
        0.321000,
        0.265000,
        0.217000,
        0.175000,
        0.138200,
        0.107000,
        0.081600,
        0.061000,
        0.044580,
        0.032000,
        0.023200,
        0.017000,
        0.011920,
        0.008210,
        0.005723,
        0.004102,
        0.002929,
        0.002091,
        0.001484,
        0.001047,
        0.000740,
        0.000520,
        0.000361,
        0.000249,
        0.000172,
        0.000120,
        0.000085,
        0.000060,
        0.000042,
        0.000030,
        0.000021,
        0.000015
    };

/// CIE Z tristimulus values, at 5nm intervals, starting at 380 nm.
    private static double[] CIE_Z = new double[]{
        0.006450,
        0.010550,
        0.020050,
        0.036210,
        0.067850,
        0.110200,
        0.207400,
        0.371300,
        0.645600,
        1.039050,
        1.385600,
        1.622960,
        1.747060,
        1.782600,
        1.772110,
        1.744100,
        1.669200,
        1.528100,
        1.287640,
        1.041900,
        0.812950,
        0.616200,
        0.465180,
        0.353300,
        0.272000,
        0.212300,
        0.158200,
        0.111700,
        0.078250,
        0.057250,
        0.042160,
        0.029840,
        0.020300,
        0.013400,
        0.008750,
        0.005750,
        0.003900,
        0.002750,
        0.002100,
        0.001800,
        0.001650,
        0.001400,
        0.001100,
        0.001000,
        0.000800,
        0.000600,
        0.000340,
        0.000240,
        0.000190,
        0.000100,
        0.000050,
        0.000030,
        0.000020,
        0.000010,
        0.000000,
        0.000000,
        0.000000,
        0.000000,
        0.000000,
        0.000000,
        0.000000,
        0.000000,
        0.000000,
        0.000000,
        0.000000,
        0.000000,
        0.000000,
        0.000000,
        0.000000,
        0.000000,
        0.000000,
        0.000000,
        0.000000,
        0.000000,
        0.000000,
        0.000000,
        0.000000,
        0.000000,
        0.000000,
        0.000000,
        0.000000
    };
}
