(set-scene-name "test-primitive-ground-plane")

(let* ((material-ground (MaterialDiffuse. (Color3i. "3D5A80") 0)))
  (add-camera-position-keyframe 0 20.0 5.0 7.0)
  (add-camera-target-keyframe 0 0.0 0.0 10.0)

  (set-scene-sdf (SDFPrimitiveGroundPlane. 0 material-ground)))

  
