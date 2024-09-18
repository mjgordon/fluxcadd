package geometry;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import java.util.UUID;

import org.joml.Vector3d;

/**
 * A set of geometry data, with methods for sorting / searching
 */
public class GeometryDatabase {

	/**
	 * Geometry, stored by GUID long
	 */
	public HashMap<UUID, Geometry> geometry;


	public GeometryDatabase() {
		geometry = new HashMap<UUID, Geometry>();
		Tag.initTags();
	}


	public void add(Geometry g) {
		UUID uuid = UUID.randomUUID();
		g.guid = uuid;
		geometry.put(uuid, g);
	}
	
	public void remove(Geometry g) {
		if (geometry.containsValue(g)) {
			geometry.values().remove(g);
		}
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
		while (it.hasNext()) {
			Map.Entry<UUID, Geometry> pair = (Map.Entry<UUID, Geometry>) it.next();
			if (pair.getValue().name.equals(name)) {
				geometry.put(pair.getKey(), newGeom);
				return;
			}
			it.remove();
		}
	}


	public void render(double time) {
		for (Geometry g : geometry.values()) {
			g.render(time);
		}
	}


	public void clear() {
		geometry.clear();
	}


	public void printNames() {
		for (Geometry g : geometry.values())
			System.out.println(g.name);
	}


	public Iterable<Geometry> getIterable() {
		return (geometry.values());
	}


	public Vector3d getCentroid(double time) {
		Vector3d centroid = new Vector3d(0, 0, 0);
		for (Geometry g : geometry.values()) {
			centroid.add(g.frame.get(time).getColumn(3, new Vector3d()));
		}
		centroid.div(geometry.values().size());

		return (centroid);
	}

}
