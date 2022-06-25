package render_sdf.renderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import controller.*;
import geometry.Geometry;
import geometry.GeometryDatabase;
import geometry.Rect;
import render_sdf.sdf.*;
import ui.*;
import utility.Color;
import utility.PVectorD;
import utility.Util;


public class Content_Renderer extends Content implements Controllable {

	private UIEControlManager controllerManager;
	private UIEFileChooser fileChooser;
	private UIETextField textfieldSDFObjectList;
	private UIEButton buttonRender;
	private UIEButton buttonRender2D;
	private UIEProgressBar progressBar;

	private Content_View previewWindow;

	public Scene scene;

	public SDF sdfScene;

	boolean debug = false;

	private int renderWidth = 800;
	private int renderHeight = 800;

	private volatile PVectorD[] colors;
	private volatile ByteBuffer colorBuffer;

	private boolean performFinalize = false;


	public Content_Renderer(Panel parent, Content_View previewWindow) {
		super(parent);

		this.previewWindow = previewWindow;
		this.previewWindow.changeType(ViewType.TOP);

		previewWindow.geometry = new GeometryDatabase();

		setupControl();

		setupSDFDemo();
		//setup2DDemo();
	}


	@Override
	public void render() {
		controllerManager.render();
		// System.out.println("yo");

		if (performFinalize) {
			renderFinalize();
		}
	}


	private void setupControl() {
		controllerManager = new UIEControlManager(getWidth(), getHeight(), 10, 30, 10, 10);

		fileChooser = new UIEFileChooser(this, "fileChooser", "File Chooser", 0, 0, parent.getWidth() - 20, 20, controllerManager, true, false);
		controllerManager.add(fileChooser);

		controllerManager.newLine();

		textfieldSDFObjectList = new UIETextField(this, "sdf_object_list", "SDF Objects", 0, 0, getWidth() - 30, 200);
		textfieldSDFObjectList.currentString = "abcdefghijklmnopqrs\ntuvwxyz0123456789.,/_-()";
		controllerManager.add(textfieldSDFObjectList);

		controllerManager.newLine();

		buttonRender = new UIEButton(this, "button_render", "Render", 0, 0, 20, 20);
		controllerManager.add(buttonRender);
		
		buttonRender2D = new UIEButton(this, "button_render_2d", "Render 2D", 0, 0, 20, 20);
		controllerManager.add(buttonRender2D);

		progressBar = new UIEProgressBar(this, "progress_bar", "Render Progress", 0, 0, parent.getWidth() - 20, 20, 1.0f);
		controllerManager.add(progressBar);

		controllerManager.finalize();
	}


	private void setupSDFDemo() {
		scene = new Scene(this.parent.getWidth(), this.parent.getHeight());

		sdfScene = new SDFGroundPlane(0);
		// sdfScene = new SDFCross(new VectorD(0,30,20),2);

		sdfScene = new SDFDifference(sdfScene, new SDFSphere(new PVectorD(0, 0, 0), 30));

		sdfScene = new SDFDifference(sdfScene, new SDFSphere(new PVectorD(-35, 0, 0), 20));
		sdfScene = new SDFDifference(sdfScene, new SDFSphere(new PVectorD(35, 0, 0), 20));
		sdfScene = new SDFDifference(sdfScene, new SDFSphere(new PVectorD(70, 0, 0), 20));
		sdfScene = new SDFDifference(sdfScene, new SDFSphere(new PVectorD(105, 0, 0), 20));
		

		 sdfScene = new SDFUnion(sdfScene, new SDFCube(new PVectorD(0,-10,10),5));
		 //sdfScene = new SDFChamfer(sdfScene, new SDFSphere(new PVectorD(0,-15,15),5),1);
		sdfScene = new SDFChamfer(sdfScene, new SDFCross(new PVectorD(0,30,20),2), 3);
		
		
		//sdfScene = new SDFUnion(sdfScene, new SDFDiamond(new PVectorD(0,-30,20),10));
		//sdfScene = new SDFUnion(sdfScene, new SDFFuckedStar(new PVectorD(0,-30,20),3));

		// sdfScene = new SDFAdd(sdfScene, new SDFTangent(0,1),0.3f);
		// sdfScene = new SDFAdd(sdfScene, new SDFSine(1,1),0.3f);
		// sdfScene = new SDFLerp(sdfScene, new SDFTangent(1,1),0.1);
	}
	
	private void setup2DDemo() {
		sdfScene = new SDFCross(new PVectorD(0,0,0),100);
		sdfScene = new SDFChamfer(sdfScene, new SDFCube(new PVectorD(0,0,0), 250), 50);
		//sdfScene = new SDFChamfer(sdfScene, new SDFCross(new PVectorD(300,300,0),50), 100);
		//sdfScene = new SDFUnion(sdfScene, new SDFCube(new PVectorD(0,0,0), 250));
	}


