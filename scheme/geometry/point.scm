(import "utility.Util")

(define (add-point point)
  (.add geometry point))

(define (point x y z)
  (let ((p (Point. (Util.explicitFloat x)
		   (Util.explicitFloat y)
		   (Util.explicitFloat z))))
    (.add geometry p)
    p))
