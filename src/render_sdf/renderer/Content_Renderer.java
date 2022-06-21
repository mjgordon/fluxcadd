package render_sdf.renderer;

import java.util.ArrayList;

import controller.Controllable;
import controller.UIEButton;
import controller.UIEControlManager;
import controller.UIEFileChooser;
import controller.UIETerminal;
import controller.UIETextField;
import controller.UIEToggle;
import controller.UserInterfaceElement;
import render_sdf.raymarcher.*;
import render_sdf.raytracer.*;
import ui.Content;
import ui.Content_View;
import ui.Panel;
import utility.VectorD;

import utility.Util;

public class Content_Renderer extends Content implements Controllable {
	
	private UIEControlManager controllerManager;
	private UIEFileChooser fileChooser;
	private UIETextField textfieldSDFObjectList;
	private UIEButton buttonRender;
	
	private Content_View previewWindow;

	public SceneRaytrace scene;

	public SDF sdfScene;
	
	boolean debug = false;
	
	
	public static int hitColor = -1;
	
	private static VectorD colorVector = new VectorD(0,0,0);
	
	public Content_Renderer(Panel parent, Content_View previewWindow) {
		super(parent);
		
		this.previewWindow = previewWindow;
		
		setupControl();
	}
	
	@Override
	public void render() {
		controllerManager.render();
	}

	private void setupControl() {
		controllerManager = new UIEControlManager(getWidth(), getHeight(),10,30,10,10);
		/*

		toggleExternal = new UIEToggle(this, "toggle_external", "External", 0, 0, 20, 20);
		controllerManager.add(toggleExternal);

		toggleLive = new UIEToggle(this, "toggle_live", "Live Update", 0, 0, 20, 20);
		controllerManager.add(toggleLive);
		
		buttonReloadSystem = new UIEButton(this, "button_reload_system", "Reload System", 0, 0, 20, 20);
		controllerManager.add(buttonReloadSystem);

		buttonReloadTest = new UIEButton(this, "button_reload_test", "Reload Test", 0, 0, 20, 20);
		controllerManager.add(buttonReloadTest);

		*/
		
		fileChooser = new UIEFileChooser(this, "fileChooser","File Chooser", 0,0, parent.getWidth() - 20, 20,controllerManager,true,false);
		controllerManager.add(fileChooser);
		
		controllerManager.newLine();

		textfieldSDFObjectList = new UIETextField(this, "sdf_object_list", "SDF Objects", 0, 0, getWidth() - 30, 200);
		textfieldSDFObjectList.currentString = "abcdefghijklmnopqrs\ntuvwxyz0123456789.,/_-()";
		controllerManager.add(textfieldSDFObjectList);
		
		controllerManager.newLine();
		
		buttonRender = new UIEButton(this, "button_render", "Render", 0, 0, 20, 20);
		controllerManager.add(buttonRender);
		
		
		
		controllerManager.finalize();
		
	}
	
	public void SDFDemo() {
		scene = new SceneRaytrace(this.parent.getWidth(),this.parent.getHeight());

		sdfScene = new SDFGroundPlane(0);
		//sdfScene = new SDFCross(new VectorD(0,30,20),2);
		
		sdfScene = new SDFDifference(sdfScene, new SDFSphere(new VectorD(0, 0, 0), 30));
		
		sdfScene = new SDFDifference(sdfScene, new SDFSphere(new VectorD(-35, 0), 20));
		sdfScene = new SDFDifference(sdfScene, new SDFSphere(new VectorD(35, 0), 20));
		sdfScene = new SDFDifference(sdfScene, new SDFSphere(new VectorD(70, 0), 20));
		sdfScene = new SDFDifference(sdfScene, new SDFSphere(new VectorD(105, 0), 20));
		
		
		//sdfScene = new SDFUnion(sdfScene, new SDFCube(new VectorD(0,-10,10),5));
		//sdfScene = new SDFUnion(sdfScene, new SDFCube(new VectorD(0,10,10),5));
		
		
		//sdfScene = new SDFUnion(sdfScene, new SDFCross(new VectorD(0,30,20),2));
		sdfScene = new SDFUnion(sdfScene, new SDFFuckedStar(new VectorD(0,-30,20),3));
		//sdfScene = new SDFUnion(sdfScene, new SDFDiamond(new VectorD(0,-30,20),10));
		
		//sdfScene = new SDFAdd(sdfScene, new SDFTangent(0,1),0.3f);
		//sdfScene = new SDFAdd(sdfScene, new SDFSine(1,1),0.3f);
		//sdfScene = new SDFLerp(sdfScene, new SDFTangent(1,1),0.1);
	}
	
	
	public static int currentY = 0;

	
	public void draw() {
		//SDF.epsilon = Math.max(1.00f * mouseX / width, 0.00001f);
		long start = System.currentTimeMillis();
		//background(0);
		//fill(255, 0, 0);
		//rect(0, 0, width, height);
		//randomSeed(scene.seed);

		// Perform raytracing
		VectorD[] colors = new VectorD[parent.getWidth() * parent.getHeight()];
		for (int i = 0; i < colors.length; i++) {
			int x = i % parent.getHeight();
			int y = i / parent.getWidth();
			if (x % (parent.getWidth() * 100) == 0) {
				System.out.println(y);
			}
			//colors[i] = handlePixel(x,y);
			colors[i] = handlePixelSDF(sdfScene, x, y);
		}

		/*
		// Update screen pixels
		loadPixels();
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = color((float)colors[i].x, (float)colors[i].y, (float)colors[i].z);
		}
		updatePixels();
		*/

		// Output
		System.out.println("Rendering took : " + (System.currentTimeMillis() - start) + " milliseconds");

	}

