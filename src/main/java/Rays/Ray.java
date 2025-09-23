package Rays;

import Vectors.Vector3;

public class Ray {

    private Vector3 eye;
    private Vector3 direction;

    public Ray(Vector3 e, Vector3 d) {
        this.eye = e;
        this.direction = d;
    }

    public Vector3 getEye() {
        return eye;
    }

    public Vector3 getDirection() {
        return direction;
    }
     
}
