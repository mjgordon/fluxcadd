(set-scene-name "animation-lighting")

(let* ((material-ground (Material. (Color. "FF888899") 0))
       (material-obj-a (Material. (Color. "FFFF0000") 0))
       (material-obj-a (Material. (Color. "FF0000FF") 0)))
  
  (set-camera-target 0.0 0.0 20.0)
  (set-camera-position 20.0 40.0 10.0)

  (set-sun-position 105.0 205.0 300.0)
  
  (set-scene-sdf (SDFPrimitiveGroundPlane. 0 material-ground))

  (set-scene-sdf (SDFBoolUnion. scene-sdf (SDFPrimitiveStar. (Vector3d. 0.0 0.0 20.0)
							     20.0
							     material-obj-a))))

 
