(set-scene-name "test-animation")

(let* ((material-sphere (MaterialDiffuse. (Color3i. "98C1D9") 0))
       (material-void (MaterialDiffuse. (Color3i. "EEC1D9") 0)))

  (add-camera-position-keyframe 0 15.0 10.0 5.0)
  (add-camera-target-keyframe 0 0.0 0.0 10.0)

  (let ((sphere (SDFPrimitiveSphere. (Vector3d. 0.0 0.0 10.0)
                                     10.0
                                     material-sphere)))
    (.addKeyframe sphere 0.0 (.setTranslation (Matrix4d.) (Vector3d. 0.0 0.0 10.0)))
    (.addKeyframe sphere 50.0 (.setTranslation (Matrix4d.) (Vector3d. 0.0 0.0 20.0)))


    (set-scene-sdf sphere)))


