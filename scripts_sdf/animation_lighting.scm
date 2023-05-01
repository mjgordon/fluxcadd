(set-scene-name "animation-lighting")

(define (get-cylinder position radius height material rx ry rz)
  (SDFPrimitiveCylinder. (.setColumn (.rotationZYX (Matrix4d.) rx ry rz)
				     3
				     (Vector4d. position 1.0))
			 radius
			 height
			 material))
  

(let* ((mat-ground (Material. (Color. "FF888899") 0))
       (mat-obj-a (Material. (Color. "FFFF0000") 0))
       (mat-obj-b (Material. (Color. "FF0000FF") 0))
       (mat-obj-c (Material. (Color. "FFFFFF00") 0)))
  
  (set-camera-target 0.0 0.0 20.0)
  (set-camera-position 20.0 40.0 10.0)

  (set-scene-sdf (SDFPrimitiveGroundPlane. 0 mat-ground))

  (let* ((vec-center (Vector3d. 0.0 0.0 20.0))
	 (vec-cylinder (Vector3d. 0.0 0.0 40.0))
	 (sdf-sphere (SDFPrimitiveSphere. vec-center 10.0 mat-obj-a))
	 (hole-size 2.0)
	 (pi java.lang.Math.PI$))
		    
    (set-sun-vector vec-center)

    (with-sdf sdf-sphere
	      (bool-difference (SDFPrimitiveSphere. vec-center 9.5 mat-obj-c))
	      (bool-difference (SDFPrimitiveCylinder. vec-center hole-size 20.0 mat-obj-b))

	      (bool-difference (get-cylinder vec-center hole-size 20.0 mat-obj-b 0.0 (/ pi 4) 0.0))
	      (bool-difference (get-cylinder vec-center hole-size 20.0 mat-obj-b 0.0 (/ pi 2) 0.0))
	      (bool-difference (get-cylinder vec-center hole-size 20.0 mat-obj-b 0.0 (/ pi -4) 0.0))

	      (bool-difference (get-cylinder vec-center hole-size 20.0 mat-obj-b (/ pi 4) (/ pi 2) 0.0))
	      (bool-difference (get-cylinder vec-center hole-size 20.0 mat-obj-b (/ pi 2) (/ pi 2) 0.0))
	      (bool-difference (get-cylinder vec-center hole-size 20.0 mat-obj-b (/ pi -4) (/ pi 2) 0.0))

	      (bool-difference (get-cylinder vec-center hole-size 20.0 mat-obj-b 0.0 0.0 (/ pi 4)))
	      (bool-difference (get-cylinder vec-center hole-size 20.0 mat-obj-b 0.0 0.0 (/ pi -4)))
	      )

	    

    (with-sdf scene-sdf
	      (bool-union sdf-sphere)
	      (bool-union (SDFBoolDifference. (SDFPrimitiveCylinder. vec-cylinder 50.0 70.0 mat-obj-b)
					      (SDFPrimitiveCylinder. vec-cylinder 49.0 72.0 mat-obj-b))))))





 
