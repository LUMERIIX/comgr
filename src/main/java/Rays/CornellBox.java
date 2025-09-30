package Rays;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import Vectors.Vector2;
import Vectors.Vector3;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;

public class CornellBox {

    private static final int WIDTH = 512;
    private static final int HEIGHT = 512;

    private static final int RAYS_PER_PIXEL = 16;

    private static final float MONTE_CARLO_PROPABILITY = 0.2f;
    private static final float BRDF_CONSTANT = 1.0f / (float) (1 - MONTE_CARLO_PROPABILITY);

    /*private static final Sphere leftWall = new Sphere(
        new Vector3(-1001, 0, 0), 1000, Colour.RED, Colour.BLACK); //a
    private static final Sphere rightWall = new Sphere(
        new Vector3(1001, 0, 0), 1000, Colour.BLUE, Colour.BLACK); //b
    private static final Sphere backWall = new Sphere(
        new Vector3(0, 0, 1001), 1000, Colour.GRAY, Colour.BLACK); //c
    private static final Sphere floor = new Sphere(
        new Vector3(0, -1001, 0), 1000, Colour.GRAY, Colour.BLACK); //d
    private static final Sphere ceiling = new Sphere(
        new Vector3(0, 1001, 0), 1000, Colour.WHITE, Colour.WHITE.multiply((float)2.0)); //e
    private static final Sphere light = new Sphere(
        new Vector3(-0.6, -0.7, -0.6), 0.3, Colour.YELLOW, Colour.BLACK); //f
    private static final Sphere bigSphere = new Sphere(
        new Vector3(0.3, -0.4, 0.3), 0.6, Colour.LIGHTCYAN, Colour.BLACK); //g*/

    private static final Sphere leftWall = new Sphere(
        new Vector3(-1001, 0, 0), 1000, new DiffuseMaterial(Colour.RED, Colour.BLACK)); //a
    private static final Sphere rightWall = new Sphere(
        new Vector3(1001, 0, 0), 1000, new DiffuseMaterial(Colour.BLUE, Colour.BLACK)); //b
    private static final Sphere backWall = new Sphere(
        new Vector3(0, 0, 1001), 1000, new DiffuseMaterial(Colour.GRAY, Colour.BLACK)); //c
    private static final Sphere floor = new Sphere(
        new Vector3(0, -1001, 0), 1000, new DiffuseMaterial(Colour.GRAY, Colour.BLACK)); //d
    private static final Sphere ceiling = new Sphere(
        new Vector3(0, 1001, 0), 1000, new DiffuseMaterial(Colour.WHITE, Colour.WHITE.multiply((float)2.0))); //e
    private static final Sphere light = new Sphere(
        new Vector3(-0.6, -0.7, -0.6), 0.3, new DiffuseMaterial(Colour.YELLOW, Colour.BLACK)); //f
    private static final Sphere bigSphere = new Sphere(
        new Vector3(0.3, -0.4, 0.3), 0.6, new SpecularMaterial(Colour.LIGHTCYAN, Colour.BLACK, Colour.WHITE)); //g

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


    private Vector3 uniformRandomSphereVector(Vector3 sphereNormal) {
        Vector3 p;
        //while(true){
        do {
            p = new Vector3(UniformRandom.uniformMinus1to1(),
                            UniformRandom.uniformMinus1to1(),
                            UniformRandom.uniformMinus1to1());
        } while (p.length() <= 1.0f);

        if(Vector3.dot(p, sphereNormal) < 0) {
            p = p.multiply(-1);
        }

        return p;
    }

