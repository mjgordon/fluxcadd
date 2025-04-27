package controller;

import fonts.BitmapFont;
import graphics.OGLWrapper;
import graphics.Primitives;
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
		System.out.println("YO: " + height);

		visibleFrameRange = new Domain(-10, 110);
		contentDrawRange = new Domain(leftGutter, width);
	}


	public void render() {
		// Background
		OGLWrapper.noStroke();
		OGLWrapper.fill(255, 255, 255);
		Primitives.rect(x + leftGutter, y, width - leftGutter, height);

		// Current frame bar
		int start = (int) contentDrawRange.convert(selectedFrame, visibleFrameRange);
		int end = (int) contentDrawRange.convert(selectedFrame + 1, visibleFrameRange);
		OGLWrapper.fill(200, 200, 255);
		Primitives.rect(start + x, y, (end - start), height);

		// Ticks and tick labels
		int tickPixel = 50;
		int tickFrame = 5;

		int advance = (int) (visibleFrameRange.convert(contentDrawRange.getSize() / tickPixel, contentDrawRange) - visibleFrameRange.getLower());
		advance = (int) (Math.ceil(1.0 * advance / tickFrame) * tickFrame);

		for (int i = (int)Math.ceil(visibleFrameRange.getLower()); i < (int)Math.ceil(visibleFrameRange.getUpper()); i++) {
			double lx = contentDrawRange.convert(i, visibleFrameRange);

			if (i % tickFrame != 0) {
				OGLWrapper.stroke(220, 220, 220);
			}
			else {
				OGLWrapper.stroke(100, 100, 100);

				if (i >= 0) {
					BitmapFont.drawString(i + "", (int) lx + x, y + height + 4, true);
				}
			}
			Primitives.line(lx + x, y, lx + x, y + height);
		}

		// Outline
		OGLWrapper.stroke(0, 0, 0);
		OGLWrapper.noFill();
		Primitives.rect(x + leftGutter, y, width - leftGutter, height);

		if (exposedAnimated != null) {
			for (int i = 0; i < exposedAnimated.length; i++) {
				int localY = y + 2 + (i * BitmapFont.cellHeight);
				BitmapFont.drawString(exposedAnimated[i].getName(), x, localY, true);
				Primitives.line(x, localY + BitmapFont.cellHeight, x + width, localY + BitmapFont.cellHeight);

				for (double keyframe : exposedAnimated[i].getKeyframes()) {
					int pos = (int) contentDrawRange.convert(keyframe, visibleFrameRange);
					int pos2 = (int) contentDrawRange.convert(keyframe + 1, visibleFrameRange);
					if (pos < 0 + leftGutter) {
						continue;
					}
					OGLWrapper.fill(200, 200, 200);
					Primitives.rect(pos + x + 1, localY, pos2 - pos, BitmapFont.cellHeight - 2);
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


	public double getTime() {
		return currentTime;
	}


	public void setAnimated(Animated[] input) {
		this.exposedAnimated = input;
	}

}
