(set-scene-name "test-primitive-diamond")

(let* ((material-ground (MaterialDiffuse. (Color. "3D5A80") 0))
       (material-diamond (MaterialDiffuse. (Color. "98C1D9") 0)))

  (add-camera-position-keyframe 0 20.0 5.0 20.0)
  (add-camera-target-keyframe 0 0.0 0.0 10.0)

  (set-scene-sdf (SDFPrimitiveGroundPlane. 0 material-ground))

  (set-scene-sdf (SDFBoolUnion. scene-sdf (SDFPrimitiveDiamond. (Vector3d. 0.0 0.0 10.0)
                                                                 10.0
                                                                 material-diamond))))
