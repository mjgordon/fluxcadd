package robocam;

import geometry.Shape;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.JFileChooser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
import console.Console;
import controller.Controllable;
import controller.ControllerManager;
import controller.Controller_Button;
import controller.Controller_CheckBox;
import controller.Controller_FileChooser;
import controller.Controller_TextField;

// 11 in  = 279.4 mm 8.5 in = 215.9mm

public class Module_Plotter extends Module implements Controllable {
	
	public static MutableFloat minimalLineDistance = new MutableFloat(1);
	private static MutableFloat hatchOffset = new MutableFloat(3);

	private Controller_FileChooser fileChooser;
	private Controller_CheckBox checkBox;
	private Controller_TextField outputName;
	
	private ArrayList<SVGElement> svgElements;
	
	private static ArrayList<String> output;
	
	public static float canvasWidth;
	public static float canvasHeight;
	
	private static float scalar;
	
	private String fileName = "svgs/section.svg";
	
	private int toolId;
	private static final int TOOL_SHARPIE = 0;
	private static final int TOOL_BRUSH = 1;
	
	private PVector brushWaterLocation = new PVector(-50,250,-50);
	private PVector brushPaintOrigin = new PVector(-45,0,-13);
	private PVector brushPaintOffset = new PVector(0,26,0);
	
	private int distanceBeforeRecharge = 300;
	
	public Module_Plotter(Content parent,Content_View associatedView) {
		super(parent,associatedView);
		
		associatedView.changeType(ViewType.TOP);
		associatedView.flipped = true;
		
		setupControl();	
		
		parseFile();
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
		else if (name.equals("export")) {
			export();
		}
		else if (name.equals("hatchOffset")) {
			parseFile();
		}
		
	}
	
	public void export() {
		File file;
		String path = "";
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = chooser.getSelectedFile();
			path = file.getAbsolutePath();
		}
		if (outputName.currentString.equals("")) {
			path += "/gen_plot.src";
		}
		else {
			String s = outputName.currentString.toLowerCase();
			if (s.contains(".src") == false) s += ".src";
			path += "/" + s;
		}
		
		Console.instance().log("Exporting .src file");
		Console.instance().log("Document has " + svgElements.size() + " elements");
		
		output = new ArrayList<String>();
		
		try {
			BufferedReader prefixReader = new BufferedReader(new FileReader("scripts/prefix.txt"));
			String line = prefixReader.readLine();
			while(line != null) {
				output.add(line);
				line = prefixReader.readLine();
			}
			prefixReader.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		output.add("\n; === Begin generated code ===\n");
		
		output.add("$BASE = BASE_DATA[4]");
		output.add("$TOOL = TOOL_DATA[5]");
		
		if (toolId == TOOL_SHARPIE) output.add("LIN {X 20, Y 20, Z -20, B 90}");
		else if (toolId == TOOL_BRUSH) output.add("LIN {X 20, Y 20, Z -20, C 45}");
		
		
		for(SVGElement element : svgElements) {
			element.plot(output,hatchOffset.get());
		}
		
		output.add("\n; === End generated code ===\n");
		
		try {
			BufferedReader suffixReader = new BufferedReader(new FileReader("scripts/suffix.txt"));
			String line = suffixReader.readLine();
			while(line != null) {
				output.add(line);
				line = suffixReader.readLine();
			}
			suffixReader.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		PrintWriter out;
		try {
			out = new PrintWriter(path);
			for (String s : output) out.println(s);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}		
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