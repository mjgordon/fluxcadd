package iofile;

import org.joml.Vector3d;

import geometry.Mesh;
import geometry.Mesh.Polygon;

public class MeshOBJ {
	public static Mesh loadMeshFromFile(String path) {
		Mesh output = new Mesh();

		String[] file;
		try {
			file = Plaintext.loadPlaintext(path);	
		}
		catch (java.io.IOException e) {
			return null;
		}
		

		for (String s : file) {
			String[] parts = s.split(" ");
			if (parts[0].equals("v")) {
				double x = Double.valueOf(parts[1]);
				double y = Double.valueOf(parts[2]);
				double z = Double.valueOf(parts[3]);
				Vector3d vertex = new Vector3d(x, -z, y);
				output.vertices.add(vertex);
			}
			else if (parts[0].equals("vn")) {
				double x = Double.valueOf(parts[1]);
				double y = Double.valueOf(parts[2]);
				double z = Double.valueOf(parts[3]);
				Vector3d vertexNormal = new Vector3d(x, y, z);
				output.vertexNormals.add(vertexNormal);
			}
			else if (parts[0].equals("f")) {
				Polygon polygon = output.new Polygon();
				for (int i = 1; i < parts.length; i++) {
					if (parts[i].indexOf("/") != -1) {
						String[] polygonParts = parts[i].split("/");
						polygon.vertexIds.add(Integer.valueOf(polygonParts[0]) - 1);
						polygon.vertexNormalIds.add(Integer.valueOf(polygonParts[2]) - 1);
					}
					else {
						polygon.vertexIds.add(Integer.valueOf(parts[i]) - 1);
					}
				}
				output.polygons.add(polygon);
			}
		}

		output.recalculateExplicitGeometry();

		return (output);
	}
}
