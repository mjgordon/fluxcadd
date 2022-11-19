;;; Master file for fluxcadd scheme integration
;;; This will change a *lot* over time

(import "geometry.*")
(import "scheme.SchemeEnvironment")

(load "scheme/geometry/curve.scm")
(load "scheme/geometry/point.scm")

;;; Overhead stuff

(define geometry ())

(define (set-geometry geo)
  (set! geometry geo))

(define (make-list n)
  (let loop ((n n) (accumulator '()))
    (if (zero? n)
        accumulator
        (loop (- n 1) (cons n accumulator)))))

	    



	   
