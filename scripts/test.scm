(.add geometry (PointCloud. "demo_data/point_clouds/structure_sparse.xyz"))

(let* ((a (point 5 5 5))
       (b (point 10 10 10))
       (l (line-2pt a b)))
  (add-point (evaluate-curve l 0.2f)))
    
(point (+ 10 5)
       (+ 12 3)
       (+ (+ 3 3) 9))

(polyline (map (lambda (i)
                (point (* 2 (sin (/ i 3.0))) i 0))
               (range 100)))
