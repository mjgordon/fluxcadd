(let* ((material-ground (Material. (Color. "FAC748") 0))
       (material-star (Material. (Color. "8390FA") 0)))

  (set-camera-position 42.0 7.0 1.0)
  (set-camera-target -0.5 1.4 18.0)
  
  (set-scene-sdf (SDFPrimitiveGroundPlane. 0 material-ground))

  (set-scene-sdf (SDFBoolUnion. scene-sdf (SDFPrimitiveStar. (Vector3d. 0.0 0.0 20.0) 40.0 material-star))))





