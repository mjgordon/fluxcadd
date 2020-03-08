package scheme;

import controller.*;
import ui.Content_View;
import ui.Panel;
import ui.Content;

public class ContentScheme extends Content implements Controllable {
	
	private ControllerManager controllerManager;
	private Controller_Toggle toggleLive;
	private Controller_Toggle toggleExternal;
	private Controller_Button buttonReload;

	private Content_View previewWindow;
	
	private SchemeEnvironment schemeEnvironment;
	
	private SourceFile sourceFile;

	/**
	 * Controls for interfacing with an exterior set of .scm files with an associated live preview
	 */
	public ContentScheme(Panel parent, Content_View previewWindow) {
		super(parent);
		this.previewWindow = previewWindow;
		parent.windowTitle = "Scheme";
		
		setupControl();
		
		schemeEnvironment = new SchemeEnvironment();
		
		previewWindow.geometry = schemeEnvironment.geometry;
		
		sourceFile = new SourceFile("scripts/test.scm");
	}
	
	@Override
	public void render() {
		controllerManager.render();
//		for (int i = 0; i < parser.source.lines.size(); i++) {
//			String line = parser.source.lines.get(i);
//			glColor3f(1, 1, 1);
//			PointFont.drawString(line, 10, parent.getHeight() - (i * 12 + 35));
//		}
	}

	private void setupControl() {
		controllerManager = new ControllerManager(this);
		toggleExternal = new Controller_Toggle(controllerManager,"toggle_external","External",20,getHeight() - 60,20,20);
		controllerManager.add(toggleExternal);
		
		toggleLive = new Controller_Toggle(controllerManager,"toggle_live","Live Update",20,getHeight() - 100,20,20);
		controllerManager.add(toggleLive);
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
	protected void textInput(char character) {}

	@Override
	public void controllerEvent(String name) {
		
	}

}
