package lisp;

import fonts.PointFont;
import ui.Content_View;
import ui.Window;
import ui.Content;

import static org.lwjgl.opengl.GL11.*;
public class Content_Lisp extends Content {

	Parser parser;
	
	Content_View previewWindow;
	
	public Content_Lisp(Window parent,Content_View previewWindow) {
		this.parent = parent;
		this.previewWindow = previewWindow;
		loadFile("scripts/test.pl");
		parent.windowTitle = "Lisp";
	}
	
	public void loadFile(String path) {
		parser = new Parser(path);
		previewWindow.geometry = parser.geometry;
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
