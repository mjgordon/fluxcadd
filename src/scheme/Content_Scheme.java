package scheme;

import controller.*;
import ui.Content_View;
import ui.Panel;
import ui.Content;

public class Content_Scheme extends Content {

	private UIEControlManager controllerManager;
	private UIEToggle toggleLive;
	private UIEToggle toggleExternal;
	private UIEButton buttonReloadSystem;
	private UIEButton buttonReloadTest;
	private UIETextField geometryList;
	private UIETerminal repl;

	private Content_View previewWindow;

	private SchemeEnvironment schemeEnvironment;

	private SourceFile sourceFile;


	/**
	 * Controls for interfacing with an exterior set of .scm files with an
	 * associated live preview
	 */
	public Content_Scheme(Panel parent, Content_View previewWindow) {
		super(parent);
		this.previewWindow = previewWindow;
		parent.windowTitle = "Scheme";

		setupControl();

		schemeEnvironment = new SchemeEnvironment();

		previewWindow.geometry = schemeEnvironment.geometry;

		try {
			sourceFile = new SourceFile("scripts/test.scm");
			schemeEnvironment.evalSafe(sourceFile.fullFile);
		} catch (Exception e) {
			System.out.println(e);
		}

	}


	@Override
	public void render() {
		controllerManager.render();
	}


	@Override
	protected void mouseWheel(float amt) {
	}


	@Override
	protected void mousePressed(int button, int mouseX, int mouseY) {
		if (button == 0) {
			controllerManager.poll(mouseX, mouseY);
		}
	}


	@Override
	protected void mouseDragged(int button, int dx, int dy) {
	}


	@Override
	protected void keyPressed(int key) {
		controllerManager.keyPressed(key);
	}


	@Override
	protected void textInput(char character) {
		controllerManager.textInput(character);
	}


	@Override
	public void resizeRespond() {
		controllerManager.reflow();
	}


	private void setupControl() {
		controllerManager = new UIEControlManager(getWidth(), getHeight(), 10, 30, 10, 10);

		toggleExternal = new UIEToggle("toggle_external", "External", 0, 0, 20, 20);
		controllerManager.add(toggleExternal);

		toggleLive = new UIEToggle("toggle_live", "Live Update", 0, 0, 20, 20);
		controllerManager.add(toggleLive);

		buttonReloadSystem = new UIEButton("button_reload_system", "Reload System", 0, 0, 20, 20).setCallback((button) -> {
			schemeEnvironment.loadSystem();
		});
		controllerManager.add(buttonReloadSystem);

		buttonReloadTest = new UIEButton("button_reload_test", "Reload Test", 0, 0, 20, 20).setCallback((button) -> {
			schemeEnvironment.geometry.clear();
			sourceFile.reload();
			schemeEnvironment.evalSafe(sourceFile.fullFile);
		});
		controllerManager.add(buttonReloadTest);

		controllerManager.newLine();

		geometryList = new UIETextField("geometry_list", "Geometry List", 0, 0, getWidth() - 30, 200);
		geometryList.setValueSilent("abcdefghijklmnopqrs\ntuvwxyz0123456789.,/_-()");
		controllerManager.add(geometryList);

		repl = new UIETerminal("terminal_repl", "Scheme REPL", 0, 0, getWidth() - 30, 500);
		controllerManager.add(repl);

		controllerManager.finalizeLayer();
	}



	@Override
	protected void mouseReleased(int button) {
		// TODO Auto-generated method stub
	}
}
