(let* ((material-ground (Material. (Color. "444455") 0))
       (material-cube (Material. (Color. "FF0000") 0)))

  (set-camera-position 25.0 37.0 3.0)
  (set-camera-target 6.5 8.0 14.1)

  (set-sun-position 105.0 205.0 300.0)
  
  (set-scene-sdf (SDFPrimitiveGroundPlane. 0 material-ground))

  (set-scene-sdf (SDFBoolUnion. scene-sdf (SDFPrimitiveCube. (Vector3d. 0.0 0.0 20.0) 20.0 material-cube))))





