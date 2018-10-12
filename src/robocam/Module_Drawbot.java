package robocam;




import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import controller.Controllable;
import controller.ControllerManager;
import controller.Controller_Button;
import controller.Controller_CheckBox;
import controller.Controller_FileChooser;
import controller.Controller_TextField;
import geometry.Shape;
import io.Serial;
import svg.SVGElement;
import svg.SVGEllipse;
import svg.SVGLine;
import svg.SVGPath;
import svg.SVGPolyLine;
import svg.SVGRect;
import ui.Content_View;
import ui.ViewType;
import ui.Content;
import utility.MutableFloat;
import utility.PVector;

// 11 in  = 279.4 mm 8.5 in = 215.9mm

public class Module_Drawbot extends Module implements Controllable {
	
	public static MutableFloat minimalLineDistance = new MutableFloat(1);
	private static MutableFloat hatchOffset = new MutableFloat(3);

	private Controller_FileChooser fileChooser;
	private Controller_CheckBox checkBox;
	private Controller_TextField outputName;
	
	private ArrayList<SVGElement> svgElements;
	
	public static float canvasWidth;
	public static float canvasHeight;
	
	private String fileName = "svgs/mona_lisa_minimal.svg";
	
	Serial serial;
	
	private boolean streaming = false;
	
	public Module_Drawbot(Content parent,Content_View associatedView) {
		super(parent,associatedView);
		
		associatedView.changeType(ViewType.TOP);
		associatedView.flipped = true;
		
		setupControl();	
		
		parseFile();
		
		serial = new Serial("COM4");
	}
	
	public void parseFile() {
		activate();
		
		Document doc = null;
		try{
			File xmlFile = new File(fileName);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			
		}  catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (doc == null) return;
		
		svgElements = new ArrayList<SVGElement>();
		
		Element topElement = doc.getDocumentElement();
		String cw = topElement.getAttribute("width");
		canvasWidth = Float.valueOf(cw.substring(0, cw.length() - 2));
		String ch = topElement.getAttribute("height");
		canvasHeight = Float.valueOf(ch.substring(0, ch.length() - 2));
		
		ArrayList<PVector> corners= new ArrayList<PVector>();
		corners.add(new PVector(0,0));
		corners.add(new PVector(canvasWidth,0));
		corners.add(new PVector(canvasWidth,canvasHeight));
		corners.add(new PVector(0,canvasHeight));
		
		geometry.add(new Shape(corners,1,1,1));
		
		NodeList nl = doc.getDocumentElement().getChildNodes();

		for (int i = 0; i< nl.getLength(); i++) {
			Node n = nl.item(i);
			if (n.getNodeType() != Node.ELEMENT_NODE) continue;
			String name = (nl.item(i).getNodeName());
			
			if (name.equals("rect")) {
				SVGRect rect = new SVGRect((Element) n);
				svgElements.add(rect);
				rect.bake(geometry,hatchOffset.get());
			}
			else if (name.equals("line")) {
				SVGLine line = new SVGLine((Element) n);
				svgElements.add(line);
				line.bake(geometry,hatchOffset.get());
			}
			else if (name.equals("ellipse")) {
				SVGEllipse ellipse = new SVGEllipse((Element) n);
				svgElements.add(ellipse);
				ellipse.bake(geometry,hatchOffset.get());
			}
			else if (name.equals("circle")) {
				SVGEllipse circle = new SVGEllipse((Element) n);
				svgElements.add(circle);
				circle.bake(geometry,hatchOffset.get());
			}
			else if (name.equals("path")) {
				SVGPath path = new SVGPath((Element) n);
				svgElements.add(path);
				path.bake(geometry,hatchOffset.get());
			}
			else if (name.equals("polyline")) {
				SVGPolyLine polyline = new SVGPolyLine((Element) n);
				svgElements.add(polyline);
				polyline.bake(geometry,hatchOffset.get());
			}
		}
	}
	
	@Override
	public void controllerEvent(String name) {
		if (name.equals("fileChooser")) {
			fileName = fileChooser.text;
			parseFile();
		}
		else if (name.equals("toolpathCheck")) {
			if (checkBox.state){
				associatedView.changeType(ViewType.PERSP);
			}
			else {
				associatedView.changeType(ViewType.TOP);
			}
		}
		else if (name.equals("stream")) {
			initStream();
		}
		else if (name.equals("hatchOffset")) {
			parseFile();
		}
		
	}
	
	private void initStream() {
		streaming = true;
		
		
		
		
	}
	
	private void updateStream() {
	
	}
	

	
	@Override
	public void setupControl() {
		controllerManager = new ControllerManager(this);
		
		outputName = new Controller_TextField(controllerManager,"outputName","Output File Name",20,parent.getHeight() - 230,120,20);
		controllerManager.add(outputName);
		
		fileChooser = new Controller_FileChooser(controllerManager,"fileChooser",10,10,parent.getWidth()-20,20);
		controllerManager.add(fileChooser);
		
		checkBox = new Controller_CheckBox(controllerManager, "toolpathCheck", "Show Tool Path", 20, parent.getHeight() - 110, 20, 20);
		controllerManager.add(checkBox);
		
		controllerManager.add(new Controller_Button(controllerManager,"export","Export",
				20,getHeight() - 150,20,20));
		
		controllerManager.add(new Controller_TextField(controllerManager,"minimalLineDistance","Minimal Line Distance",minimalLineDistance,
				20,getHeight() - 190,60,20));
		
		controllerManager.add(new Controller_TextField(controllerManager,"hatchOffset","Hatch Offset",hatchOffset,
				200,getHeight() - 190,60,20));
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
	
	

	

}