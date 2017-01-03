package ui;


import java.util.ArrayList;
import java.util.Iterator;

public class PanelManager {
	public ArrayList<Panel> panels;
	
	public PanelManager() {
		panels = new ArrayList<Panel>();
	}
	
	public void render() {
		for (int i = 0; i < panels.size(); i++) {
			int id = panels.size() - i - 1;
			panels.get(id).render(id == 0);
		}
	}
	
	public void add(Panel w) {
		panels.add(w);
	}
	
	public void addTop(Panel w) {
		panels.add(0,w);
	}
	
	public Iterator<Panel> getIterator() {
		return(panels.iterator());
	}

}
