(let* ((material-ground (Material. (Color. "444455") 0))
       (material-columns (Material. (Color. "EEEEDD") 0)))

  (set-camera-position 145.0 31.0 6.0)
  (set-camera-target 1.0 10.0 42.0)

  (set-sun-position 25.0 25.0 25.0)
  
  (set-scene-sdf (SDFPrimitiveGroundPlane. 0 material-ground))

  (let ((sdf-columns (SDFPrimitiveCross. (Vector3d. 0.0 0.0 30.0) 1.0 material-columns)))
    (with-sdf sdf-columns
	      (op-modulo 50.0 50.0 -1.0)
	      (op-subtract (SDFPrimitiveSimplex. material-ground 0.3) 0.5))
    (set-scene-sdf (SDFOpSmooth. scene-sdf sdf-columns 10.0))))
