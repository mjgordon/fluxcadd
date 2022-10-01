package robocam;

import static robocam.DrawbotConstants.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.joml.Vector3d;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import controller.*;
import event.EventMessage;
import geometry.Geometry;
import io.OutputGeneric;
import io.OutputSerial;
import io.CommandMessage;
import svg.*;
import ui.Content_View;
import ui.ViewType;
import ui.Content;
import utility.MutableFloat;
import utility.Util;

public class Module_Drawbot extends Module {

	public static MutableFloat minimalLineDistance = new MutableFloat(1);
	// private static MutableFloat hatchOffset = new MutableFloat(3);

	private UIEFileChooser fileChooser;
	private UIEToggle toggle;
	private UIETextField outputName;

	private ArrayList<SVGElement> svgElements;

	public static float canvasWidth;
	public static float canvasHeight;

	private String fileName = "svgs/test5.svg";

	private OutputGeneric output;


	public Module_Drawbot(Content parent, Content_View associatedView) {
		super(parent, associatedView);

		associatedView.changeType(ViewType.TOP);
		associatedView.flipped = true;
		associatedView.setGridSize(1);

		setupControl();

		parseFile();

		output = new OutputSerial("COM3");
		// output = new OutputNetwork("localhost",52323);

	}


	public void parseFile() {
		activate();

		Document doc = null;
		try {
			File xmlFile = new File(fileName);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (doc == null)
			return;

		svgElements = new ArrayList<SVGElement>();

		Element topElement = doc.getDocumentElement();
		String cw = topElement.getAttribute("width");
		canvasWidth = Float.valueOf(cw.substring(0, cw.length() - 2));
		String ch = topElement.getAttribute("height");
		canvasHeight = Float.valueOf(ch.substring(0, ch.length() - 2));

		// geometry.add(new Rect(0, 0, canvasWidth, canvasHeight));

		NodeList nl = doc.getDocumentElement().getChildNodes();

		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if (n.getNodeType() != Node.ELEMENT_NODE)
				continue;
			String name = (nl.item(i).getNodeName());

			if (name.equals("rect")) {
				SVGRect rect = new SVGRect((Element) n);
				svgElements.add(rect);
				rect.bake(geometry);
			}
			else if (name.equals("line")) {
				SVGLine line = new SVGLine((Element) n);
				svgElements.add(line);
				line.bake(geometry);
			}
			else if (name.equals("ellipse")) {
				SVGEllipse ellipse = new SVGEllipse((Element) n);
				svgElements.add(ellipse);
				ellipse.bake(geometry);
			}
			else if (name.equals("circle")) {
				SVGEllipse circle = new SVGEllipse((Element) n);
				svgElements.add(circle);
				circle.bake(geometry);
			}
			else if (name.equals("path")) {
				SVGPath path = new SVGPath((Element) n);
				svgElements.add(path);
				path.bake(geometry);
			}
			else if (name.equals("polyline")) {
				SVGPolyLine polyline = new SVGPolyLine((Element) n);
				svgElements.add(polyline);
				polyline.bake(geometry);
			}
		}
	}


	private ArrayList<CommandMessage> generateMessages(Geometry geom) {
		ArrayList<CommandMessage> out = new ArrayList<CommandMessage>();
		Vector3d[] points = geom.getVectorRepresentation(10);

		Vector3d p0 = points[0];
		// System.out.println("Shape");
		// System.out.println(p0);
		out.add(new CommandMessage(DB_PEN_UP));
		out.add(new CommandMessage(DB_GOTO_POSITION, Util.vector2DToByteArray(p0)));
		out.add(new CommandMessage(DB_PEN_DOWN));
		for (int i = 1; i < points.length; i++) {
			Vector3d p = points[i];
			// System.out.println(p);
			out.add(new CommandMessage(DB_GOTO_POSITION, Util.vector2DToByteArray(p)));
		}
		out.add(new CommandMessage(DB_PEN_UP));
		return (out);
	}


	private void initStream() {

		ArrayList<CommandMessage> messages = new ArrayList<CommandMessage>();
		System.out.println(geometry.geometry.size());
		for (Geometry geom : geometry.getIterable()) {
			System.out.println(geom);
			messages.addAll(generateMessages(geom));
		}

		output.send(messages);
	}


	@Override
	protected void setupControl() {
		outputName = new UIETextField(this, "outputName", "Output File Name", 0, 0, 120, 20);
		controllerManager.add(outputName);

		fileChooser = new UIEFileChooser(this, "fileChooser", "File Chooser", 0, 0, parent.getWidth() - 20, 20, controllerManager, true, false);
		controllerManager.add(fileChooser);

		toggle = new UIEToggle(this, "toolpathCheck", "Show Tool Path", 0, 0, 20, 20);
		controllerManager.add(toggle);

		controllerManager.add(new UIEButton(this, "stream", "Stream", 0, 0, 20, 20));

		controllerManager.add(new UIEButton(this, "stop", "Stop", 0, 0, 20, 20));

		controllerManager.finalizeLayer();

		// controllerManager.add(new UIETextField(this, "minimalLineDistance", "Minimal
		// Line Distance", minimalLineDistance, 20, getHeight() - 190, 60, 20));

		// controllerManager.add(new UIETextField(this, "hatchOffset", "Hatch Offset",
		// hatchOffset, 200, getHeight() - 190, 60, 20));
	}


	@Override
	public void message(EventMessage message) {
		UserInterfaceElement<? extends UserInterfaceElement<?>> controller = ((UIEEvent)message).element;
		
		String name = controller.getName();
		if (name.equals("fileChooser")) {
			fileName = fileChooser.getCurrentString();
			parseFile();
		}
		else if (name.equals("toolpathCheck")) {
			if (toggle.state) {
				associatedView.changeType(ViewType.PERSP);
			}
			else {
				associatedView.changeType(ViewType.TOP);
			}
		}
		else if (name.equals("stream")) {
			initStream();
		}
		else if (name.equals("stop")) {
			output.stop();
		}
		else if (name.equals("hatchOffset")) {
			parseFile();
		}
		
	}
}