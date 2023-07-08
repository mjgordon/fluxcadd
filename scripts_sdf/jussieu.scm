(let* ((material-ground (MaterialDiffuse. (Color. "404050") 0.0))
       (material-building (MaterialDiffuse. (Color. "404050") 0.0)))
  

  (set-camera-position -9.0 100.0 32.0)
  (set-camera-target -2.0 0.0 26.0)

  (set-sun-position 3.0 -70.0 34.0)

  (.setSkyColor scene-render (Color. "b0e4e9"))
  
  (set-scene-sdf (SDFPrimitiveGroundPlane. 0 material-ground))

  (for-each
   (lambda (z)
     (set-scene-sdf (SDFBoolUnion. scene-sdf (SDFPrimitiveCube. (Vector3d. 0.0 0.0 (* 8.0 z))
                                                                100.0 100.0 1.0 material-building))))
   (range 8))

  (for-each
   (lambda (x)
     (for-each
      (lambda (y)
        (set-scene-sdf (SDFBoolUnion. scene-sdf (SDFPrimitiveCube. (Vector3d. (- (* x 16.0) 48)
                                                                              (- (* y 16.0) 48)
                                                                              32.0)
                                                                   1.0 1.0 64.0 material-building))))
      (range 7)))
   (range 7)))
  

  (with-sdf scene-sdf
            ())

  

)
