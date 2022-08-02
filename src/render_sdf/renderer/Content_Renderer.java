package render_sdf.renderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;
import org.lwjgl.opengl.GL11;

import controller.*;
import geometry.Geometry;
import geometry.GeometryDatabase;
import geometry.Rect;
import render_sdf.material.Material;
import render_sdf.sdf.*;
import ui.*;
import utility.Color;
import utility.Util;

public class Content_Renderer extends Content implements Controllable {

	private UIEControlManager controllerManager;
	private UIEFileChooser fileChooser;
	private UIETextField textfieldSDFObjectList;
	private UIETextField cameraPositionX;
	private UIETextField cameraPositionY;
	private UIETextField cameraPositionZ;
	private UIETextField cameraTargetX;
	private UIETextField cameraTargetY;
	private UIETextField cameraTargetZ;
	private UIEButton buttonRender;
	private UIEButton buttonCancel;
	private UIEButton buttonResult;
	private UIEButton buttonRender2D;
	private UIEProgressBar progressBar;

	private Content_View previewWindow;

	public Scene scene;

	public SDF sdfScene;

	boolean debug = false;

	private int renderWidth = 800;
	private int renderHeight = 600;

	/**
	 * Intermediate render data, outer array is each level of detail Direct colors
	 * for each pixel as returned by the raymarcher, later converted to the
	 * ByteBuffer for display or read directly for saving
	 */
	private volatile Color[][] colors;

	/**
	 * Bytebuffer representation of the rendered colors, converted in a new thread
	 * after all render threads have joined, then not touched until the UI thread
	 * gets back
	 */
	private volatile ByteBuffer colorBuffer;

	/**
	 * Flag to indicate if the UI thread should act on the render results
	 */
	private boolean performFinalize = false;

	/**
	 * Timestamp for the start of the render
	 */
	private long renderStartTime;

	/**
	 * Most recently completed level of detail
	 */
	private int lastLevel = -1;

	/**
	 * Total levels of detail for render process
	 */
	private int renderLevels = -1;

	/**
	 * X coordinates of new pixels to be rendered at each level of detail
	 */
	private ArrayList<Integer>[] xListUnique;

	/**
	 * Y Coordinates of new pixels to be rendered at each level of detail
	 */
	private ArrayList<Integer>[] yListUnique;

	/**
	 * Dimensions of 'image' at each level of detail
	 */
	private int levelWidth[];
	private int levelHeight[];

	private RenderThread[] renderThreads;
	private RenderEndThread renderEndThread;

	private volatile boolean cancelFlag = false;

	private GeometryDatabase geometryScenePreview;
	private GeometryDatabase geometryRenderPreview;


	public Content_Renderer(Panel parent, Content_View previewWindow) {
		super(parent);

		geometryScenePreview = new GeometryDatabase();
		geometryRenderPreview = new GeometryDatabase();

		setupControl();

		setupSDFDemoMain();
		// setupSDFDemoCross();
		// setup2DDemo();

		this.previewWindow = previewWindow;
		this.previewWindow.renderGrid = false;
		setViewScenePreview();

		updateCameraLabels();

	}


