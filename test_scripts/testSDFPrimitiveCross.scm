(set-scene-name "test-primitive-cross")

(let* ((material-ground (MaterialDiffuse. (Color3i. "3D5A80") 0))
       (material-cross (MaterialDiffuse. (Color. "98C1D9") 0)))

  (add-camera-position-keyframe 0 50.0 5.0 2.0)
  (add-camera-target-keyframe 0 0.0 0.0 10.0)

  (set-scene-sdf (SDFPrimitiveGroundPlane. 0 material-ground))

  (set-scene-sdf (SDFBoolUnion. scene-sdf (SDFPrimitiveCross. (Vector3d. 0.0 0.0 10.0)
                                                             3.0
                                                             material-cross))))
