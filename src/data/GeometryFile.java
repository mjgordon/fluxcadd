package data; 

import geometry.Geometry;
import java.util.ArrayList;

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
	public ArrayList<Geometry> geometry;
	
	public GeometryFile() {
		geometry = new ArrayList<Geometry>();
	}
	
	public void add(Geometry g) {
		geometry.add(g);
	}
	
	public void render() {
		for (Geometry g : geometry) {
			g.render();
		}
	}
	
}
