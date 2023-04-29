(set-scene-name "animation-test")

(let* ((material-ground (Material. (Color. "888899") 0))
       (material-obj-a (Material. (Color. "9CFFFA") 0))
       (material-obj-b (Material. (Color. "E85F5C") 0)))
  
  (set-camera-position 5.0 45.0 6.0)
  (set-camera-target 5.0 6.5 10.0)

  (set-sun-position 7.5 10.0 10.0)
  
  (set-scene-sdf (SDFPrimitiveGroundPlane. 0 material-ground))

  (let ((cross-a (SDFPrimitiveCross. (Vector3d. 0.0 0.0 0.0) 2.0 material-obj-a))
	(cross-b (SDFPrimitiveCross. (Vector3d. 10.0 0.0 0.0) 2.0 material-obj-b)))
    (.addKeyframe cross-b 0.0 (.setTranslation (Matrix4d.) (Vector3d. -10.0 0.0 0.0)))
    (.addKeyframe cross-b 120.0 (.setTranslation (Matrix4d.) (Vector3d. 10.0 0.0 20.0)))
    (set-scene-sdf (SDFBoolUnion. scene-sdf
				  (SDFOpSmooth. (SDFOpModulo. cross-a 20.0)
						(SDFOpModulo. cross-b 20.0)
						3.0)))))
		 
	    
