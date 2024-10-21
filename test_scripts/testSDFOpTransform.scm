(set-scene-name "test-op-transform")

(let* ((material-sphere (MaterialDiffuse. (Color3i. "98C1D9") 0)))

  (add-camera-position-keyframe 0 15.0 10.0 15.0)
  (add-camera-target-keyframe 0 0.0 0.0 10.0)


  (set-scene-sdf (SDFOpTransform. (SDFPrimitiveSphere. (Vector3d. 0.0 0.0 10.0)
                                                       10.0
                                                       material-sphere)
                                  (Vector3d. -5.0 -5.0 -5.0))))
