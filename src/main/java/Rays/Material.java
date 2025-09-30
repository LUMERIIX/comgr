package Rays;

import Vectors.Vector3;

public abstract class Material implements BRDF {
    private Vector3 diffuseColor;
    private Vector3 emissionColor;

    public Material(Vector3 diffuseColor, Vector3 emissionColor) {
        this.diffuseColor = diffuseColor;
        this.emissionColor = emissionColor;
    }

    public Vector3 getDiffuseColor() {
        return diffuseColor;
    }

    public Vector3 getEmissionColor() {
        return emissionColor;
    }
}