    public Vector3 ComputeColor(Vector3 o, Vector3 d) {
        Intersection closestHitPoint = FindClosestHitPoint(scene, o, d);

        if(closestHitPoint == null) {
            return Colour.BLACK;
        }

        if(UniformRandom.uniform0to1() < MONTE_CARLO_PROPABILITY) {
            return closestHitPoint.sphere().getEmissionColor();
        }


        Vector3 normalSphere = Vector3.normalize(Vector3.subtract(closestHitPoint.point(),closestHitPoint.sphere().getCenter()));
        Vector3 wR_hat = Vector3.normalize(uniformRandomSphereVector(normalSphere));
        Vector3 wR = Vector3.normalize(uniformRandomSphereVector(normalSphere));

        Vector3 sphereEmissions = closestHitPoint.sphere().getEmissionColor();
        float factor = (float)(2.0f * Math.PI * BRDF_CONSTANT);
        float wRTimesNormal = Vector3.dot(wR, normalSphere) * factor;
        Vector3 computeColor = ComputeColor(closestHitPoint.point(), wR_hat);
        Vector3 brdf = closestHitPoint.sphere().getMaterial().BRDFevaluate(Vector3.normalize(d), closestHitPoint.point(), closestHitPoint.sphere());
        return sphereEmissions.add(brdf.multiply(wRTimesNormal).multiply(computeColor));
    }

    /*public Vector3 BRDF(Vector3 wI, Vector3 wO, Vector3 color) {
        return color.multiply((float)(1.0f / Math.PI));
    }*/

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
        
        Vector3 closestHit = intersectionsInFront.stream().sorted((v1, v2) -> {
            float dist1 = Vector3.distanceSquared(o, v1.point());
            float dist2 = Vector3.distanceSquared(o, v2.point());
            return Float.compare(dist1, dist2);
        }).findFirst().orElseThrow(() -> new IllegalStateException("No intersections in front of the eye")).point();

        Intersection closestIntersection = intersectionsInFront.stream()
                                                .filter(v -> v.point().equals(closestHit))
                                                .findFirst()
                                                .orElseThrow(() -> new IllegalStateException("No intersection found for the closest hit point"));

        // Epsilon correct (avoid intersection inside of the sphere surface)
        Vector3 epsilonD = Vector3.normalize(d).multiply(10e-3f);
        Vector3 nHp = Vector3.normalize(closestIntersection.sphere().normalToPoint(closestHit));
        Vector3 epsilonUp = Vector3.normalize(nHp).multiply(10e-3f);
        Vector3 adjustedHit = Vector3.subtract(closestHit, epsilonD);
        adjustedHit = adjustedHit.add(epsilonUp);

        return new Intersection(adjustedHit, closestIntersection.sphere());
    }

    public Image run() {
        int[] pixels = new int[WIDTH * HEIGHT];

        int threadCount = Math.max(1, Runtime.getRuntime().availableProcessors());
        System.out.println("Using " + threadCount + " threads for rendering.");
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<?>> futures = new ArrayList<>();

        for (int y = 0; y < HEIGHT; y++) {
            final int row = y;
            futures.add(executor.submit(() -> {
                for (int x = 0; x < WIDTH; x++) {
                    int dstY = HEIGHT - 1 - row; // invert y for image coordinates
                    float px = (2.0f * x / (WIDTH - 1)) - 1.0f;
                    float py = (2.0f * row / (HEIGHT - 1)) - 1.0f;
                    Vector2 pixelNDC = new Vector2(px, py);
                    Ray eyeRay = CreateEyeRay(eye, lookAt, FOV, pixelNDC);

                    Vector3 color = new Vector3(0, 0, 0);
                    for (int s = 0; s < RAYS_PER_PIXEL; s++) {
                        color = color.add(ComputeColor(eyeRay.getEye(), eyeRay.getDirection()));
                    }
                    color = Colour.gammaCorrection(color.multiply(1.0f / RAYS_PER_PIXEL));
                    pixels[dstY * WIDTH + x] = Colour.toARGB32(color);
                }
            }));
        }

        try {
            for (Future<?> future : futures) {
                future.get();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Rendering interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Rendering task failed", e);
        } finally {
            executor.shutdown();
        }

        MemoryImageSource src = new MemoryImageSource(WIDTH, HEIGHT, pixels, 0, WIDTH);
        return Toolkit.getDefaultToolkit().createImage(src);
    }



    
}

