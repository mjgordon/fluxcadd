(set-scene-name "test-primitive-sphere")

(let* ((material-ground (MaterialDiffuse. (Color3i. "3D5A80") 0))
       (material-sphere (MaterialDiffuse. (Color3i. "98C1D9") 0)))

  (add-camera-position-keyframe 0 20.0 5.0 7.0)
  (add-camera-target-keyframe 0 0.0 0.0 10.0)

  (set-scene-sdf (SDFPrimitiveGroundPlane. 0 material-ground))

  (set-scene-sdf (SDFBoolUnion. scene-sdf (SDFPrimitiveSphere. (Vector3d. 0.0 0.0 10.0)
                                                               10.0
                                                               material-sphere))))
