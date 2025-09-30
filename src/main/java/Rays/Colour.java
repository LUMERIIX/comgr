package Rays;

import Vectors.Vector3;

public class Colour {
    // Predefined colours (realistic ones)
    public static final Vector3 RED = new Vector3(0.8, 0.05, 0.05);
    public static final Vector3 BLUE = new Vector3(0.05, 0.05, 0.8);
    public static final Vector3 GRAY = new Vector3(0.5, 0.5, 0.5);
    public static final Vector3 WHITE = new Vector3(1.0, 1.0, 1.0);
    public static final Vector3 YELLOW = new Vector3(0.8, 0.8, 0.05);
    public static final Vector3 LIGHTCYAN = new Vector3(0.7, 0.8, 0.8);
    public static final Vector3 BLACK = new Vector3(0, 0, 0);


    public static Vector3 gammaCorrection(Vector3 v1) {
        return new Vector3(
                (float) Math.pow(v1.x(), 1 / 2.2),
                (float) Math.pow(v1.y(), 1 / 2.2),
                (float) Math.pow(v1.z(), 1 / 2.2)
        );
    }

    public static int toARGB32(Vector3 v) {
        int r = (int) (v.x() * 255);
        int g = (int) (v.y() * 255);
        int b = (int) (v.z() * 255);
        r = (r & 0xFF);
        g = (g & 0xFF);
        b = (b & 0xFF);
        return (0xFF << 24) | (r << 16) | (g << 8) | b;
    }

}