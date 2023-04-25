(set-scene-name "chamfer-test")

(let* ((material-ground (Material. (Color. "888899") 0))
       (material-objA (Material. (Color. "FF0000") 0))
       (material-objB (Material. (Color. "0000FF") 0)))
  
  (set-camera-position -29.0 88.0 49.0)
  (set-camera-target 25.5 14.0 18.1)

  (set-sun-position 105.0 205.0 300.0)
  
  (set-scene-sdf (SDFPrimitiveGroundPlane. 0 material-ground))

  (let ((cube-a (SDFPrimitiveCube. (Vector3d. 0.0 0.0 20.0) 20.0 material-objA)))
    (.addKeyframe cube-a 0.0 (.setTranslation (.scale (Matrix4d.) 10.0) (Vector3d. 0.0 0.0 20.0)))
    (.addKeyframe cube-a 20.0 (.setTranslation (.scale (Matrix4d.) 10.0) (Vector3d. 100.0 0.0 20.0)))
    (set-scene-sdf (SDFBoolUnion. scene-sdf
				  (SDFOpChamfer. cube-a
						 (SDFPrimitiveCube. (Vector3d. 10.0 10.0 10.0) 20.0 material-objB)
						 0.5)))
    (set-scene-sdf (SDFBoolUnion. scene-sdf
				  (SDFOpChamfer. (SDFPrimitiveSphere. (Vector3d. 40.0 0.0 20.0) 10.0 material-objA)
						 (SDFPrimitiveSphere. (Vector3d. 50.0 10.0 10.0) 10.0 material-objB)
						 3)))
    (set-scene-sdf (SDFBoolUnion. scene-sdf
				  (SDFOpChamfer. (SDFPrimitiveSphere. (Vector3d. 80.0 0.0 20.0) 10.0 material-objA)
						 (SDFPrimitiveCube. (Vector3d. 90.0 10.0 10.0) 20.0 material-objB)
						 2))))
    )


				 





