package ui;

import java.util.ArrayList;
import java.util.Iterator;

public class WindowManager {
	public ArrayList<Window> windows;
	
	public WindowManager() {
		windows = new ArrayList<Window>();
	}
	
	public void render() {
		for (int i = 0; i < windows.size(); i++) {
			int id = windows.size() - i - 1;
			windows.get(id).render(id == 0);
		}
	}
	
	public void add(Window w) {
		windows.add(w);
	}
	
	public void addTop(Window w) {
		windows.add(0,w);
	}
	
	public Iterator<Window> getIterator() {
		return(windows.iterator());
	}
}