	private void renderScene() {
		long startTime = System.currentTimeMillis();

		// Perform raytracing
		colors = new PVectorD[renderWidth * renderHeight];

		//int threadCount = 4;
		int threadCount = Runtime.getRuntime().availableProcessors();
		System.out.println("Max Threadcount : " + threadCount);
		
		int threadDiv = colors.length / threadCount;
		RenderThread[] rt = new RenderThread[threadCount];

		for (int i = 0; i < threadCount; i++) {
			int start = threadDiv * i;
			int end = (threadDiv * (i + 1));
			
			if (i == threadCount - 1) {
				end = colors.length;
			}

			rt[i] = new RenderThread(start, end, i == rt.length - 1);
			rt[i].start();
			//System.out.println("Thread " + i + " ( " + start + " -> " + end + " ) ");
		}

		RenderEndThread ret = new RenderEndThread(rt);
		ret.start();
	}


	private void renderFinalize() {
		performFinalize = false;
		//GL.createCapabilities();
		int textureId = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, renderWidth, renderHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, colorBuffer);

		previewWindow.geometry.clear();
		previewWindow.geometry.add((Geometry) new Rect(0, 0, renderWidth, renderHeight, textureId));
		
		BufferedImage bi = new BufferedImage(renderWidth,renderHeight,3);
		for (int y = 0; y < renderHeight; y++) {
			for (int x = 0; x < renderWidth; x++) {
				Color c = new Color(colors[y * renderWidth + x]);
				bi.setRGB(x, y, c.toInt());
			}
		}
		
		try {
			String filename = Util.getTimestamp();
			String appPath = new File(".").getCanonicalPath();
			File outFile = new File(appPath + "\\output\\renders\\" + filename + ".png");
			System.out.println(outFile);
			ImageIO.write(bi, "png", outFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}


	private PVectorD handlePixelSDF(SDF sdf, int x, int y) {
		PVectorD rayPosition = scene.camera.position.copy();
		PVectorD rayVector = scene.camera.getRayVector(x, y);

		return (getColor(sdf, rayPosition, rayVector, false));
	}


	private PVectorD getColor(SDF sdf, PVectorD pos, PVectorD vec, boolean reflect) {
		PVectorD output = new PVectorD();
		
		PVectorD colorVector = new PVectorD(0, 0, 0);

		PVectorD hit = rayMarch(sdf, pos, vec, colorVector);
		debug = false;
		if (hit == null) {
			return (new PVectorD(0, 0, 0));
		}

		PVectorD shadowVector = PVectorD.sub(scene.sunPosition, hit).normalize();
		PVectorD normal = sdfScene.getNormal(hit);
		double angle = 1 - (PVectorD.angleBetween(normal, shadowVector) / (Math.PI));

		PVectorD color;
		PVectorD shadowCollision;

		if (reflect) {
			shadowCollision = getColor(sdf, PVectorD.add(hit, PVectorD.mult(normal, 0.01)), shadowVector, false);
			color = PVectorD.div(PVectorD.add(normal.setMag(255), shadowCollision), 2);
		}
		else {
			// color = normal.setMag(255);
			//color = new VectorD(Util.red(hitColor), Util.green(hitColor), Util.blue(hitColor));
			color = colorVector.copy();
			shadowCollision = rayMarch(sdf, PVectorD.add(hit, PVectorD.mult(normal, 0.01)), shadowVector, colorVector);
		}

		double mult = (shadowCollision == null) ? angle : scene.ambientLight;
		output.add(PVectorD.mult(color, mult));

		return (output);
	}


	private PVectorD rayMarch(SDF sdf, PVectorD pos, PVectorD vec, PVectorD colorVector) {
		double farClip = 1000;
		double travelled = 0;
		int count = 0;

		PVectorD posOriginal = pos.copy();

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

			if (PVectorD.dist(pos, posOriginal) >= farClip) {
				return (null);
			}

			count += 1;
			colorVector.x = count;
			colorVector.y = 255 - count;

		}

	}
	
	/** 
	 * Renders a non-marching 2d slice of the scene. Not using threading for now
	 */
	private void render2DSlice(SDF sdf, double z) {
		colors = new PVectorD[renderWidth * renderHeight];
		colorBuffer = ByteBuffer.allocateDirect(renderWidth * renderHeight * 4);
		colorBuffer.order(ByteOrder.nativeOrder());
		
		float scale = 1;
		
		for (int y = 0; y < renderHeight; y++) {
			for (int x = 0;  x <renderWidth; x++) {
				int py = renderHeight - 1 - y;
				
				double lx = (x - (renderWidth / 2)) / scale;
				double ly = (y - (renderHeight / 2)) / scale;
				PVectorD v = new PVectorD(lx,ly);
				
				double dist = sdf.getDistance(v);

				int r = (int)Math.max(0, Math.min((int) 255 - Math.abs(dist), 255));
				int g = (int)Math.max(0, Math.min((int) 0, 255));
				int b = (int)Math.max(0, Math.min((int) dist > 0 ? 0 : 255, 255));
				
				colorBuffer.put((byte) r);
				colorBuffer.put((byte) g);
				colorBuffer.put((byte) b);
				colorBuffer.put((byte) 255);
				
				PVectorD color = new Color(r,g,b).getVector();
				colors[y * renderWidth + x] = color;
			}
		}
		
		colorBuffer.flip();
		
		renderFinalize();
	}

