package scheme;

import geometry.GeometryDatabase;

import java.io.FileNotFoundException;

import jscheme.JScheme;

/**
 * The interface with the JScheme instance itself
 */
public class SchemeEnvironment {
	private JScheme js = new JScheme();
	
	protected GeometryDatabase geometry;
	
	public SchemeEnvironment() {
		try {
			js.load(new java.io.FileReader("scheme/fluxcadd-system.scm"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		geometry = new GeometryDatabase();
		
		js.call("set-geometry", geometry);
//		System.out.println(js.call("point",10.0f,10.0f,10.0f));
	}
	
	public void eval(String s) {
		js.eval(s);
	}
}
