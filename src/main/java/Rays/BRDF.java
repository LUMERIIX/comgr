package Rays;

import Vectors.Vector3;

public interface BRDF {
    // wI = Input direction (ray direction)
    // hp = Hit point on the RenderObject
    // obj = The RenderObject that was hit
    Vector3 BRDFevaluate(Vector3 wI, Vector3 hp, RenderObject obj);
}
