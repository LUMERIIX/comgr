package Rays;

import Vectors.Vector3;

public class DiffuseMaterial extends Material {
    public DiffuseMaterial(Vector3 diffuseColor, Vector3 emissionColor) {
        super(diffuseColor, emissionColor);
    }

    @Override
    public Vector3 BRDFevaluate(Vector3 wI, Vector3 hp, RenderObject obj) {
        return obj.getDiffuseColor().multiply((float) (1.0 / Math.PI));
    }
    
}
