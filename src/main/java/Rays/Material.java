package Rays;

import Vectors.Vector3;

public class Material {
    private Vector3 diffuseColor;
    private Vector3 emissionColor;
    private BRDF brdf;

    public Material(Vector3 diffuseColor, Vector3 emissionColor, BRDF brdf) {
        this.diffuseColor = diffuseColor;
        this.emissionColor = emissionColor;
        this.brdf = brdf;
    }

    public Vector3 getDiffuseColor() {
        return diffuseColor;
    }

    public BRDF getBrdf() {
        return brdf;
    }

    public Vector3 getEmissionColor() {
        return emissionColor;
    }
}
