import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.util.Vector;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.util.stream.Collectors;

public class Interpolation {

    protected Vector3 startColor;
    protected Vector3 endColor;

    protected long steps;

    public Interpolation(Vector3 startColor, Vector3 endColor, long steps) {
        if (steps < 1) throw new IllegalArgumentException("steps must be >= 1");
        this.startColor = startColor;
        this.endColor = endColor;
        this.steps = steps;
    }

    private Vector3 gammaCorrection(Vector3 v1) {
        return new Vector3(
                (float) Math.pow(v1.x(), 2.2),
                (float) Math.pow(v1.y(), 2.2),
                (float) Math.pow(v1.z(), 2.2)
        );
    }

    private Vector3 convertBitRepresentation(Vector3 v1) {
        return new Vector3(v1.x() * 255, v1.y() * 255, v1.z() * 255);
    }

    private Vector3 processPixel(Vector3 v1) {
        Vector3 v = clip(v1);
        v = gammaCorrection(v);
        v = convertBitRepresentation(v);
        return v;
    }

    private Vector3 clip(Vector3 v1) {
        return new Vector3(
                Math.max(0, Math.min(1, v1.x())),
                Math.max(0, Math.min(1, v1.y())),
                Math.max(0, Math.min(1, v1.z()))
        );
    }

    private Vector3 interpolate(long step) {
        float t = (float) step / (float) steps; // step in [0..steps]
        return Vector3.lerp(startColor, endColor, t);
    }

    private int toRGBA32(Vector3 v) {
        int result = 0;
        int r = ((int) v.x()) & 0xFF;
        int g = ((int) v.y()) & 0xFF;
        int b = ((int) v.z()) & 0xFF;
        return result | (r << 16) | (g << 8) | b;
    }

    private int toARGB32(Vector3 v) {
        int r = (int) Math.round(v.x() * 255.0);
        int g = (int) Math.round(v.y() * 255.0);
        int b = (int) Math.round(v.z() * 255.0);
        r = (r & 0xFF);
        g = (g & 0xFF);
        b = (b & 0xFF);
        return (0xFF << 24) | (r << 16) | (g << 8) | b;
    }

    public Image generateInterpolationImage(String filename, int height) {
        if (height < 1) height = 1;
        // Width is steps + 1 because we include both endpoints (0..steps)
        int width = Math.toIntExact(Math.addExact(steps, 1L));
        int[] pixels = new int[width];
        for (long i = 0; i <= steps; i++) {
            //Vector3 color = interpolate(i);
            Vector3 color = processPixel(startColor);
            pixels[Math.toIntExact(i)] = toARGB32(color);
        }
        MemoryImageSource src = new MemoryImageSource(width, height, pixels, 0, width);
        return Toolkit.getDefaultToolkit().createImage(src);
    }

}
