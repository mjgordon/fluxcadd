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

(define (range end)
  (let ((n (- end 1)))
    (let loop ((n n) (accumulator '()))
      (if (< n 0)
          accumulator
          (loop (- n 1) (cons n accumulator))))))

(define pi 3.141592654)





	   
