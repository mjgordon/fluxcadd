package iofile;

import java.util.ArrayList;

import org.joml.Vector2d;
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
		
		ArrayList<Vector3d> vertices = new ArrayList<Vector3d>();
		ArrayList<Vector2d> verticesUV = new ArrayList<Vector2d>();
		ArrayList<Vector3d> verticesNormal = new ArrayList<Vector3d>();
		

		for (String s : file) {
			String[] parts = s.split(" ");
			if (parts[0].equals("v")) {
				double x = Double.valueOf(parts[1]);
				double y = Double.valueOf(parts[2]);
				double z = Double.valueOf(parts[3]);
				Vector3d vertex = new Vector3d(x, -z, y);
				vertices.add(vertex);
			}
			else if (parts[0].equals("vt")) {
				double u = Double.valueOf(parts[1]);
				double v = 1 - Double.valueOf(parts[2]);
				
				Vector2d vertexTexture = new Vector2d(u, v);
				verticesUV.add(vertexTexture);
			}
			else if (parts[0].equals("vn")) {
				double x = Double.valueOf(parts[1]);
				double y = Double.valueOf(parts[2]);
				double z = Double.valueOf(parts[3]);
				Vector3d vertexNormal = new Vector3d(x, y, z);
				verticesNormal.add(vertexNormal);
			}
			else if (parts[0].equals("f")) {
				Polygon polygon = output.new Polygon();
				
				int vertexCount = parts.length - 1;
				polygon.vertexIds = new int[vertexCount];
				
				if (s.indexOf("/") != -1) {
					polygon.vertexTextureIds = new int[vertexCount];
					polygon.vertexNormalIds = new int[vertexCount];	
				}
				
				for (int i = 1; i < parts.length; i++) {
					
					if (parts[i].indexOf("/") != -1) {
						String[] polygonParts = parts[i].split("/");
						polygon.vertexIds[i-1] = Integer.valueOf(polygonParts[0]) - 1;
						polygon.vertexTextureIds[i-1] = Integer.valueOf(polygonParts[1]) - 1;
						polygon.vertexNormalIds[i-1] = Integer.valueOf(polygonParts[2]) - 1;
					}
					else {
						polygon.vertexIds[i-1] = Integer.valueOf(parts[i]) - 1;
					}
				}
				output.polygons.add(polygon);
			}
		}
		
		output.vertices = vertices.toArray(new Vector3d[vertices.size()]);
		output.vertexTextures = verticesUV.toArray(new Vector2d[verticesUV.size()]);
		output.vertexNormals = verticesNormal.toArray(new Vector3d[verticesNormal.size()]);

		output.recalculateExplicitGeometry();

		return (output);
	}
}
