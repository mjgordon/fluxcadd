;;; Master file for fluxcadd scheme integration
;;; This will change a *lot* over time

(import "geometry.*")
(import "utility.Util")

;;; Overhead stuff

(define geometry ())

(define (set-geometry geo)
  (set! geometry geo))

;;; Geometry functionality

(define (point x y z)
  (let ((p (Point. (Util.explicitFloat x)
		   (Util.explicitFloat y)
		   (Util.explicitFloat z))))
    (.add geometry p)
    p))

(define (line-2pt a b)
  (let ((l (Line. a b)))
    (.add geometry l)
    l))
	   
