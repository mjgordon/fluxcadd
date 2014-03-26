package geometry;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import utility.PVector;

import static org.lwjgl.opengl.GL11.*;


public class OBJModel extends Geometry {
	public ArrayList<String> file;
	public ArrayList<PVector> vertexes;
	public ArrayList<PVector> vertexNormals;
	public ArrayList<Polygon> polygons;
	
	public int visiblity;
	public static final int VISIBLE = 0;
	public static final int GHOSTED = 1;
	public static final int INVISIBLE = 2;
	
	
	public OBJModel(String name) {
		vertexes = new ArrayList<PVector>();
		vertexNormals = new ArrayList<PVector>();
		polygons = new ArrayList<Polygon>();
		
		String path = "objs/" + name;
		
		file = new ArrayList<String>();
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(path));
	        String line = br.readLine();  
	        while (line != null) {
	        	file.add(line);
	            line = br.readLine();
	        }
	        br.close();
		} catch(Exception e) {
			System.out.println(e);
		}
		
		for (String s :  file) {
			String[] parts = s.split(" ");
			if (parts[0].equals("v")) {
				float x = Float.valueOf(parts[1]);
				float y = Float.valueOf(parts[2]);
				float z = Float.valueOf(parts[3]);
				PVector vertex = new PVector(x,y,z);
				vertexes.add(vertex);
			}
			else if (parts[0].equals("vn")) {
				float x = Float.valueOf(parts[1]);
				float y = Float.valueOf(parts[2]);
				float z = Float.valueOf(parts[3]);
				PVector vertexNormal = new PVector(x,y,z);
				vertexNormals.add(vertexNormal);
			}
			else if (parts[0].equals("f")) {
				Polygon polygon = new Polygon();
				for (int i = 1; i < parts.length; i++) {
					if (parts[i].indexOf("/") != -1) {
						String[] polygonParts = parts[i].split("/");
						polygon.vertexIds.add(Integer.valueOf(polygonParts[0])-1);
						polygon.vertexNormalIds.add(Integer.valueOf(polygonParts[2])-1);
					}
					else {
						polygon.vertexIds.add(Integer.valueOf(parts[i])-1);
					}
				}
				polygons.add(polygon);
			}
		}
	}
	
	public void render() {
		if (visiblity == INVISIBLE) return;
		
		glEnable(GL_POLYGON_OFFSET_FILL);
		glPolygonOffset(1,1);
		glEnable(GL_DEPTH_TEST);
		for (Polygon polygon : polygons) {
			glPushMatrix();
			
			glColor4f(r,g,b,(visiblity == VISIBLE) ? 1 : 0.5f);
			glBegin(GL_POLYGON);
			for (int i = 0; i < polygon.vertexIds.size(); i++) {
				float vx = vertexes.get(polygon.vertexIds.get(i)).x;
				float vy = vertexes.get(polygon.vertexIds.get(i)).y;
				float vz = vertexes.get(polygon.vertexIds.get(i)).z;
				
				if (polygon.vertexNormalIds.size() > 0) {
					float nx = vertexes.get(polygon.vertexNormalIds.get(i)).x;
					float ny = vertexes.get(polygon.vertexNormalIds.get(i)).y;
					float nz = vertexes.get(polygon.vertexNormalIds.get(i)).z;
					glNormal3f(nx,ny,nz);
				}
				
				glVertex3f(vx,vy,vz);
			}
			glEnd();
			
			glColor3f(0.5f,0.5f,0.5f);
			glBegin(GL_LINE_LOOP);
			for (int i = 0; i < polygon.vertexIds.size(); i++) {
				float vx = vertexes.get(polygon.vertexIds.get(i)).x;
				float vy = vertexes.get(polygon.vertexIds.get(i)).y;
				float vz = vertexes.get(polygon.vertexIds.get(i)).z;
				
				if(polygon.vertexNormalIds.size() > 0) {
					float nx = vertexes.get(polygon.vertexNormalIds.get(i)).x;
					float ny = vertexes.get(polygon.vertexNormalIds.get(i)).y;
					float nz = vertexes.get(polygon.vertexNormalIds.get(i)).z;
					glNormal3f(nx,ny,nz);
				}
				
				glVertex3f(vx,vy,vz);
			}
			glEnd();
			
			glPopMatrix();
		}
		glDisable(GL_LIGHTING);
	}
	
	public class Polygon {
		public ArrayList<Integer> vertexIds = new ArrayList<Integer>();
		public ArrayList<Integer> vertexNormalIds = new ArrayList<Integer>();
		
		public ArrayList<Line> getLines() {
			ArrayList<Line> out = new ArrayList<Line>();
			out.add(new Line(vertexes.get(vertexIds.get(0)),vertexes.get(vertexIds.get(1))));
			out.add(new Line(vertexes.get(vertexIds.get(1)),vertexes.get(vertexIds.get(2))));
			if (vertexIds.size() == 3) {
				out.add(new Line(vertexes.get(vertexIds.get(2)),vertexes.get(vertexIds.get(0))));
			}
			else {
				out.add(new Line(vertexes.get(vertexIds.get(2)),vertexes.get(vertexIds.get(3))));
				out.add(new Line(vertexes.get(vertexIds.get(3)),vertexes.get(vertexIds.get(0))));
			}
			return(out);
		}
	}
}
