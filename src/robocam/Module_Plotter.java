package robocam;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import ui.Content_View;
import ui.ViewType;
import ui.WindowContent;
import controller.Controllable;
import controller.ControllerManager;
import controller.Controller_FileChooser;

public class Module_Plotter extends Module implements Controllable {

	public Controller_FileChooser fileChooser;
	
	public ArrayList<SVGElement> svgElements;
	
	public float canvasWidth;
	public float canvasHeight;
	
	public Module_Plotter(WindowContent parent,Content_View associatedView) {
		super(parent,associatedView);
		
		associatedView.changeType(ViewType.TOP);
		
		setupControl();
		fileChooser = new Controller_FileChooser(controllerManager,"fileChooser",10,10,parent.getWidth()-20,20);
		controllerManager.add(fileChooser);
		
		parseFile();
		
	}
	
	public void parseFile() {
		Document doc = null;
		
		try{
			File xmlFile = new File("scripts/test.svg");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			
		}  catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (doc == null) return;
		
		svgElements = new ArrayList<SVGElement>();
		
		Element topElement = doc.getDocumentElement();
		String cw = topElement.getAttribute("width");
		canvasWidth = Float.valueOf(cw.substring(0, cw.length() - 2));
		String ch = topElement.getAttribute("height");
		canvasHeight = Float.valueOf(ch.substring(0, cw.length() - 2));
		NodeList nl = doc.getDocumentElement().getChildNodes();

		for (int i = 0; i< nl.getLength(); i++) {
			Node n = nl.item(i);
			if (n.getNodeType() != Node.ELEMENT_NODE) continue;
			String name = (nl.item(i).getNodeName());
			System.out.println(name);
			if (name.equals("rect")) {
				SVGRectangle rect = new SVGRectangle((Element) n);
				svgElements.add(rect);
			}
		}
	}
	
	@Override
	public void controllerEvent(String name) {
		if (name.equals("fileChooser"));
		
	}
	
	@Override
	public void setupControl() {
		controllerManager = new ControllerManager(this);
		
	}
	
	// Everything below this is pretty much cruft, and wouldn't exist with a better program design... we'll see
	
	@Override
	public int getX() {
		return(parent.getX());
	}

	@Override
	public int getY() {
		return(parent.getY());
	}

	@Override
	public int getWidth() {
		return(parent.getWidth());
	}

	@Override
	public int getHeight() {
		return(parent.getHeight());
	}
	
	abstract class SVGElement {
		abstract void render();
		abstract void export();
	}
	
	class SVGRectangle extends SVGElement {

		float x;
		float y;
		float width;
		float height;
		
		public SVGRectangle(Element e) {
			x = Float.valueOf(e.getAttribute("x"));
			y = Float.valueOf(e.getAttribute("y"));
			width = Float.valueOf(e.getAttribute("width"));
			height = Float.valueOf(e.getAttribute("height"));
		}
		
		@Override
		void render() {
			// TODO Auto-generated method stub
			
		}

		@Override
		void export() {
			// TODO Auto-generated method stub
			
		}
		
	}
}