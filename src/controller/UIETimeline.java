package controller;

import event.EventListener;
import fonts.BitmapFont;
import graphics.OGLWrapper;
import graphics.Primitives;
import utility.math.Domain;

public class UIETimeline extends UserInterfaceElement<UIETimeline> {
	
	
	
	private Domain visibleRange;
	private Domain drawRange;

	public UIETimeline(EventListener target, String name, String displayName, int x, int y, int width, int height) {
		super(target, name, displayName, x, y, width, height);
		
		visibleRange = new Domain(-10,110);
		drawRange = new Domain(0,width);
	}
	
	public void render() {
		OGLWrapper.noStroke();
		OGLWrapper.fill(255, 255, 255);
		
		Primitives.rect(x, y, width, height);
		
		int tickPixel= 50;
		int tickFrame = 5;
		
		int advance = (int) (visibleRange.convert(drawRange.getSize()  / tickPixel, drawRange) - visibleRange.getLower());
		advance = (int) (Math.ceil(1.0 * advance / tickFrame) * tickFrame);
		
		for (int i = (int)visibleRange.getLower(); i < (int) visibleRange.getUpper(); i++) {
			double lx = drawRange.convert(i, visibleRange);
			
			if (i % tickFrame != 0) {
				OGLWrapper.stroke(220, 220, 220);
			}
			else {
				OGLWrapper.stroke(100, 100, 100);
				
				if (i >= 0) {
					BitmapFont.drawString(i + "", (int)lx + x, y + height + 4, null);	
				}	
			}
			
			Primitives.line(lx + x, y, lx + x, y + height);
			
			
		}
		
		OGLWrapper.stroke(0, 0, 0);
		OGLWrapper.noFill();

		Primitives.rect(x, y, width, height);
		
		

		super.render();
	}
	
	@Override
	public UIETimeline pick(int mouseX, int mouseY) {

		if (super.pick(mouseX, mouseY) == this) {
			mouseX -= this.x;
			
			return (this);
		}
		return (null);
	}
	
	public void pan(int dx) {
		
		double dxReal = dx / drawRange.getSize() * visibleRange.getSize();
		
		visibleRange.setLower(visibleRange.getLower() - dxReal);
		visibleRange.setUpper(visibleRange.getUpper() - dxReal);
	}
	
	
	public void zoom(double amt, int cursorX) {
		//TODO: scale amt by current width
		double anchor = visibleRange.convert(cursorX, drawRange);
		
		double ratio = visibleRange.getNormalize(anchor);
		double ratioI = 1 - ratio;
		
		visibleRange.setLower(visibleRange.getLower() + (amt * ratio));
		visibleRange.setUpper(visibleRange.getUpper() -(amt * ratioI));
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
	public void setWidth(int width) {
		super.setWidth(width);
		drawRange = new Domain(0,width);
	}

}
