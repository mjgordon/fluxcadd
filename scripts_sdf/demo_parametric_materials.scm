(let* ((material-ground (MaterialDiffuse. (Color. "444455") 0))
       (material-cube (MaterialSimplex. (Color. "FF0000") 0.0
					(Color. "0000FF") 0.0
					0.1)))

  (set-camera-position 25.0 37.0 3.0)
  (set-camera-target 6.5 8.0 14.1)

  (set-sun-position 105.0 205.0 300.0)
  
  (set-scene-sdf (SDFPrimitiveGroundPlane. 0 material-ground))

  (set-scene-sdf (SDFBoolUnion. scene-sdf (SDFPrimitiveCube. (Vector3d. 0.0 0.0 20.0) 30.0 material-cube)))

  (set-scene-sdf (SDFBoolDifference. scene-sdf (SDFOpSubtract. (SDFPrimitiveSimplex. material-cube 0.1) 0.5)))

  )