	@Override
	public void render() {
		// Currently, this is turned off as it causes fireflies, I think because internally it touches the camera variables while the threads are reading them, 
		// even though it doesn't change them
		//copyViewToCamera(); 
		//updateCameraLabels();
		
		controllerManager.render();

		if (performFinalize) {
			renderLevelFinalize();
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

		{
			UIEVerticalStack stackPosition = new UIEVerticalStack(this, "stack_position", "", 0, 0, 120, 0);
			stackPosition.add(new UIELabel(this, "camera_position_label", "Camera Position", 0, 0, 100, 20));
			cameraPositionX = new UIETextField(this, "camera_position_x", "X", 0, 0, 100, 20).setClearOnExecute(false);
			stackPosition.add(cameraPositionX);
			cameraPositionY = new UIETextField(this, "camera_position_y", "Y", 0, 0, 100, 20).setClearOnExecute(false);
			stackPosition.add(cameraPositionY);
			cameraPositionZ = new UIETextField(this, "camera_position_y", "Z", 0, 0, 100, 20).setClearOnExecute(false);
			stackPosition.add(cameraPositionZ);
			stackPosition.close();
			controllerManager.add(stackPosition);
		}
		{
			UIEVerticalStack stackTarget = new UIEVerticalStack(this, "stack_target", "", 0, 0, 120, 0);
			stackTarget.add(new UIELabel(this, "camera_target_label", "Camera Target", 0, 0, 100, 20));
			cameraTargetX = new UIETextField(this, "camera_target_x", "X", 0, 0, 100, 20).setClearOnExecute(false);
			stackTarget.add(cameraTargetX);
			cameraTargetY = new UIETextField(this, "camera_target_y", "Y", 0, 0, 100, 20).setClearOnExecute(false);
			stackTarget.add(cameraTargetY);
			cameraTargetZ = new UIETextField(this, "camera_target_y", "Z", 0, 0, 100, 20).setClearOnExecute(false);
			stackTarget.add(cameraTargetZ);
			stackTarget.close();
			controllerManager.add(stackTarget);
		}

		controllerManager.newLine();

		buttonRender = new UIEButton(this, "button_render", "Render", 0, 0, 20, 20);
		controllerManager.add(buttonRender);

		buttonCancel = new UIEButton(this, "button_cancel", "Cancel", 0, 0, 20, 20);
		controllerManager.add(buttonCancel);

		buttonResult = new UIEButton(this, "button_result", "Result", 0, 0, 20, 20);
		controllerManager.add(buttonResult);

		controllerManager.newLine();

		progressBar = new UIEProgressBar(this, "progress_bar", "Render Progress", 0, 0, parent.getWidth() - 20, 20, 1.0f);
		controllerManager.add(progressBar);

		controllerManager.newLine();

		buttonRender2D = new UIEButton(this, "button_render_2d", "Render 2D", 0, 0, 20, 20);
		controllerManager.add(buttonRender2D);

		controllerManager.finalize();
	}


	private void setupSDFDemoMain() {
		scene = new Scene(renderWidth, renderHeight);
		scene.camera.setPosition(new Vector3d(100, -100, 30));
		scene.camera.setTarget(new Vector3d(0, 0, -10));

		Material materialMain = new Material(new Color(0xFF0000), 0);
		Material materialCarve = new Material(new Color(0x0000FF), 0);
		Material materialReflect = new Material(new Color(0xFFFFFF), 1);

		sdfScene = new SDFPrimitiveGroundPlane(0, materialMain);

		sdfScene = new SDFBoolDifference(sdfScene, new SDFPrimitiveSphere(new Vector3d(0, 0, 0), 30, materialCarve));

		sdfScene = new SDFBoolDifference(sdfScene, new SDFPrimitiveSphere(new Vector3d(-35, 0, 0), 20, materialCarve));
		sdfScene = new SDFBoolDifference(sdfScene, new SDFPrimitiveSphere(new Vector3d(35, 0, 0), 20, materialCarve));
		sdfScene = new SDFBoolDifference(sdfScene, new SDFPrimitiveSphere(new Vector3d(70, 0, 0), 20, materialCarve));
		sdfScene = new SDFBoolDifference(sdfScene, new SDFPrimitiveSphere(new Vector3d(105, 0, 0), 20, materialCarve));

		sdfScene = new SDFBoolUnion(sdfScene, new SDFPrimitiveCube(new Vector3d(0, 20, 10), 10, materialMain));
		sdfScene = new SDFOpChamfer(sdfScene, new SDFPrimitiveSphere(new Vector3d(0, 25, 15), 5, materialCarve), 1);

		sdfScene = new SDFBoolUnion(sdfScene, new SDFPrimitiveCube(new Vector3d(0, -20, 10), 10, materialMain));
		sdfScene = new SDFOpSmooth(sdfScene, new SDFPrimitiveSphere(new Vector3d(0, -25, 15), 5, materialCarve), 1);

		sdfScene = new SDFBoolUnion(sdfScene, new SDFPrimitiveSphere(new Vector3d(-30, 0, 15), 10, materialReflect));

		Matrix4d crossOffset = new Matrix4d();
		crossOffset.setColumn(3, new Vector4d(0, 32, 20, 1)).rotate(Math.PI / 4, 1, 0, 0);
		sdfScene = new SDFOpSmooth(sdfScene, new SDFPrimitiveCross(crossOffset, 2, materialMain), 3);

		geometryScenePreview.clear();
		sdfScene.extractSceneGeometry(geometryScenePreview, true);
	}


	private void setupSDFDemoCross() {
		scene = new Scene(renderWidth, renderHeight);
		scene.camera.setPosition(new Vector3d(100, -100, 30));
		scene.camera.setTarget(new Vector3d(0, 0, -10));

		Material materialGround = new Material(new Color(0x3D5A80), 0);
		Material materialCross = new Material(new Color(0x98C1D9), 0);
		Material materialCut = new Material(new Color(0xEE6C4D), 0);

		sdfScene = new SDFPrimitiveGroundPlane(0, materialGround);

		for (int i = 0; i < 10; i++) {
			Matrix4d crossMatrix = new Matrix4d();
			crossMatrix.setColumn(3, new Vector4d(i * 10, 0, 20, 1)).rotate(Math.PI / 24 * i, 1, 0, 0);
			sdfScene = new SDFOpSmooth(sdfScene, new SDFPrimitiveCross(crossMatrix, 2, materialCross), 3);
		}

		sdfScene = new SDFBoolDifference(sdfScene, new SDFPrimitiveSphere(new Vector3d(60, 10, 15), 10, materialCut));

		geometryScenePreview.clear();
		sdfScene.extractSceneGeometry(geometryScenePreview, true);
	}


	@SuppressWarnings("unused")
	private void setup2DDemo() {
		Material materialMain = new Material(new Color(0xFF0000), 0);

		sdfScene = new SDFPrimitiveCross(new Vector3d(0, 0, 0), 75, materialMain);
		sdfScene = new SDFOpChamfer(sdfScene, new SDFPrimitiveCube(new Vector3d(200, 0, 0), 200, materialMain), 50);
		sdfScene = new SDFOpFillet(sdfScene, new SDFPrimitiveCube(new Vector3d(-200, 0, 0), 200, materialMain), 100);
	}


	@SuppressWarnings("unchecked")
	private void renderScene() {
		setViewScenePreview(); // Go to scene preview first to make sure we're copying the correct target and
		// eye vectors
		scene.camera.setPosition(previewWindow.getVectorEye());
		scene.camera.setTarget(previewWindow.getVectorTarget());
		updateCameraLabels();
		setViewRenderPreview();

		renderStartTime = System.currentTimeMillis();
		cancelFlag = false;

		renderLevels = (int) Util.log2(Math.max(renderWidth, renderHeight)) + 1;

		xListUnique = (ArrayList<Integer>[]) new ArrayList[renderLevels];
		yListUnique = (ArrayList<Integer>[]) new ArrayList[renderLevels];

		levelWidth = new int[renderLevels];
		levelHeight = new int[renderLevels];

		int levelCount[] = new int[renderLevels];

		for (int i = 0; i < renderLevels; i++) {
			levelWidth[i] = 0;
			levelHeight[i] = 0;
			levelCount[i] = 0;
			xListUnique[i] = new ArrayList<Integer>();
			yListUnique[i] = new ArrayList<Integer>();
		}

		for (int y = 0; y < renderHeight; y++) {
			for (int x = 0; x < renderWidth; x++) {
				boolean flag = true;
				for (int i = renderLevels - 1; i >= 0; i--) {
					int n = 1 << i;
					if (x % n == 0 && y % n == 0) {
						levelCount[i] += 1;

						if (y == 0) {
							levelWidth[i] += 1;
						}

						if (flag) {
							xListUnique[i].add(x);
							yListUnique[i].add(y);
						}

						flag = false;
					}
				}
			}
		}

		colors = new Color[renderLevels][];

		for (int i = 0; i < renderLevels; i++) {
			colors[i] = new Color[levelCount[i]];
			levelHeight[i] = levelCount[i] / levelWidth[i];
		}

		System.out.println("Start Render : " + renderWidth + "x" + renderHeight + " : " + renderLevels + " lod");

		renderLevel(renderLevels - 1);
	}


	/**
	 * Start the threads for a single level of detail
	 * 
	 * @param lod
	 */
	private void renderLevel(int lod) {
		if (cancelFlag) {
			return;
		}

		// By default, assign threadcount by number of processors
		// In early levels of detail, use single threading, the *128 is arbitrary for
		// now
		int threadCount = Runtime.getRuntime().availableProcessors();
		if (xListUnique[lod].size() < threadCount * 128) {
			threadCount = 1;
		}

		progressBar.setDisplayName("Render Progress | Level : " + lod + " | Threadcount : " + threadCount);

		int threadDiv = colors.length / threadCount;
		renderThreads = new RenderThread[threadCount];

		for (int i = 0; i < threadCount; i++) {
			int start = threadDiv * i;
			int end = (threadDiv * (i + 1));

			if (i == threadCount - 1) {
				end = xListUnique[lod].size();
			}

			renderThreads[i] = new RenderThread(start, end, i == renderThreads.length - 1, lod);
			renderThreads[i].start();
		}

		renderEndThread = new RenderEndThread(renderThreads, lod);
		renderEndThread.start();
	}


	/**
	 * Finalizes a single level of detail Called by main UI thread due to flag set
	 * by the RenderEndThread Assigns the colorBuffer to a texture in the display,
	 * and exports the image if necessary Binding the texture must apparently be
	 * done here as part of the main thread, as opposed to in the finalization
	 * thread
	 */
	private void renderLevelFinalize() {
		performFinalize = false;

		int imageWidth = levelWidth[lastLevel];
		int imageHeight = levelHeight[lastLevel];

		int textureId = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, imageWidth, imageHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, colorBuffer);

		previewWindow.geometry.clear();
		previewWindow.geometry.add((Geometry) new Rect(renderWidth, renderHeight, renderWidth, renderHeight, textureId));

		if (lastLevel > 0) {
			renderLevel(lastLevel - 1);
		}
		else {
			long renderEndTime = System.currentTimeMillis();
			System.out.println("Render Time : " + (renderEndTime - renderStartTime) / 1000.0 + " Seconds");
			progressBar.setDisplayName("Render Progress");
			saveRenderToFile();
		}
	}


