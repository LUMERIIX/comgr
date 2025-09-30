package Rays;

import java.util.Vector;

import Vectors.Vector3;

public class SpecularMaterial extends Material implements BRDF {

    private final static float BRDF_CONSTANT = 10.0f; // Adjust this constant to control the specular intensity
    private final static float EPSILON = 10e-3f;

    private Vector3 specularColor;

    public SpecularMaterial(Vector3 diffuseColor, Vector3 emissionColor, Vector3 specularColor) {
        super(diffuseColor, emissionColor);
        this.specularColor = specularColor;
    }


    @Override
    public Vector3 BRDFevaluate(Vector3 wI, Vector3 hp, RenderObject obj) {
        Vector3 n = obj.normalToPoint(hp);
        Vector3 dR = Vector3.reflect(wI, n);

        if( Vector3.dot(wI, dR) > 1 - EPSILON ) { // Check if reflection is in phase with spectular-angle
            //System.out.println("Specular hit");
            return obj.getDiffuseColor().multiply((float) (1.0f / Math.PI)).add(Vector3.multiply(specularColor, BRDF_CONSTANT));
        }
        else {
            return obj.getDiffuseColor().multiply((float) (1.0f / Math.PI)); // Diffuse Color only
        }
    }
    
}
