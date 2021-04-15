;;; Adding optional argument functionality

;;; Doesn't seem to work yet
      
(define-syntax nil!
  (syntax-rules ()
    ((_ x)
     (set! x '()))))
		
(define-syntax defkeywords
  (syntax-rules ()
    ((_ nargs kwargs body)
     (with-environment env
		       (eval (macro-expand
			      `(define
				 ,(reduce (flip cons)
					  (cons 'args)
					  (reverse 'nargs))
				 (begin
				   (with-environment env
						     (let ((dict (apply make-hash args)))
						       (map
							($ (let* ((k (car %))
								  (s (atom->symbol k)))
							     (eval `(define ,s
								      ,(if (in? dict k)
									   (dict k)
									   (eval (get-from % 1)))) env)))
							(quote ,(treat-keywords 'kwargs)))))

				   ,body))))))))



(define (treat-keywords args)
  (case (length args)
    ((0) [])
    ((1) `((,(car args) nil)))
    (else
      (let ((key  (car args))
            (meta (cadr args)))
        (if (eq? :default meta)
          (++ `((,key ,(caddr args)))
              (treat-keywords (cdddr args)))
          (++ `((,key ,nil))
              (treat-keywords (cdr args))))))))
			       
