package controller;

import graphics.Graphics2D;
import render_sdf.animation.Animated;
import utility.math.Domain;

public class UIETimeline extends UserInterfaceElement<UIETimeline> {
	private Domain visibleFrameRange;
	private Domain contentDrawRange;

	private double currentTime;

	private int selectedFrame = 0;

	Animated[] exposedAnimated = null;

	int leftGutter = 80;


	public UIETimeline(String name, String displayName, int x, int y, int width, int height) {
		super(name, displayName, x, y, width, height);

		visibleFrameRange = new Domain(-10, 110);
		contentDrawRange = new Domain(leftGutter, width);
	}


	@Override
	public void render() {
		// Background
		Graphics2D.noStroke();
		Graphics2D.fill(255, 255, 255);
		Graphics2D.rect(x + leftGutter, y, width - leftGutter, height);

		// Current frame bar
		int start = (int) contentDrawRange.convert(selectedFrame, visibleFrameRange);
		int end = (int) contentDrawRange.convert(selectedFrame + 1, visibleFrameRange);
		Graphics2D.fill(200, 200, 255);
		Graphics2D.rect(start + x, y, (end - start), height);

		// Ticks and tick labels
		int tickPixel = 50;
		int tickFrame = 5;

		int advance = (int) (visibleFrameRange.convert(contentDrawRange.getSize() / tickPixel, contentDrawRange) - visibleFrameRange.getLower());
		advance = (int) (Math.ceil(1.0 * advance / tickFrame) * tickFrame);

		for (int i = (int)Math.ceil(visibleFrameRange.getLower()); i < (int)Math.ceil(visibleFrameRange.getUpper()); i++) {
			double lx = contentDrawRange.convert(i, visibleFrameRange);

			if (i % tickFrame != 0) {
				Graphics2D.stroke(220, 220, 220);
			}
			else {
				Graphics2D.stroke(100, 100, 100);

				if (i >= 0) {
					Graphics2D.text((int)lx + x, y + height + 4, i + "", true);
				}
			}
			Graphics2D.line(lx + x, y, lx + x, y + height);
		}

		// Outline
		Graphics2D.stroke(0, 0, 0);
		Graphics2D.noFill();
		Graphics2D.rect(x + leftGutter, y, width - leftGutter, height);

		if (exposedAnimated != null) {
			for (int i = 0; i < exposedAnimated.length; i++) {
				int localY = y + 2 + (i * Graphics2D.textCellHeight);
				Graphics2D.text(x, localY, exposedAnimated[i].getName(), true);
				Graphics2D.line(x, localY + Graphics2D.textCellHeight, x + width, localY + Graphics2D.textCellHeight);

				for (double keyframe : exposedAnimated[i].getKeyframes()) {
					int pos = (int) contentDrawRange.convert(keyframe, visibleFrameRange);
					int pos2 = (int) contentDrawRange.convert(keyframe + 1, visibleFrameRange);
					if (pos < 0 + leftGutter) {
						continue;
					}
					Graphics2D.fill(200, 200, 200);
					Graphics2D.rect(pos + x + 1, localY, pos2 - pos, Graphics2D.textCellHeight - 2);
				}
			}
		}

		super.render();
	}


	@Override
	public UIETimeline pick(int mouseX, int mouseY) {

		if (mouseX > leftGutter + 10 && super.pick(mouseX, mouseY) == this) {
			mouseX -= this.x;

			selectedFrame = (int) Math.floor(visibleFrameRange.convert(mouseX, contentDrawRange));
			currentTime = selectedFrame;

			return (this);
		}
		return (null);
	}


	public void pan(int dx) {

		double dxReal = dx / contentDrawRange.getSize() * visibleFrameRange.getSize();

		visibleFrameRange.setLower(visibleFrameRange.getLower() - dxReal);
		visibleFrameRange.setUpper(visibleFrameRange.getUpper() - dxReal);
	}


	public void zoom(double amt, int cursorX) {
		// TODO: scale amt by current width
		double anchor = visibleFrameRange.convert(cursorX, contentDrawRange);

		double ratio = visibleFrameRange.getNormalize(anchor);
		double ratioI = 1 - ratio;

		visibleFrameRange.setLower(visibleFrameRange.getLower() + (amt * ratio));
		visibleFrameRange.setUpper(visibleFrameRange.getUpper() - (amt * ratioI));
	}


	@Override
	public void setWidth(int width) {
		super.setWidth(width);
		contentDrawRange = new Domain(0, width);
	}
	
	
	public void setTime(double time) {
		this.currentTime = time;
		this.selectedFrame = (int) time;
	}


	public double getTime() {
		return currentTime;
	}


	public void setAnimated(Animated[] input) {
		this.exposedAnimated = input;
	}

}
