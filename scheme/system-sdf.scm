(import "org.joml.*")

(import "render_sdf.material.*")
(import "render_sdf.renderer.*")
(import "render_sdf.sdf.*")
(import "utility.*")
(import "java.lang.Math.*")

(define scene-render ())
(define scene-sdf ())

(define (set-scene-render s)
  (set! scene-render s))

(define (set-scene-sdf s)
  (set! scene-sdf s))

(define (set-scene-name s)
  (.setName scene-render s))


(define (add-camera-position-keyframe t x y z)
  (.setPositionKeyframe (.camera$ scene-render) t (Vector3d. x y z)))


(define (add-camera-target-keyframe t x y z)
  (.setTargetKeyframe (.camera$ scene-render) t (Vector3d. x y z)))


(define (set-sun-position x y z)
  (add-sun-keyframe 0.0 x y z))


(define (add-sun-keyframe frame x y z)
  (.addKeyframe (.sunPosition$ scene-render) frame (Vector3d. x y z)))

(define (set-sun-vector vec)
  (.set (.sunPosition$ scene-render) vec))


(define (set-far-clip val)
  (set! SDF.farClip$ val))


(define (set-frame-start frame)
  (.frameStart$ scene-render frame))


(define (set-frame-end frame)
  (.frameEnd$ scene-render frame))


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
		    `(set! ,sdf (SDFOpModulo. ,sdf ,(second op) ,(third op) ,(fourth op))))
		   ('op-smooth
		    `(set! ,sdf (SDFOpSmooth. ,sdf ,(second op) ,(third op))))
		   ('op-subtract
		    `(set! ,sdf (SDFOpSubtract. ,sdf ,(second op) ,(third op))))))
	       body))))


