;;; Main file for fluxcadd scheme integration

(import "geometry.*")
(import "scheme.SchemeEnvironment")

(load "scheme/geometry/curve.scm")
(load "scheme/geometry/point.scm")

;; Note, it seems that the modulo implementation in JScheme doesn't take doubles, 
;; and the remainder implementation returns negatives sometimes even for positive inputs? 
(define (mod-custom a b)
	(- a (* b (floor (/ a b)))))

;; Color
(define (hsv-to-rgb h s v)
	(let* ((sector (/ 1 6.0))
					(h-sector (/ h sector))
					(c (* v s))
					(x (* c (- 1 (abs (- (mod-custom h-sector 2) 1)))))
					(rgb-point (cond ((< h-sector 1) (list c x 0))
														((< h-sector 2) (list x c 0))
														((< h-sector 3) (list 0 c x))
														((< h-sector 4) (list 0 x c))
														((< h-sector 5) (list x 0 c))
														((< h-sector 6) (list c 0 x))))
					(m (- v c)))
		(list (* 255.0 (+ (first rgb-point) m)) 
					(* 255.0 (+ (second rgb-point) m))
					(* 255.0 (+ (third rgb-point) m)))))
														
		

;; Math

(define (range end)
  (let ((n (- end 1)))
    (let loop ((n n) (accumulator '()))
      (if (< n 0)
          accumulator
          (loop (- n 1) (cons n accumulator))))))

(define pi 3.141592654)

;; Scene 

(define geometry ())

(define (set-geometry geo)
  (set! geometry geo))








	   
