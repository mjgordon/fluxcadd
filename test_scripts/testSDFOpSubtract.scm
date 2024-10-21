(set-scene-name "test-op-subtract")

(let* ((material-sphere (MaterialDiffuse. (Color3i. "98C1D9") 0))
       (material-cube (MaterialDiffuse. (Color. "EEC1AA") 0)))

  (add-camera-position-keyframe 0 15.0 10.0 15.0)
  (add-camera-target-keyframe 0 0.0 0.0 10.0)


  (set-scene-sdf (SDFOpSubtract. (SDFPrimitiveSphere. (Vector3d. 0.0 0.0 10.0)
                                                      10.0
                                                      material-sphere)
                                 (SDFPrimitiveCube. (Vector3d. 10.0 0.0 10.0)
                                                    3.0
                                                    3.0
                                                    10.0
                                                    material-cube)
                                 0.3)))