	private void saveRenderToFile() {
		BufferedImage bi = new BufferedImage(renderWidth, renderHeight, 3);
		for (int y = 0; y < renderHeight; y++) {
			for (int x = 0; x < renderWidth; x++) {
				Color c = colors[0][y * renderWidth + x];
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


	private Color getSDFRayColor(SDF sdf, Vector3d pos, Vector3d vec) {
		Color output = new Color(0, 0, 0);

		Material material = new Material(null, 0);

		Vector3d hit = rayMarch(sdf, pos, vec, material);

		if (hit == null) {
			return (new Color(0, 0, 0));
		}

		Vector3d normal = sdfScene.getNormal(hit);

		if (material.reflectivity > 0) {
			Vector3d newStart = new Vector3d(normal).mul(0.1).add(hit);
			Color reflectedColor = getSDFRayColor(sdf, newStart, new Vector3d(normal));
			material.diffuseColor.set(Color.lerpColor(material.diffuseColor, reflectedColor, material.reflectivity));
		}

		Vector3d shadowVector = new Vector3d(scene.sunPosition).sub(hit).normalize();
		double angle = 1 - (normal.angle(shadowVector) / Math.PI);

		Vector3d shadowCollision = rayMarch(sdf, new Vector3d(normal).mul(0.01).add(hit), shadowVector, material.copy());

		double multFactor = (shadowCollision == null) ? angle : scene.ambientLight;
		output.set(material.diffuseColor);
		output.mult(multFactor);

		return (output);
	}


	private Vector3d rayMarch(SDF sdf, Vector3d pos, Vector3d vec, Material material) {
		double farClip = 1000;

		Vector3d posOriginal = new Vector3d(pos);

		while (true) {
			DistanceData distanceData = sdf.getDistance(pos);
			double dist = distanceData.distance;
			material.set(distanceData.material);

			if (debug) {
				System.out.println("POS : " + pos);
				System.out.println("DIST : " + dist);
			}

			if (dist <= SDF.epsilon) {
				return (pos);
			}

			vec.normalize(dist * SDF.distanceFactor);
			pos.add(vec);

			if (pos.distance(posOriginal) >= farClip) {
				return (null);
			}
		}
	}


	/**
	 * Renders a non-marching 2d slice of the scene. Not using threading for now
	 */
	private void render2DSlice(SDF sdf, double z) {
		colorBuffer = ByteBuffer.allocateDirect(renderWidth * renderHeight * 4);
		colorBuffer.order(ByteOrder.nativeOrder());

		float scale = 8;

		for (int y = 0; y < renderHeight; y++) {
			for (int x = 0; x < renderWidth; x++) {
				// int py = renderHeight - 1 - y;

				double lx = (x - (renderWidth / 2.0)) / scale;
				double ly = (y - (renderHeight / 2.0)) / scale;
				Vector3d v = new Vector3d(lx, ly, z);

				DistanceData distanceData = sdf.getDistance(v);
				double dist = distanceData.distance;

				int r = 255 - (int) Math.max(0, Math.min((int) Math.abs(dist) * 10.0, 255));
				int g = (int) Math.max(0, Math.min((int) 0, 255));
				int b = (int) Math.max(0, Math.min((int) dist > 0 ? 0 : 255, 255));

				colorBuffer.put((byte) r);
				colorBuffer.put((byte) g);
				colorBuffer.put((byte) b);
				colorBuffer.put((byte) 255);

				// Color color = new Color(r, g, b);
			}
		}

		colorBuffer.flip();

		// TODO : Fix this
		// renderFinalize();
	}


	private void setViewRenderPreview() {
		copyViewToCamera();

		this.previewWindow.changeType(ViewType.TOP, true);
		this.previewWindow.renderGrid = false;
		this.previewWindow.tabControl = false;

		float scaleFactor = 1.0f * previewWindow.getWidth() / renderWidth / 2;
		this.previewWindow.setScaleFactor(scaleFactor);
		this.previewWindow.setOrthoTarget(new Vector3d(-renderWidth * scaleFactor, -renderHeight * scaleFactor, 0));

		this.previewWindow.geometry = geometryRenderPreview;
	}


	private void setViewScenePreview() {
		this.previewWindow.changeType(ViewType.PERSP, false);
		this.previewWindow.fov = (float) scene.camera.fov;

		this.previewWindow.geometry = geometryScenePreview;
	}


	private void copyViewToCamera() {
		scene.camera.setPosition(previewWindow.getVectorEye());
		scene.camera.setTarget(previewWindow.getVectorTarget());
	}


	private class RenderThread extends Thread {
		private int start;
		private int stop;
		private boolean updateBar = false;
		private int lod;


		public RenderThread(int start, int stop, boolean updateBar, int lod) {
			this.start = start;
			this.stop = stop;
			this.updateBar = updateBar;
			this.lod = lod;
		}


		@Override
		public void run() {
			for (int i = start; i < stop; i++) {
				if (cancelFlag) {
					break;
				}

				int x = xListUnique[lod].get(i);
				int y = yListUnique[lod].get(i);

				Vector3d rayPosition = scene.camera.getPosition();
				Vector3d rayVector = scene.camera.getRayVector(x, y);

				Color c = getSDFRayColor(sdfScene, rayPosition, rayVector);

				for (int j = 0; j < renderLevels; j++) {
					int lx = x / (1 << j);
					int ly = y / (1 << j);
					int li = ly * levelWidth[j] + lx;
					colors[j][li] = c;
				}

				if (updateBar) {
					progressBar.update(1.0f * (i - start) / (stop - start));
				}
			}
		}
	}


	private class RenderEndThread extends Thread {
		private RenderThread[] rts;
		private int lod;


		public RenderEndThread(RenderThread[] rts, int lod) {
			this.rts = rts;
			this.lod = lod;
		}


		@Override
		public void run() {
			// Wait for threads
			for (int i = 0; i < rts.length; i++) {
				try {
					rts[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			if (cancelFlag) {
				return;
			}

			// Update colorBuffer
			ByteBuffer colorBufferTemp = ByteBuffer.allocateDirect(levelWidth[lod] * levelHeight[lod] * 4);
			colorBufferTemp.order(ByteOrder.nativeOrder());

			for (int y = 0; y < levelHeight[lod]; y++) {
				for (int x = 0; x < levelWidth[lod]; x++) {
					int ly = levelHeight[lod] - 1 - y;
					colorBufferTemp.put((byte) Util.clip(colors[lod][ly * levelWidth[lod] + x].r, 0, 255));
					colorBufferTemp.put((byte) Util.clip(colors[lod][ly * levelWidth[lod] + x].g, 0, 255));
					colorBufferTemp.put((byte) Util.clip(colors[lod][ly * levelWidth[lod] + x].b, 0, 255));
					colorBufferTemp.put((byte) 255);
				}
			}
			colorBufferTemp.flip();
			colorBuffer = colorBufferTemp;

			performFinalize = true;
			lastLevel = lod;
		}
	}


	@Override
	protected void keyPressed(int key) {
		controllerManager.keyPressed(key);
	}


	@Override
	protected void textInput(char character) {
		controllerManager.textInput(character);
	}


	@Override
	protected void mouseWheel(float amt) {
		// TODO Auto-generated method stub

	}


	@Override
	protected void mousePressed(int button, int mouseX, int mouseY) {
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

		else if (controller == buttonCancel) {
			cancelFlag = true;
			progressBar.update(0);
			setViewScenePreview();
		}

		else if (controller == buttonResult) {

		}

		else if (controller == buttonRender2D) {
			render2DSlice(sdfScene, 15.99);
		}

		else if (controller == cameraPositionX) {
			UIETextField tf = (UIETextField) controller;
			Vector3d pos = scene.camera.getPosition();
			try {
				pos.x = Float.parseFloat(tf.getValue());
			} catch (Exception e) {
				tf.setValueSilent(pos.x + "");
			}
			scene.camera.setPosition(pos);
		}

		else if (controller == cameraPositionY) {
			UIETextField tf = (UIETextField) controller;
			Vector3d pos = scene.camera.getPosition();
			try {
				pos.y = Float.parseFloat(tf.getValue());
			} catch (Exception e) {
				tf.setValueSilent(pos.y + "");
			}
			scene.camera.setPosition(pos);
		}

		else if (controller == cameraPositionZ) {
			UIETextField tf = (UIETextField) controller;
			Vector3d pos = scene.camera.getPosition();
			try {
				pos.z = Float.parseFloat(tf.getValue());
			} catch (Exception e) {
				tf.setValueSilent(pos.z + "");
			}
			scene.camera.setPosition(pos);
		}

	}


	private void loadFile(String filename) {
		System.out.println(filename);
	}


	private void updateCameraLabels() {
		Vector3d cameraPosition = scene.camera.getPosition();
		Vector3d cameraTarget = scene.camera.getTarget();

		cameraPositionX.setValueSilent(cameraPosition.x + "");
		cameraPositionY.setValueSilent(cameraPosition.y + "");
		cameraPositionZ.setValueSilent(cameraPosition.z + "");

		cameraTargetX.setValueSilent(cameraTarget.x + "");
		cameraTargetY.setValueSilent(cameraTarget.y + "");
		cameraTargetZ.setValueSilent(cameraTarget.z + "");
	}
}
