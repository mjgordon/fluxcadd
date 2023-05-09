(set-scene-name "animation-lighting")

(define (get-cylinder position radius height material rx ry rz)
  (SDFPrimitiveCylinder. (.setColumn (.rotationZYX (Matrix4d.) rx ry rz)
				     3
				     (Vector4d. position 1.0))
			 radius
			 height
			 material))
  

(let* ((mat-ground (Material. (Color. "FF558B6E") 0))
       (mat-obj-a (Material. (Color. "FF545775") 0))
       (mat-obj-b (Material. (Color. "FFFFF200") 0))
       (mat-obj-c (Material. (Color. "FFAEECEF") 0)))
  
  (set-camera-target 0.0 0.0 20.0)
  (set-camera-position 15.0 35.0 5.0)

  (set-scene-sdf (SDFPrimitiveGroundPlane. 0 mat-ground))

  (let* ((vec-center (Vector3d. 0.0 0.0 20.0))
	 (vec-cylinder (Vector3d. 0.0 0.0 40.0))
	 (sdf-sphere (SDFPrimitiveSphere. vec-center 10.0 mat-obj-a))
	 (hole-size 3.6)
	 (hole-size-2 2.2)
	 (pi java.lang.Math.PI$)
	 (magic-angle (java.lang.Math.acos (/ 1 (sqrt 3) ))))
		    
    (add-sun-keyframe 0.0 0.0 0.0 1.0)
    (add-sun-keyframe 120.0 0.0 0.0 40.0)
    (add-sun-keyframe 240.0 0.0 0.0 1.0)

    (with-sdf sdf-sphere
	      (bool-difference (SDFPrimitiveSphere. vec-center 9.5 mat-obj-b))
	      (bool-difference (SDFPrimitiveCylinder. vec-center hole-size 20.0 mat-obj-b))

	      (bool-difference (get-cylinder vec-center hole-size 20.0 mat-obj-b 0.0 (/ pi 4) 0.0))
	      (bool-difference (get-cylinder vec-center hole-size 20.0 mat-obj-b 0.0 (/ pi 2) 0.0))
	      (bool-difference (get-cylinder vec-center hole-size 20.0 mat-obj-b 0.0 (/ pi -4) 0.0))

	      (bool-difference (get-cylinder vec-center hole-size 20.0 mat-obj-b (/ pi 4) (/ pi 2) 0.0))
	      (bool-difference (get-cylinder vec-center hole-size 20.0 mat-obj-b (/ pi 2) (/ pi 2) 0.0))
	      (bool-difference (get-cylinder vec-center hole-size 20.0 mat-obj-b (/ pi -4) (/ pi 2) 0.0))

	      (bool-difference (get-cylinder vec-center hole-size 20.0 mat-obj-b 0.0 0.0 (/ pi 4)))
	      (bool-difference (get-cylinder vec-center hole-size 20.0 mat-obj-b 0.0 0.0 (/ pi -4)))

	      (bool-difference (get-cylinder vec-center hole-size-2 20.0 mat-obj-b (/ pi 4) magic-angle 0.0))
	      (bool-difference (get-cylinder vec-center hole-size-2 20.0 mat-obj-b (/ pi -4) magic-angle 0.0))
	      (bool-difference (get-cylinder vec-center hole-size-2 20.0 mat-obj-b (/ pi 4) (* -1 magic-angle) 0.0))
	      (bool-difference (get-cylinder vec-center hole-size-2 20.0 mat-obj-b (/ pi -4) (* -1 magic-angle) 0.0))
	      )

    (set! sdf-sphere (SDFOpTransform. sdf-sphere))

    (.addKeyframe sdf-sphere 0.0 (.rotationZ (Matrix4d.) 0.0))
    (.addKeyframe sdf-sphere 60.0 (.rotationZ (Matrix4d.) (* pi 0.5)))
    (.addKeyframe sdf-sphere 120.0 (.rotationZ (Matrix4d.) (* pi 1)))
    (.addKeyframe sdf-sphere 180.0 (.rotationZ (Matrix4d.) (* pi 1.5)))
    (.addKeyframe sdf-sphere 240.0 (.rotationZ (Matrix4d.) (* pi 2)))

	    

    (with-sdf scene-sdf
	      (bool-union sdf-sphere)
	      (bool-union (SDFBoolDifference. (SDFPrimitiveSphere. vec-cylinder 100.0 mat-obj-c)
					      (SDFPrimitiveSphere. vec-cylinder 98.0 mat-obj-c))))))





 
