(set-scene-name "animation-lighting")

(let* ((mat-ground (Material. (Color. "FF888899") 0))
       (mat-obj-a (Material. (Color. "FFFF0000") 0))
       (mat-obj-a (Material. (Color. "FF0000FF") 0)))
  
  (set-camera-target 0.0 0.0 20.0)
  (set-camera-position 20.0 40.0 10.0)


  
  (set-scene-sdf (SDFPrimitiveGroundPlane. 0 mat-ground))

  (let* ((vec-center (Vector3d. 0.0 0.0 20.0))
	 (sdf-sphere (SDFPrimitiveSphere. vec-center 10.0 mat-obj-a)))
		    

    (set-sun-vector vec-center)


    (with-sdf sdf-sphere
	      (bool-difference (SDFPrimitiveSphere. vec-center 9.5 mat-obj-a))
	      (bool-difference (SDFPrimitiveCross. vec-center 2.0  mat-obj-a)))

    (set-scene-sdf (SDFBoolUnion. scene-sdf
				  (SDFPrimitiveCylinder. (Vector3d. 10.0 10.0 10.0) 5.0 10.0 mat-obj-a)))))





 
