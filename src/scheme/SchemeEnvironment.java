package scheme;

import geometry.GeometryFile;

import java.io.FileNotFoundException;

import jscheme.JScheme;

/**
 * The interface with the JScheme instance itself
 */
public class SchemeEnvironment {
	JScheme js = new JScheme();
	
	GeometryFile geometry;
	
	public SchemeEnvironment() {
		try {
			js.load(new java.io.FileReader("scheme/fluxcadd-system.scm"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		geometry = new GeometryFile();
		
		js.call("set-geometry", geometry);
		System.out.println(js.call("point",10.0f,10.0f,10.0f));
	}
}
