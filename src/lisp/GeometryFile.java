package lisp; 

import geometry.Geometry;
import java.util.HashMap;

/**
 * This class contains all of the geometry of the current file, as generated
 * dynamically by it's accompanying source file. 
 *
 */
public class GeometryFile {
	
	/**
	 * All of the current Geometry in the scene as generated form the source
	 * file. Currently all-in-one, may eventually want to organize by type.
	 */
	public HashMap<String,Geometry> geometry;
	
	public GeometryFile() {
		geometry = new HashMap<String,Geometry>();
	}
	
	public void add(Geometry g) {
		geometry.put(g.name,g);
	}
	
	public void add(String name, Geometry g) {
		geometry.put(name,g);
	}
	
	public void render() {
		for (Geometry g : geometry.values()) {
			g.render();
		}
	}
	
	public void clear() {
		geometry.clear();
	}
	
	public void printNames() {
		for(String s : geometry.keySet()) System.out.println(s);
	}
	
}
