(let* ((material-ground (MaterialDiffuse. (Color. "444455") 0))
       (material-cube (MaterialDiffuse. (Color. "FF0000") 0)))

  (add-camera-position-keyframe 0 25.0 37.0 3.0)
  (add-camera-target-keyframe 0 6.5 8.0 14.1)

  (set-sun-position 105.0 205.0 300.0)
  
  (set-scene-sdf (SDFPrimitiveGroundPlane. 0 material-ground))

  (set-scene-sdf (SDFBoolUnion. scene-sdf (SDFPrimitiveCube. (Vector3d. 0.0 0.0 20.0) 20.0 material-cube))))





