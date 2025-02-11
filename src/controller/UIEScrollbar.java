package controller;

import graphics.OGLWrapper;
import graphics.Primitives;
import utility.math.UtilMath;


public class UIEScrollbar extends UserInterfaceElement<UIEScrollbar> {
	/**
	 * Y distance of the handle itself from the top
	 */
	public int positionPixels;
	
	/**
	 * Position from 0.0 (top of handle at top of bar) to 1.0 (bottom of handle at bottom of bar)
	 */
	public double positionRatio;
	
	/**
	 * Position in terms of items being affected (pixels, lines, etc) 
	 */
	public int positionItems;
	
	private int barHeight;
	
	private int itemCount;
	
	private int visibleArea;
	
	protected boolean active;

	
	public UIEScrollbar(String name, String displayName, int x, int y, int width, int height, int itemCount, int visibleArea) {
		super(name, displayName, x, y, width, height);
		
		this.visibleArea = visibleArea;
		
		setItemCount(itemCount);
	}
	
	
	public void setItemCount(int itemCount) {
		if (itemCount == -1 || itemCount > this.itemCount) { 
			this.itemCount = itemCount;	
		}
		
		this.active = (itemCount >= 0);
		
		recalculateBar();
	}
	
	
	public void setVisibleArea(int visibleArea) {
		this.visibleArea = visibleArea;
		recalculateBar();
	}
	
	
	private void recalculateBar() {
		double barRatio = Math.min(1.0 * visibleArea / itemCount, 1);  
		barHeight = (int)(this.height * barRatio);
		
		recalculatePosition();
	}
	
	
	private void recalculatePosition() {
		positionPixels = UtilMath.clip(positionPixels, 0, height - barHeight);
		
		int ratioDenom = (height - barHeight);
		positionRatio = (ratioDenom) > 0 ? 1.0 * positionPixels / (height - barHeight) : 0;
		
		positionItems = (int)Math.max(positionRatio * (itemCount - visibleArea), 0);
	}
	
	
	@Override public void mouseDragged(int x, int y, int dx, int dy) {		
		if (selected) {
			positionPixels += dy;
			
			recalculatePosition();
			
			execute();	
		}
	}
	
	@Override 
	public void mouseWheel(int dy) {
		positionPixels -= dy * 10;
		recalculatePosition();
		
		execute();
	}
	
	public void setScrollPosition(int position) {
		positionPixels = position;
		recalculatePosition();
	}
	
	
	@Override
	public void render() {
		if (visible && active) {
			OGLWrapper.fill(200, 200, 200);
			OGLWrapper.stroke(0, 0, 0);

			Primitives.rect(x, y, width, height);

			OGLWrapper.fill(255, 255, 255);
			Primitives.rect(x, y + positionPixels, width, barHeight);

			super.render();	
		}
	}

}
