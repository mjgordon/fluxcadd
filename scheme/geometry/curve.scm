(define (line-2pt a b)
  (let ((l (Line. a b)))
    (.add geometry l)
    l))

(define (polyline points)
  (let ((pl (Polyline. points)))
    (.add geometry pl)
    pl))

(define (evaluate-curve curve p)
  (.getPointOnCurve curve p))
