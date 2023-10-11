(set-scene-name "chamfer-test")

(let* ((material-ground (MaterialDiffuse. (Color. "888899") 0))
       (material-objA (MaterialDiffuse. (Color. "FF0000") 0))
       (material-objB (MaterialDiffuse. (Color. "0000FF") 0)))
  
  (add-camera-position-keyframe 0 -29.0 88.0 49.0)
  (add-camera-target-keyframe 0 25.5 14.0 18.1)

  (set-sun-position 105.0 205.0 300.0)
  
  (set-scene-sdf (SDFPrimitiveGroundPlane. 0 material-ground))
  
  (let ((cube-a (SDFPrimitiveCube. (Vector3d. 0.0 0.0 20.0) 24.0 material-objA)))
    
    (set-scene-sdf (SDFBoolUnion. scene-sdf
				  (SDFOpChamfer. cube-a
						 (SDFPrimitiveCube. (Vector3d. 10.0 10.0 10.0) 22.0 material-objB)
						 0.5)))
    (set-scene-sdf (SDFBoolUnion. scene-sdf
				  (SDFOpChamfer. (SDFPrimitiveSphere. (Vector3d. 40.0 0.0 20.0) 10.0 material-objA)
						 (SDFPrimitiveSphere. (Vector3d. 50.0 10.0 10.0) 10.0 material-objB)
						 3)))
    (set-scene-sdf (SDFBoolUnion. scene-sdf
				  (SDFOpChamfer. (SDFPrimitiveSphere. (Vector3d. 80.0 0.0 20.0) 10.0 material-objA)
						 (SDFPrimitiveCube. (Vector3d. 90.0 10.0 10.0) 22.0 material-objB)
						 2))))
    )


				 





