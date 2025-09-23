package Rays;
import java.util.ArrayList;
import java.util.List;

import Vectors.Vector2;
import Vectors.Vector3;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;

public class CornellBox {

    private static final int WIDTH = 512;
    private static final int HEIGHT = 512;

    private static final Sphere leftWall = new Sphere(
        new Vector3(-1001, 0, 0), 1000, Colour.RED); //a
    private static final Sphere rightWall = new Sphere(
        new Vector3(1001, 0, 0), 1000, Colour.BLUE); //b
    private static final Sphere backWall = new Sphere(
        new Vector3(0, 0, 1001), 1000, Colour.GRAY); //c
    private static final Sphere floor = new Sphere(
        new Vector3(0, -1001, 0), 1000, Colour.GRAY); //d
    private static final Sphere ceiling = new Sphere(
        new Vector3(0, 1001, 0), 1000, Colour.WHITE); //e
    private static final Sphere light = new Sphere(
        new Vector3(-0.6, -0.7, -0.6), 0.3, Colour.YELLOW); //f
    private static final Sphere bigSphere = new Sphere(
        new Vector3(0.3, -0.4, 0.3), 0.6, Colour.LIGHTCYAN); //g

    private static final Sphere[] spheres = {
        leftWall,
        rightWall,
        backWall,
        floor,
        ceiling,
        light,
        bigSphere
    };

    private Vector3 eye = new Vector3(0, 0, -4);
    private Vector3 lookAt = new Vector3(0, 0, 6);
    private float FOV = 36.0f;

    // Option B
    /*private Vector3 eye = new Vector3(-0.9, -0.5, 0.9);
    private Vector3 lookAt = new Vector3(0, 0, 0);
    private float FOV = 110.0f;*/

    private static Scene scene = new Scene(spheres);

    // Pixel: (0,0) is bottom left, (1,1) is top right
    public Ray CreateEyeRay(Vector3 eye, Vector3 lookAt, float FOV, Vector2 pixel) {
        assert pixel.x() >= -1 && pixel.x() <= 1;
        assert pixel.y() >= -1 && pixel.y() <= 1;
        // check that FOV is legitim
        assert FOV >= 0 && FOV <= 180;


        Vector3 forward = Vector3.normalize(lookAt.subtract(eye));
        Vector3 cameraOrientation = new Vector3(0, 1, 0);

        // right hand rule
        Vector3 screenRight = Vector3.normalize(Vector3.cross(cameraOrientation, forward));
        Vector3 screenUp    = Vector3.normalize(Vector3.cross(forward, screenRight));

        
        float tan_alpha_half = (float) Math.tan(Math.toRadians(FOV) / 2);

        Vector3 betaUp = Vector3.normalize(screenUp).multiply(tan_alpha_half * pixel.y());
        Vector3 omegaRight = Vector3.normalize(screenRight).multiply(tan_alpha_half * pixel.x());

        Vector3 rayDirection = Vector3.normalize(forward).add(betaUp).add(omegaRight);

        return new Ray(eye, rayDirection);
        
    }

    public Intersection FindClosestHitPoint(Scene s, Vector3 o, Vector3 d) {
        List<Intersection> intersections = new ArrayList<>();
        for (Sphere sphere : s.getSpheres()) {
            intersections.addAll(sphere.intersect(new Ray(o, d)));
        }
        if(intersections.isEmpty()) {
            return null;
        }
        List<Intersection> intersectionsInFront = intersections.stream()
                                                .filter(v -> v.point().subtract(o).length() >= 0).toList();
        
        return intersectionsInFront.stream().sorted((v1, v2) -> {
            float dist1 = Vector3.distanceSquared(o, v1.point());
            float dist2 = Vector3.distanceSquared(o, v2.point());
            return Float.compare(dist1, dist2);
        }).findFirst().orElseThrow(() -> new IllegalStateException("No intersections in front of the eye"));
    }

    public Image run() {
        int[] pixels = new int[WIDTH * HEIGHT];

        // Create eye rays for each pixel
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                int dstY = HEIGHT - 1 - y; // invert y for image coordinates
                // Convert pixel coordinate to [-1,1] range
                float px = (2.0f * x / (WIDTH - 1)) - 1.0f;
                float py = (2.0f * y / (HEIGHT - 1)) - 1.0f;
                Vector2 pixelNDC = new Vector2(px, py);
                Ray eyeRay = CreateEyeRay(eye, lookAt, FOV, pixelNDC);
                Intersection intersectionPoint = FindClosestHitPoint(scene, eyeRay.getEye(), eyeRay.getDirection());
                if(intersectionPoint == null) {
                    // No intersection, set pixel to black
                    pixels[dstY * WIDTH + x] = 0xFF000000; // ARGB for black
                    continue;
                }
                // For now, just set the pixel to white if there's an intersection
                pixels[dstY * WIDTH + x] = Colour.toARGB32(intersectionPoint.sphere().getColor());
            }
        }
        MemoryImageSource src = new MemoryImageSource(WIDTH, HEIGHT, pixels, 0, WIDTH);
        return Toolkit.getDefaultToolkit().createImage(src);
    }



    
}