	public VectorD handlePixelSDF(SDF sdf, int x, int y) {
		VectorD rayPosition = scene.camera.position.copy();
		VectorD rayVector = scene.camera.getRayVector(x,y);
		
		if (debug) {
			System.out.println(x + " : " + y);
		}

		return(getColor(sdf,rayPosition,rayVector,false));
	}
	
	private VectorD getColor(SDF sdf, VectorD pos, VectorD vec,boolean reflect) {
		VectorD output = new VectorD();

		VectorD hit = rayMarch(sdf, pos, vec);
		debug = false;
		if (hit == null) {
			return (new VectorD(0,0,0));
		}
		
		VectorD shadowVector = VectorD.sub(scene.sunPosition, hit).normalize();
		VectorD normal = sdfScene.getNormal(hit);
		double angle = 1 - (VectorD.angleBetween(normal, shadowVector) / (Math.PI));
				
		VectorD color;
		VectorD shadowCollision;

		
		if (reflect) {
			shadowCollision =  getColor(sdf, VectorD.add(hit, VectorD.mult(normal, 0.01)), shadowVector,false);
			color = VectorD.div(VectorD.add(normal.setMag(255), shadowCollision),2);
		}
		else {
			//color = normal.setMag(255);
			color = new VectorD(Util.red(hitColor),Util.green(hitColor),Util.blue(hitColor));
			color = colorVector.copy();
			shadowCollision = rayMarch(sdf,VectorD.add(hit, VectorD.mult(normal, 0.01)),shadowVector);
		}

		double mult = (shadowCollision == null) ? angle : scene.ambientLight;
		output.add(VectorD.mult(color, mult));
		
		return (output);
	}

	private VectorD rayMarch(SDF sdf, VectorD pos, VectorD vec) {
		double farClip = 1000;
		double travelled = 0;
		int count = 0;
		
		VectorD posOriginal = pos.copy();
		
		while (true) {
			double dist = sdf.getDistance(pos);
			
			if (debug) {
				System.out.println("POS : " + pos);
				System.out.println("DIST : " + dist);
			}

			if (dist <= SDF.epsilon) {
				return (pos);
			} 
			
			vec.setMag(dist * SDF.distanceFactor);
			pos.add(vec);
			travelled += dist;

			if (VectorD.dist(pos, posOriginal) >= farClip) {
				return (null);
			}
			
			count += 1;
			colorVector.x = count;
			colorVector.y = 255 - count;
			
		}
		
		
	}

	public VectorD handlePixel(int x, int y) {
		Content_Renderer.currentY = y;
		VectorD rayPosition = scene.camera.position.copy();
		VectorD rayVector = scene.camera.getRayVector(x, y);
		
		VectorD output = new VectorD();

		int iterations = 1;

		for (int i = 0; i < iterations; i++) {
			Collision collision = castRay(rayPosition, rayVector);

			if (collision != null) {
				VectorD shadowVector = VectorD.sub(scene.sunPosition, collision.position).normalize();
				double angle = 1 - (VectorD.angleBetween(collision.geometry.getNormal(collision.position), shadowVector) / (Math.PI));
				Collision shadowCollision = castRay(collision.position, shadowVector);

				double mult = (shadowCollision == null) ? angle : scene.ambientLight;
				output.add(VectorD.mult(collision.geometry.material.diffuseColor.getVector(), mult));
			}
		}
		output.div(iterations);
	
		return (output);
	}

	public Collision castRay(VectorD rayPosition, VectorD rayVector) {
		ArrayList<Collision> collisions = new ArrayList<Collision>();
		for (Geometry g : scene.geometryList) {
			VectorD v = g.intersect(rayPosition, rayVector);
			if (v != null) {
				collisions.add(new Collision(g, v));
			}
		}
		double bestDistance = Double.MAX_VALUE;
		Collision bestCollision = null;
		for (Collision c : collisions) {
			double distance = VectorD.dist(rayPosition, c.position);
			if (distance < bestDistance) {
				bestDistance = distance;
				bestCollision = c;
			}
		}
		return (bestCollision);
	}


	@Override
	protected void keyPressed(int key) {
		if (key == 'r') {
			//redraw();
		}
		if (key == 'e') {
			//save("output/output-" + Util.getTimestamp() + ".png");
		}
		if (key == 's') {
			//scene.seed = (int) random(10000);
		}
		
	}

	@Override
	protected void textInput(char character) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void mouseWheel(float amt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void mousePressed(int button, int mouseX, int mouseY) {
//		scene.camera.focalLength = (1.0f * mouseX / width) * scene.camera.displayWidth;
//		redraw();
		//debug = true;
		//System.out.println(handlePixelSDF(sdfScene,mouseX,mouseY));
		if (button == 0) {
			controllerManager.poll(mouseX, mouseY);
		}
	}

	@Override
	protected void mouseDragged(int dx, int dy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void controllerEvent(UserInterfaceElement controller) {
		if (controller == fileChooser) {
			String filename = fileChooser.getCurrentString();
			loadFile(filename);
		}
		
	}
	
	private void loadFile(String filename) {
		System.out.println(filename);
	}


}
