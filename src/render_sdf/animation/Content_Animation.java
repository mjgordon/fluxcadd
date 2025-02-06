package render_sdf.animation;

import controller.UIEControlManager;
import controller.UIETimeline;
import event.EventListener;
import event.EventMessage;
import io.MouseButton;
import io.MouseCursor;
import ui.Content;
import ui.Panel;

public class Content_Animation extends Content implements EventListener {
	
	private UIEControlManager controllerManager;
	
	private UIETimeline timeline;

	public Content_Animation(Panel parent) {
		super(parent);
		
		setParentWindowTitle("Animation");
		
		setupControl();
	}

	@Override
	public void render() {
		controllerManager.render();
		
	}

	@Override
	protected void keyPressed(int key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void textInput(char character) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void mouseWheel(int mouseX, int mouseY, int wheelDY) {
		int localX = mouseX - timeline.getX();
		timeline.zoom(wheelDY, localX);
		
	}

	@Override
	protected void mousePressed(int button, int mouseX, int mouseY) {
		controllerManager.mousePressed(mouseX, mouseY);
		
	}

	@Override
	protected void mouseReleased(int button) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void mouseDragged(int button, int x, int y, int dx, int dy) {
		if (button == MouseButton.CENTER) {
			timeline.pan(dx);
		}
		else if (button == MouseButton.LEFT) {
			timeline.pick(MouseCursor.instance().getX() - this.getX(), MouseCursor.instance().getY() - this.getY());
		}
		
	}

	@Override
	public void resizeRespond(int newWidth, int newHeight) {
		controllerManager.setWidth(newWidth);
		controllerManager.setHeight(newHeight);
		controllerManager.reflow();
		
		timeline.setHeight(getHeight() - this.parent.barHeight - 30);
	}
	
	
	@Override
	public void message(EventMessage message) {
		// TODO Auto-generated method stub
		
	}
	
	private void setupControl() {
		controllerManager = new UIEControlManager(0, parent.barHeight, getWidth(), getHeight(), 10, 10, 10, 10, false);
		timeline = new UIETimeline("timeline", "Timeline", 0, 0, this.getWidth() - 20, this.getHeight() - this.parent.barHeight - 30);
		
		controllerManager.add(timeline);
		
		controllerManager.finalizeLayer();
	}
	
	public double getTime() {
		return timeline.getTime();
	}
	
	public void setAnimated(Animated[] animated) {
		timeline.setAnimated(animated);
	}

	

}
