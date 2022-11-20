(import "org.joml.*")

(import "render_sdf.material.*")
(import "render_sdf.renderer.*")
(import "render_sdf.sdf.*")
(import "utility.*")

(define scene-render ())
(define scene-sdf ())

(define (set-scene-render s)
  (set! scene-render s))

(define (set-scene-sdf s)
  (set! scene-sdf s))


(define (set-camera-position x y z)
  (.setPosition (.camera$ scene-render) (Vector3d. x y z)))


(define (set-camera-target x y z)
  (.setTarget (.camera$ scene-render) (Vector3d. x y z)))


(define (set-sun-position x y z)
  (.set (.sunPosition$ scene-render) x y z))


(define with-sdf
  (macro (sdf . body)
    (cons 'begin
	  (map (lambda (op)
		 (case (first op)
		   ('bool-union
		    `(set! ,sdf (SDFBoolUnion. ,sdf ,(second op))))
		   ('bool-difference
		    `(set! ,sdf (SDFBoolDifference. ,sdf ,(second op))))
		   ('op-chamfer
		    `(set! ,sdf (SDFOpChamfer. ,sdf ,(second op) ,(third op))))
		   ('op-modulo
		    `(set! ,sdf (SDFOpModulo. ,sdf ,(second op))))
		   ('op-smooth
		    `(set! ,sdf (SDFOpSmooth. ,sdf ,(second op) ,(third op))))
		   ('op-subtract
		    `(set! ,sdf (SDFOpSubtract. ,sdf ,(second op) ,(third op))))))
	       body))))


