package scheme;
import controller.*;
import ui.Content_View;
import ui.Panel;
import ui.Content;

public class Content_Scheme extends Content implements Controllable {

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

	private void setupControl() {
		controllerManager = new UIEControlManager(getWidth(), getHeight(),10,30,10,10);

		toggleExternal = new UIEToggle(this, "toggle_external", "External", 0, 0, 20, 20);
		controllerManager.add(toggleExternal);

		toggleLive = new UIEToggle(this, "toggle_live", "Live Update", 0, 0, 20, 20);
		controllerManager.add(toggleLive);
		
		buttonReloadSystem = new UIEButton(this, "button_reload_system", "Reload System", 0, 0, 20, 20);
		controllerManager.add(buttonReloadSystem);

		buttonReloadTest = new UIEButton(this, "button_reload_test", "Reload Test", 0, 0, 20, 20);
		controllerManager.add(buttonReloadTest);

		controllerManager.newLine();

		geometryList = new UIETextField(this, "geometry_list", "Geometry List", 0, 0, getWidth() - 30, 200);
		geometryList.currentString = "abcdefghijklmnopqrs\ntuvwxyz0123456789.,/_-()";
		controllerManager.add(geometryList);

		repl = new UIETerminal(this, "terminal_repl", "Scheme REPL", 0, 0, getWidth() - 30, 500);
		controllerManager.add(repl);
		
		controllerManager.finalize();
		
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
	protected void mouseDragged(int dx, int dy) {
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
	public void controllerEvent(UserInterfaceElement<? extends UserInterfaceElement<?>> controller) {
		switch (controller.getName()) {
		case "button_reload_system":
			schemeEnvironment.loadSystem();
			break;
		case "button_reload_test":
			schemeEnvironment.geometry.clear();
			sourceFile.reload();
			schemeEnvironment.evalSafe(sourceFile.fullFile);
			break;
		}
	}

}
