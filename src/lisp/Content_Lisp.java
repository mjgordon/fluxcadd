package lisp;

import fonts.PointFont;
import ui.Content_View;
import ui.Panel;
import ui.Content;
import static org.lwjgl.opengl.GL11.*;

public class Content_Lisp extends Content {

	private Parser parser;

	private Content_View previewWindow;

	public Content_Lisp(Panel parent, Content_View previewWindow) {
		super(parent);
		this.previewWindow = previewWindow;
		loadFile("scripts/test.pl");
		parent.windowTitle = "Lisp";
	}
	
	@Override
	public void render() {
		for (int i = 0; i < parser.source.lines.size(); i++) {
			String line = parser.source.lines.get(i);
			glColor3f(1, 1, 1);
			PointFont.drawString(line, 10, parent.getHeight() - (i * 12 + 35));
		}
	}

	private void loadFile(String path) {
		parser = new Parser(path);
		previewWindow.geometry = parser.geometry;
	}

	@Override
	protected void mouseWheel(float amt) {}

	@Override
	protected void mousePressed(int button,int mouseX, int mouseY) {}

	@Override
	protected void mouseDragged(int dx, int dy) {}

	@Override
	protected void keyPressed(int key) {}
	
	@Override 
	protected void textInput(int codepoint) {}

}
