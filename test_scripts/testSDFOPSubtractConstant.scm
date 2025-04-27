(set-scene-name "test-op-subtract-constant")

(let* ((material-cube (MaterialDiffuse. (Color3i. "EEC1AA") 0)))

  (add-camera-position-keyframe 0 15.0 10.0 15.0)
  (add-camera-target-keyframe 0 0.0 0.0 10.0)


  (set-scene-sdf (SDFOpSubtractConstant. 
                  (SDFPrimitiveCube. (Vector3d. 10.0 0.0 10.0)
                                     3.0
                                     3.0
                                     10.0
                                     material-cube)
                                 0.3)))
