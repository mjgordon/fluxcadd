package scheme;

import geometry.GeometryDatabase;

import java.io.FileNotFoundException;

import console.Console;
import jscheme.JScheme;

/**
 * The interface with the JScheme instance itself
 */
public class SchemeEnvironment {
	public JScheme js = new JScheme();

	protected GeometryDatabase geometry;


	public SchemeEnvironment() {
		geometry = new GeometryDatabase();
		loadSystem();
	}


	public void loadSystem() {
		try {
			js.load(new java.io.FileReader("scheme/fluxcadd-system.scm"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		js.call("set-geometry", geometry);
		Console.log("Scheme System Loaded");
	}

	public void call(String s, Object o) {
		js.call(s, o);
	}
	

	/**
	 * Raw eval entry into the JScheme instance
	 * 
	 * @param s - Scheme String to be evaluated
	 */
	public void eval(String s) {
		js.eval(s);
	}


	/**
	 * Wraps the input in a (begin)
	 * 
	 * @param s - Scheme String to be evaluated
	 */
	public void evalSafe(String s) {
		s = "(begin " + s + ")";
		eval(s);
	}


	public static void print(String s) {
		System.out.println(s);
	}
}
