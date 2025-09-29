package Rays;
import java.util.ArrayList;
import java.util.List;

import Vectors.Vector3;

public class Sphere {
    private Vector3 center;
    private double radius;
    private Vector3 diffuseColor;
    private Vector3 emissionColor;

    public Sphere(Vector3 center, double radius, Vector3 diffuseColor, Vector3 emissionColor) {
        this.center = center;
        this.radius = radius;
        this.diffuseColor = diffuseColor;
        this.emissionColor = emissionColor;
    }

    public Vector3 getCenter() {
        return center;
    }

    public double getRadius() {
        return radius;
    }

    public Vector3 getDiffuseColor() {
        return diffuseColor;
    }

    public Vector3 getEmissionColor() {
        return emissionColor;
    }

    private record MidNightResult(boolean hasSolutions, float t1, float t2) {}

    private MidNightResult solveQuadratic(float a, float b, float c) {
        float discriminant = b * b - 4 * a * c;
        if (discriminant < 0) {
            return new MidNightResult(false, 0, 0);
        } else {
            float sqrtDiscriminant = (float) Math.sqrt(discriminant);
            float t1 = (-b - sqrtDiscriminant) / (2 * a);
            float t2 = (-b + sqrtDiscriminant) / (2 * a);
            return new MidNightResult(true, t1, t2);
        }
    }

    // Returns a list of intersection points (can be empty)
    public List<Intersection> intersect(Ray ray) {
        List<Intersection> intersections = new ArrayList<>();
        Vector3 co = ray.getEye().subtract(center);

        // calc potential intersections based on scaled ray-vector
        float a = ray.getDirection().lengthSquared();
        float b = 2.0f * Vector3.dot(co, ray.getDirection());
        float c = co.lengthSquared() - (float) (radius * radius);
        MidNightResult result = solveQuadratic(a, b, c);

        if(result.hasSolutions) {
            if(result.t1 >= 0) {
                Vector3 intersection1 = ray.getEye().add(ray.getDirection().multiply(result.t1));
                intersections.add(new Intersection(intersection1, this));
            }
            if(result.t2 >= 0) {
                Vector3 intersection2 = ray.getEye().add(ray.getDirection().multiply(result.t2));
                intersections.add(new Intersection(intersection2, this));
            }
        }

        return intersections;
    }
}
