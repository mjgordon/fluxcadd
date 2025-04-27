;;; Main file for fluxcadd scheme integration

(import "geometry.*")
(import "scheme.SchemeEnvironment")

(load "scheme/geometry/curve.scm")
(load "scheme/geometry/point.scm")


;; Scene 

(define geometry ())

(define (set-geometry geo)
  (set! geometry geo))


;; Math

(define (range end)
  (let ((n (- end 1)))
    (let loop ((n n) (accumulator '()))
      (if (< n 0)
          accumulator
          (loop (- n 1) (cons n accumulator))))))

(define pi 3.141592654)





	   
