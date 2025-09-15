import java.io.FileOutputStream;
import java.util.Vector;

public class Interpolation {

    protected Vector3 startColor;
    protected Vector3 endColor;

    protected long steps;

    public Interpolation(Vector3 startColor, Vector3 endColor, long steps) {
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
        float t = (float) step / (float) steps;
        return Vector3.lerp(startColor, endColor, t);
    }

    private int toRGBA32(Vector3 v) {
        int result = 0;
        int r = ((int) v.x()) & 0xFF;
        int g = ((int) v.y()) & 0xFF;
        int b = ((int) v.z()) & 0xFF;
        return result | (r << 16) | (g << 8) | b;
    }

    public boolean generateInterpolationImage(String filename) {
        Vector<Vector3> pixels = new Vector<>();
        for (long i = 0; i <= steps; i++) {
            Vector3 color = interpolate(i);
            color = processPixel(color);
            pixels.add(color);
        }
        Image img = createImage(new MemoryImageSource(steps, 1, pixels.toArray(), 0, steps));
    }

    public static void main(String[] args) {
        // Initialize start and end colors
        Vector3 startColor = new Vector3(1, 0, 0); // Red
        Vector3 endColor = new Vector3(0, 1, 0);   // Green
        Interpolation interp = new Interpolation(startColor, endColor, 255);
    }


}
