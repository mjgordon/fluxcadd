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

(define color-string
  (constructor "Color" "String"))
  
  
	       

