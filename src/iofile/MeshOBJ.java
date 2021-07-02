package iofile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import geometry.Mesh;
import geometry.Mesh.Polygon;
import utility.PVector;

public class MeshOBJ {
	public static Mesh loadMeshFromFile(String path) {
		ArrayList<String> file = new ArrayList<String>();
		
		Mesh output = new Mesh();
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(path));
			String line = br.readLine();
			while (line != null) {
				file.add(line);
				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			System.out.println(e);
		}

		for (String s : file) {
			String[] parts = s.split(" ");
			if (parts[0].equals("v")) {
				float x = Float.valueOf(parts[1]);
				float y = Float.valueOf(parts[2]);
				float z = Float.valueOf(parts[3]);
				PVector vertex = new PVector(x, -z, y);
				output.vertices.add(vertex);
			}
			else if (parts[0].equals("vn")) {
				float x = Float.valueOf(parts[1]);
				float y = Float.valueOf(parts[2]);
				float z = Float.valueOf(parts[3]);
				PVector vertexNormal = new PVector(x, y, z);
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
		
		return(output);
	}
}
