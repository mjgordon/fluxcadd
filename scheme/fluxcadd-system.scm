;;; Master file for fluxcadd scheme integration
;;; This will change a *lot* over time

(import "geometry.*")

;;; Overhead stuff

(define geometry ())

(define (set-geometry geo)
  (set! geometry geo))

;;; Geometry functionality

(define (point x y z)
  (let ((p (Point. x y z)))
    (.add geometry p)
    p))
