package lisp;

import geometry.GeometryFile;

import java.io.FileNotFoundException;

import jscheme.JScheme;

public class LispEnvironment {
	JScheme js = new JScheme();
	
	GeometryFile geometry;
	
	public LispEnvironment() {
		try {
			js.load(new java.io.FileReader("lisp/fluxcadd-system.scm"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		geometry = new GeometryFile();
		
		js.call("set-geometry", geometry);
		System.out.println(js.call("point",10.0f,10.0f,10.0f));
	}
}
