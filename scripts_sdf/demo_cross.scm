(let* ((material-ground (Material. (Color. "3D5A80") 0))
       (material-cross (Material. (Color. "98C1D9") 0))
       (material-cut (Material. (Color. "EE6C4D") 0)))

  (set-camera-position 112.0 9.0 2.0)
  (set-camera-target 0.0 0.0 10.0)

  (set-scene-sdf (SDFPrimitiveGroundPlane. 0 material-ground))

  (map (lambda (i)
	 (let* ((m-base (Matrix4d.))
		(m-column (.setColumn m-base 3 (Vector4d. (* i 10.0) 0.0 20.0 1.0)))
		(m-rotate (.rotate m-column (* (/ pi 24.0) i) 1.0 0.0 0.0)))
	   (set-scene-sdf (SDFOpSmooth. scene-sdf
					(SDFPrimitiveCross. m-rotate 2.0 material-cross)
					3.0))))
       (range 10))

  (set-scene-sdf (SDFBoolDifference. scene-sdf
				     (SDFPrimitiveSphere. (Vector3d. 60.0 10.0 15.0) 10.0 material-cut))))




   

