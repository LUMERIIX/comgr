package Rays;

import Vectors.Vector3;

public interface RenderObject {

    Vector3 normalToPoint(Vector3 point);
    Vector3 getDiffuseColor();
    Vector3 getEmissionColor();
    
}
