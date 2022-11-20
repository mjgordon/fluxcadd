(let* ((material-ground (Material. (Color. "DDBEA8") 0))
       (material-sphere (Material. (Color. "F21395") 0)))

  (set-camera-position 50.0 10.0 10.0)
  (set-camera-target 0.0 10.0 5.0)

  (set-scene-sdf (SDFPrimitiveGroundPlane. 0 material-ground))
  (set-scene-sdf (SDFOpSubtract. scene-sdf (SDFPrimitiveSimplex. material-ground 0.05) 10.0))

  (let ((sdf-mollusk '()))
    (map (lambda (i)
	   (let ((vector-sphere (Vector3d. 0.0
					   (* i (- 5 (* i 0.2)))
					   (+ 5 (* i 0.7))))
		 (size (- 10.0 i)))
	     (if (= (modulo i 2) 0)
		 (if (null? sdf-mollusk)
		     (set! sdf-mollusk (SDFPrimitiveSphere. vector-sphere size material-sphere))
		     (set! sdf-mollusk (SDFOpSmooth. sdf-mollusk 
						    (SDFPrimitiveSphere. vector-sphere size material-sphere)
						    2.0)))
		 (set! sdf-mollusk (SDFBoolDifference. sdf-mollusk (SDFPrimitiveSphere. vector-sphere
											size
											material-sphere))))))
	 (range 10))
    (set-scene-sdf (SDFOpSmooth. scene-sdf sdf-mollusk 2.0))))




   

