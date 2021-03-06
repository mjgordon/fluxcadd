package geometry; 

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import utility.PVector;

import java.util.UUID;


/**
 * A set of geometry data, with methods for sorting / searching
 */
public class GeometryDatabase {
	
	/**
	 * Geometry, stored by GUID long
	 */
	public HashMap<UUID,Geometry> geometry;
	
	public GeometryDatabase() {
		geometry = new HashMap<UUID,Geometry>();
		Tag.initTags();
	}
	
	public void add(Geometry g) {
		UUID uuid = UUID.randomUUID();
		g.guid = uuid;
		geometry.put(uuid,g);
	}
	
	public Geometry get(String name) {
		for (Geometry g : geometry.values()) {
			if (g.name != null && g.name.equals(name)) {
				return g;
			}
		}
		return null;
	}
	
	public void replace(String name, Geometry newGeom) {
		Iterator<Entry<UUID, Geometry>> it = geometry.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<UUID,Geometry> pair = (Map.Entry<UUID,Geometry>)it.next();
			if (pair.getValue().name.equals(name)) {
				geometry.put(pair.getKey(),newGeom);
				return;
			}
			it.remove();
		}
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
		for(Geometry g : geometry.values()) System.out.println(g.name);
	}
	
	public Iterable<Geometry> getIterable() {
		return(geometry.values());
	}
	
	public PVector getCentroid() {
		PVector centroid = new PVector(0,0,0);
		for (Geometry g : geometry.values()) {
			centroid.add(g.getPositionVector());
		}
		centroid.div(geometry.values().size());
		
		return(centroid);
	}
	
}