	// Non-SDF raytrace system disabled for now
/*
	public PVectorD handlePixel(int x, int y) {
		PVectorD rayPosition = scene.camera.position.copy();
		PVectorD rayVector = scene.camera.getRayVector(x, y);

		PVectorD output = new PVectorD();

		int iterations = 1;

		for (int i = 0; i < iterations; i++) {
			Collision collision = castRay(rayPosition, rayVector);

			if (collision != null) {
				PVectorD shadowVector = PVectorD.sub(scene.sunPosition, collision.position).normalize();
				double angle = 1 - (PVectorD.angleBetween(collision.geometry.getNormal(collision.position), shadowVector) / (Math.PI));
				Collision shadowCollision = castRay(collision.position, shadowVector);

				double mult = (shadowCollision == null) ? angle : scene.ambientLight;
				output.add(PVectorD.mult(collision.geometry.material.diffuseColor.getVector(), mult));
			}
		}
		output.div(iterations);

		return (output);
	}
	*/

/*
	public Collision castRay(PVectorD rayPosition, PVectorD rayVector) {
		ArrayList<Collision> collisions = new ArrayList<Collision>();
		for (RenderGeometry g : scene.geometryList) {
			PVectorD v = g.intersect(rayPosition, rayVector);
			if (v != null) {
				collisions.add(new Collision(g, v));
			}
		}
		double bestDistance = Double.MAX_VALUE;
		Collision bestCollision = null;
		for (Collision c : collisions) {
			double distance = PVectorD.dist(rayPosition, c.position);
			if (distance < bestDistance) {
				bestDistance = distance;
				bestCollision = c;
			}
		}
		return (bestCollision);
	}
	
	*/


	@Override
	protected void keyPressed(int key) {
		if (key == 'r') {
			// redraw();
		}
		if (key == 'e') {
			// save("output/output-" + Util.getTimestamp() + ".png");
		}
		if (key == 's') {
			// scene.seed = (int) random(10000);
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
		// debug = true;
		// System.out.println(handlePixelSDF(sdfScene,mouseX,mouseY));
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
		else if (controller == buttonRender) {
			renderScene();
		}
		else if (controller == buttonRender2D) {
			render2DSlice(sdfScene,0);
		}

	}


	private void loadFile(String filename) {
		System.out.println(filename);
	}


	private class RenderThread extends Thread {
		private int start;
		private int stop;
		
		private boolean updateBar = false;


		public RenderThread(int start, int stop, boolean updateBar) {
			this.start = start;
			this.stop = stop;
			this.updateBar = updateBar;
		}


		@Override
		public void run() {
			for (int i = start; i < stop; i++) {
				int x = i % renderWidth;
				int y = i / renderWidth;
				colors[i] = handlePixelSDF(sdfScene, x, y);
				

				if (updateBar) {
					progressBar.update( 1.0f * (i - start) / (stop - start));
				}
			}
		}
	}


	private class RenderEndThread extends Thread {
		private RenderThread[] rts;

		public RenderEndThread(RenderThread[] rts) {
			this.rts = rts;
		}

		@Override
		public void run() {
			for (int i = 0; i < rts.length; i++) {
				try {
					rts[i].join();
					System.out.println(i);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
			
			// Send to texture
			colorBuffer = ByteBuffer.allocateDirect(renderWidth * renderHeight * 4);
			colorBuffer.order(ByteOrder.nativeOrder());

			for (int y = 0; y < renderHeight; y++) {
				for (int x = 0; x < renderWidth; x++) {
					int ly = renderHeight - 1 - y;
					colorBuffer.put((byte) Math.max(0, Math.min((int) colors[ly * renderWidth + x].x, 255)));
					colorBuffer.put((byte) Math.max(0, Math.min((int) colors[ly * renderWidth + x].y, 255)));
					colorBuffer.put((byte) Math.max(0, Math.min((int) colors[ly * renderWidth + x].z, 255)));
					colorBuffer.put((byte) 255);
				}
			}
			colorBuffer.flip();
			
			System.out.println("Rendering Complete");
			performFinalize = true;

			// System.out.println("Rendering took : " + (System.currentTimeMillis() -
			// startTime) + " milliseconds");
		}
	}
}
