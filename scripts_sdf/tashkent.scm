(let* ((material-ground (Material. (Color. "9999AA") 0.0))
       (material-building (Material. (Color. "661e58") 0.0))
       (material-window (Material. (Color. "f5d742") 0.0)))
       

  (set-camera-position 146.0 27.0 1.0)
  (set-camera-target 7.9 3.4 53.9)

  (set-sun-position 20.0 50.0 100.0)

  (.setSkyColor scene-render (Color. "BBBBEE"))
  
  (set-scene-sdf (SDFPrimitiveGroundPlane. 0 material-ground))

  (let* ((block (SDFBoolIntersection. (SDFBoolDifference. (SDFOpSubtract. (SDFPrimitiveCube. (Vector3d. 0.0 0.0 0.0)
											     10.0
											     material-building)
									  1.0)
							  (SDFOpSubtract. (SDFPrimitiveCube. (Vector3d. 0.0 0.0 0.0)
											     10.0
											     material-building)
									  0.9))
				      (SDFPrimitiveCube. (Vector3d. 0.0 0.0 0.0) 4.0 40.0 40.0 material-building)))
	 (block-array (SDFOpModulo. block -1.0 25.0 25.0))
	 (block-array2 (SDFOpTranslate. block-array (Vector3d. 0.0 12.5 12.5)))
	 (block-array-all (SDFBoolUnion. block-array block-array2))
	 (block-slice (SDFBoolIntersection. block-array-all (SDFPrimitiveCube. (Vector3d. 0.0 0.0 50.0)
									       4.0
									       100.0
									       100.0
									       material-building)))
	 (windows (SDFOpTranslate. (SDFOpModulo.
				    (SDFPrimitiveCube. (Vector3d. -2.0 0.0 0.0) 5.0 material-window) 
				    -1.0 12.5 12.5)
				   (Vector3d. 0.0 6.25 6.25)))
	 (wall (SDFBoolDifference. (SDFPrimitiveCube. (Vector3d. -5.0 0.0 50.0) 10.0 100.0 100.0 material-building)
				   windows))
	 (building (SDFBoolUnion. wall block-slice)))
    
    (set-scene-sdf (SDFBoolUnion. scene-sdf building))))







