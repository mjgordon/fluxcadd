package lisp;

import fonts.PointFont;
import ui.Window;
import ui.WindowContent;

import static org.lwjgl.opengl.GL11.*;
public class Content_Lisp extends WindowContent {

	Parser parser;
	GeometryFile geometry;
	
	public Content_Lisp(Window parent) {
		this.parent = parent;
		loadFile("scripts/test.pl");
		parent.windowTitle = "Lisp";
	}
	
	public void loadFile(String path) {
		parser = new Parser(path);
		geometry = parser.geometry;
	}

	@Override
	public void render() {
		for (int i = 0; i <  parser.source.lines.size(); i++) {
			String line = parser.source.lines.get(i);
			glColor3f(1,1,1);
			PointFont.drawString(line, 10, parent.getHeight() - (i * 12 + 35));
		}
		
	}
	
	@Override
	public void keyPressed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseWheel(float amt) {
		// TODO Auto-generated method stub
		
	}
	
	

	@Override
	public void mousePressed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged() {
		// TODO Auto-generated method stub
		
	}

}
