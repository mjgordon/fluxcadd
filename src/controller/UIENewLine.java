package controller;

/**
 * Dummy class, used as a placeholder for when the layout is reflowed as if
 * calling the newLine function
 */
public class UIENewLine extends UserInterfaceElement<UIENewLine> {
	
	protected boolean explicit;
	
	public UIENewLine(boolean explicit) {
		super("", "", 0, 0, 0, 0);
		this.explicit = explicit;
	}
}
