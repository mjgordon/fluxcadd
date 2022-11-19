(let* ((material-main (Material. (Color. 255 0 0) 0))
       (material-carve (Material. (Color. 0 0 255) 0))
       (material-reflect (Material. (Color. 255 255 255) 1)))

  (set-camera-position 100.0 0.0 30.0)
  (set-camera-target 0.0 0.0 -10.0)
  
  (set-scene-sdf (SDFPrimitiveGroundPlane. 0 material-main))
  (set-scene-sdf (SDFBoolDifference. scene-sdf
				     (SDFPrimitiveSphere. (Vector3d. 0.0 0.0 0.0) 30 material-carve)))
  
  (set-scene-sdf (SDFBoolDifference. scene-sdf
				     (SDFPrimitiveSphere. (Vector3d. -35.0 0.0 0.0) 20 material-carve)))
  (set-scene-sdf (SDFBoolDifference. scene-sdf
				     (SDFPrimitiveSphere. (Vector3d. 35.0 0.0 0.0) 20 material-carve)))
  (set-scene-sdf (SDFBoolDifference. scene-sdf
				     (SDFPrimitiveSphere. (Vector3d. 70.0 0.0 0.0) 20 material-carve)))
  (set-scene-sdf (SDFBoolDifference. scene-sdf
				     (SDFPrimitiveSphere. (Vector3d. 105.0 0.0 0.0) 20 material-carve)))

  (set-scene-sdf (SDFBoolUnion. scene-sdf
				(SDFPrimitiveCube. (Vector3d. 0.0 20.0 10.0) 10.0 material-main)))
  (set-scene-sdf (SDFOpChamfer. scene-sdf
				(SDFPrimitiveSphere. (Vector3d. 0.0 25.0 15.0) 5 material-carve)
				1.0))

  (set-scene-sdf (SDFBoolUnion. scene-sdf
				(SDFPrimitiveCube. (Vector3d. 0.0 -20.0 10.0) 10.0 material-main)))
  (set-scene-sdf (SDFOpSmooth. scene-sdf
			       (SDFPrimitiveSphere. (Vector3d. 0.0 -25.0 15.0) 5 material-carve)
			       1.0))

  (set-scene-sdf (SDFBoolUnion. scene-sdf
				(SDFPrimitiveSphere. (Vector3d. -30.0 0.0 15.0) 10.0 material-reflect)))
  



  )




   

