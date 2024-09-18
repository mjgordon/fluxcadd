package render_sdf.renderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

import console.Console;
import controller.*;
import geometry.Geometry;
import geometry.GeometryDatabase;
import geometry.Group;
import geometry.Line;
import geometry.Rect;
import main.FluxCadd;
import render_sdf.animation.Content_Animation;
import render_sdf.material.Material;
import render_sdf.material.MaterialDiffuse;
import render_sdf.sdf.*;
import scheme.SchemeEnvironment;
import scheme.SourceFile;
import ui.*;
import utility.Color;
import utility.Util;
import utility.UtilString;
import utility.math.Domain;
import utility.math.UtilMath;
import utility.math.UtilVector;

public class Content_Renderer extends Content {

	private UIEControlManager controllerManager;
	private UIEFileChooser fileChooser;
	private UIETextField textfieldSDFObjectList;
	private UIETextField cameraPositionX;
	private UIETextField cameraPositionY;
	private UIETextField cameraPositionZ;
	private UIETextField cameraTargetX;
	private UIETextField cameraTargetY;
	private UIETextField cameraTargetZ;
	private UIELabel finishCounterLabel;
	private UIELabel renderJobLabel;
	private UIEProgressBar progressBar;

	private Content_View previewWindow;

	private Content_Animation animationWindow;

	private Scene scene;

	private SDF sdfScene;

	private ArrayList<SDF> sdfArray;

	private int renderWidth = 1080;
	private int renderHeight = 1080;

	/**
	 * Bytebuffer representation of the rendered colors, converted in a new thread
	 * after all render threads have joined, then not touched until the UI thread
	 * gets back
	 */
	private volatile ByteBuffer colorBuffer;

	/**
	 * Contains all of the render jobs that may have finished since the last main
	 * thread tick
	 */
	private LinkedList<RenderJob> finishedJobs;

	/**
	 * Timestamp for the start of the render
	 */
	private long renderStartTime;

	/**
	 * Most recently completed level of detail
	 */
	private int lastLevel = -1;

	/**
	 * X coordinates of new pixels to be rendered at each level of detail
	 */
	private ArrayList<Integer>[] xListUnique;

	/**
	 * Y Coordinates of new pixels to be rendered at each level of detail
	 */
	private ArrayList<Integer>[] yListUnique;

	private RenderThread[] renderThreads;
	private RenderEndThread renderEndThread;

	private volatile boolean cancelFlag = false;

	private GeometryDatabase geometryScenePreview;
	private GeometryDatabase geometryRenderPreview;

	private boolean cameraLockedToPreview = true;

	private boolean flagRendering = false;

	private int maxDepth = 100;

	private boolean materialPreview = true;

	private boolean autoUpdate = false;

	private int finishCounter = 0;

	private SchemeEnvironment schemeEnvironment;

	private String sdfFilename = "scripts_sdf/demo_chamfer.scm";

	private LinkedList<RenderJob> renderJobs;


	public Content_Renderer(Panel parent, Content_View previewWindow, Content_Animation animationWindow) {
		super(parent);

		geometryScenePreview = new GeometryDatabase();
		geometryRenderPreview = new GeometryDatabase();

		setupControl();

		scene = new Scene(renderWidth, renderHeight);		

		this.previewWindow = previewWindow;
		this.previewWindow.renderGrid = false;
		this.previewWindow.fovDiff = 0.18;

		this.animationWindow = animationWindow;

		resetPreviewGeometry();

		setupSDFFromScript();
		updateSDFFromScript(sdfFilename);
		
		scene.camera.updateMatrix(0);

		setViewScenePreview();

		updateCameraLabels(0);

		setParentWindowTitle("SDF Render");

		renderJobs = new LinkedList<RenderJob>();
		finishedJobs = new LinkedList<RenderJob>();
	}


	@Override
	public void render() {
		double time = (renderJobs.size() > 0) ? renderJobs.getFirst().timestamp : animationWindow.getTime();
		previewWindow.time = time;

		controllerManager.render();

		while (finishedJobs.size() > 0) {
			renderLevelFinalize(finishedJobs.pop());
		}
	}


	@Override
	public void resizeRespond() {
		controllerManager.setWidth(parent.getWidth());
		controllerManager.setHeight(parent.getHeight());
		controllerManager.reflow();
	}



	private void setupSDFFromScript() {
		scene = new Scene(renderWidth, renderHeight);
		schemeEnvironment = new SchemeEnvironment();
		try {
			SourceFile systemSDFFile = new SourceFile("scheme/system-sdf.scm");
			schemeEnvironment.evalSafe(systemSDFFile.fullFile);
		} catch (Exception e) {
			System.out.println(e);
		}
		schemeEnvironment.call("set-scene-render", scene);

	}


	@SuppressWarnings("unused")
	private void setup2DDemo() {
		Material materialMain = new MaterialDiffuse(new Color(0xFF0000), 0);

		sdfScene = new SDFPrimitiveCross(new Vector3d(0, 0, 0), 75, materialMain);
		sdfScene = new SDFOpChamfer(sdfScene, new SDFPrimitiveCube(new Vector3d(200, 0, 0), 200, materialMain), 50);
		sdfScene = new SDFOpFillet(sdfScene, new SDFPrimitiveCube(new Vector3d(-200, 0, 0), 200, materialMain), 100);
	}


	/**
	 * Load an SDF scene by evaluating the .scm file at filename.
	 * 
	 * @param filename
	 */
	private void updateSDFFromScript(String filename) {
		try {
			SourceFile sdfFile = new SourceFile(filename);
			schemeEnvironment.evalSafe(sdfFile.fullFile);
			sdfScene = (SDF) schemeEnvironment.js.eval("scene-sdf");
			copyCameraToView(0);

			resetPreviewGeometry();
			sdfScene.extractSceneGeometry(geometryScenePreview, true, materialPreview, animationWindow.getTime());
			
			Group g = new Group();
			double hp = 10.0;
			Color c = new Color(255,255,0);
			g.add(new Line(new Vector3d(-hp, 0, 0), new Vector3d(hp, 0, 0)).setFillColor(c));
			g.add(new Line(new Vector3d(0, -hp, 0), new Vector3d(0, hp, 0)).setFillColor(c));
			g.add(new Line(new Vector3d(0, 0, -hp), new Vector3d(0, 0, hp)).setFillColor(c));
			g.setFrame(scene.sunPosition);
			geometryScenePreview.add(g);

			this.textfieldSDFObjectList.setValueSilent(sdfScene.describeTree("", 0, "", true));

			sdfArray = sdfScene.getArray();

		} catch (Exception e) {
			Console.log("Scheme SDF Exception: " + e);
		}
	}


	private void resetPreviewGeometry() {
		geometryScenePreview.clear();
		geometryScenePreview.add(scene.camera.getGeometryFirstPerson());
		geometryScenePreview.add(scene.camera.getGeometryThirdPerson());

		scene.camera.getGeometryFirstPerson().visible = true;
		scene.camera.getGeometryThirdPerson().visible = false;
	}


	@SuppressWarnings("unchecked")
	private void renderScene(RenderJob job) {

		renderJobLabel.setText("Render Jobs: " + (renderJobs.size()));

		if (sdfScene == null) {
			Console.log("No SDF Scene Loaded");
			return;
		}

		setViewScenePreview();

		scene.camera.updateMatrix(job.timestamp);
		
		setViewRenderPreview();

		renderStartTime = System.currentTimeMillis();
		cancelFlag = false;
		flagRendering = true;

		job.renderLevels = (int) UtilMath.log2(Math.max(renderWidth, renderHeight)) + 1;

		xListUnique = (ArrayList<Integer>[]) new ArrayList[job.renderLevels];
		yListUnique = (ArrayList<Integer>[]) new ArrayList[job.renderLevels];

		job.levelWidth = new int[job.renderLevels];
		job.levelHeight = new int[job.renderLevels];

		int levelCount[] = new int[job.renderLevels];

		for (int i = 0; i < job.renderLevels; i++) {
			job.levelWidth[i] = 0;
			job.levelHeight[i] = 0;
			levelCount[i] = 0;
			xListUnique[i] = new ArrayList<Integer>();
			yListUnique[i] = new ArrayList<Integer>();
		}

		for (int y = 0; y < renderHeight; y++) {
			for (int x = 0; x < renderWidth; x++) {
				boolean flag = true;
				for (int i = job.renderLevels - 1; i >= 0; i--) {
					int n = 1 << i;
					if (x % n == 0 && y % n == 0) {
						levelCount[i] += 1;

						if (y == 0) {
							job.levelWidth[i] += 1;
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

		job.colors = new Color[job.renderLevels][];

		for (int i = 0; i < job.renderLevels; i++) {
			job.colors[i] = new Color[levelCount[i]];
			job.levelHeight[i] = levelCount[i] / job.levelWidth[i];
		}

		System.out.println("Start Render : " + renderWidth + "x" + renderHeight + " : " + job.renderLevels + " lod");
		System.out.println("Timestamp : " + job.timestamp);

		renderLevel(job, job.renderLevels - 1);
	}


	/**
	 * Start the threads for a single level of detail
	 * 
	 * @param lod
	 */
	private void renderLevel(RenderJob job, int lod) {
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

		int threadDiv = job.colors.length / threadCount;
		renderThreads = new RenderThread[threadCount];

		for (int i = 0; i < threadCount; i++) {
			int start = threadDiv * i;
			int end = (threadDiv * (i + 1));

			if (i == threadCount - 1) {
				end = xListUnique[lod].size();
			}
			renderThreads[i] = new RenderThread(job, start, end, i == renderThreads.length - 1, lod);
			renderThreads[i].start();
		}

		renderEndThread = new RenderEndThread(job, renderThreads, lod);
		renderEndThread.start();
	}


	/**
	 * Finalizes a single level of detail. Called by main UI thread due to flag set
	 * by the RenderEndThread. Assigns the colorBuffer to a texture in the display,
	 * and exports the image if necessary. Binding the texture must apparently be
	 * done here as part of the main thread, as opposed to in the finalization
	 * thread
	 */
	private void renderLevelFinalize(RenderJob job) {
		int imageWidth = job.levelWidth[lastLevel];
		int imageHeight = job.levelHeight[lastLevel];

		int textureId = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, imageWidth, imageHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, colorBuffer);

		previewWindow.geometry.clear();
		previewWindow.geometry.add((Geometry) new Rect(renderWidth, renderHeight, renderWidth, renderHeight, textureId));

		// Either render next level, next frame, or finish
		if (lastLevel > 0) {
			renderLevel(job, lastLevel - 1);
		}
		else {
			long renderEndTime = System.currentTimeMillis();
			System.out.println("Render Time : " + (renderEndTime - renderStartTime) / 1000.0 + " Seconds");
			flagRendering = false;
			progressBar.setDisplayName("Render Progress");
			saveRenderToFile(job);
			// Go to the next frame if necessary

			renderJobs.pop();
			if (renderJobs.size() > 0) {
				renderScene(renderJobs.getFirst());
			}
		}
	}


	private void saveRenderToFile(RenderJob job) {
		BufferedImage bi = new BufferedImage(renderWidth, renderHeight, 3);
		for (int y = 0; y < renderHeight; y++) {
			for (int x = 0; x < renderWidth; x++) {
				Color c = job.colors[0][y * renderWidth + x];
				bi.setRGB(x, y, c.toInt());
			}
		}

		try {
			String appPath = new File(".").getCanonicalPath();
			File outFile;

			if (scene.name == null) {
				outFile = new File(appPath + "\\output\\renders\\" + Util.getTimestamp() + ".png");
			}
			else {
				outFile = new File(appPath + "\\output\\renders_named\\" + scene.name + "\\frames\\" + job.name + ".png");
			}
			new File(outFile.getParent()).mkdirs();

			System.out.println(outFile);
			ImageIO.write(bi, "png", outFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private Color getSDFRayColor(RenderJob job, Vector3d pos, Vector3d vec, int depth) {
		Vector3d hit = rayMarch(job.sdf, pos, vec, null, job.timestamp);
		
		if (hit == null) {
			return (scene.skyColor);
		}
		
		Material material = job.sdf.getMaterial(hit, job.timestamp);

		double multFactor = 1;

		if (job.useNormalShading || job.useShadows || job.useReflectivity) {
			Vector3d normal = sdfScene.getNormal(hit, job.timestamp);
			Vector3d shadowVector = scene.sunPosition.get(job.timestamp).getColumn(3, new Vector3d()).sub(hit).normalize();

			double sunNormalAngle = 1;

			if (job.useReflectivity && material.getReflectivity() > 0 && depth < maxDepth) {
				Vector3d newStart = new Vector3d(normal).mul(0.1).add(hit);
				Color reflectedColor = getSDFRayColor(job, newStart, new Vector3d(normal), depth + 1);
				material.lerpTowards(reflectedColor, material.getReflectivity());
			}

			if (job.useNormalShading) {
				sunNormalAngle = 1 - (normal.angle(shadowVector) / Math.PI);
				multFactor = sunNormalAngle;
			}

			if (job.useShadows) {

				int shadowCount = 0;
				int dirCount = 8;

				Vector3d[] shadowStarts = new Vector3d[dirCount + 1];
				shadowStarts[0] = new Vector3d(normal).mul(0.01).add(hit);
				double shadowRadius = 0.05;

				Matrix4d shadowTransform = UtilVector.getTransformVecVec(new Vector3d(0, 0, 1), normal);
				// getTransformVecVec fails is normal is (0,0,-1), in this case the results can
				// be trivially replaced
				if (!shadowTransform.isFinite()) {
					shadowTransform = UtilVector.getTransformVecVec(new Vector3d(0, 0, -1), normal);
				}
				for (int i = 0; i < dirCount; i++) {
					double n = Math.PI * 2 * i / dirCount;
					double x = Math.cos(n) * shadowRadius;
					double y = Math.sin(n) * shadowRadius;
					Vector3d shadowOffset = new Vector3d(x, y, 0);
					shadowOffset = shadowTransform.transformPosition(shadowOffset);
					shadowOffset.add(shadowStarts[0]);
					shadowStarts[i + 1] = shadowOffset;
				}

				for (int i = 0; i < dirCount + 1; i++) {
					Vector3d shadowCollision = rayMarch(job.sdf, shadowStarts[i], shadowVector, scene.sunPosition.get(job.timestamp).getColumn(3,  new Vector3d()), job.timestamp);
					if (shadowCollision != null) {
						shadowCount += 1;
					}
				}

				multFactor = UtilMath.lerp(sunNormalAngle, scene.ambientLight, 1.0 * shadowCount / (dirCount + 1));
			}
		}
		
		Color output = new Color(0, 0, 0);
		output.set(material.getColor());
		output.mult(multFactor);

		return (output);
	}


	private Vector3d rayMarch(SDF sdf, Vector3d pos, Vector3d vec, Vector3d goalPoint, double time) {
		double distanceDelta = 0;

		while (true) {
			double distance = sdf.getDistance(pos, time);
		
			if (distance <= SDF.epsilon) {
				return (pos);
			}

			double marchDistance = distance * SDF.distanceFactor;
			vec.normalize(marchDistance);
			pos.add(vec);
			distanceDelta += marchDistance;

			// Once ray passes the goalpoint, report no obstacles found
			if (goalPoint != null) {
				if (new Vector3d(goalPoint).sub(pos).dot(vec) < 0) {
					return (null);
				}
			}

			if (distanceDelta > SDF.farClip) {
				return (null);
			}
		}
	}


	/**
	 * Renders a non-marching 2d slice of the scene. Not using threading for now
	 * Currently not used as hasn't been updated for multithreading
	 */
	@Deprecated
	private void render2DSlice(SDF sdf, double z, double time) {
		colorBuffer = ByteBuffer.allocateDirect(renderWidth * renderHeight * 4);
		colorBuffer.order(ByteOrder.nativeOrder());

		float scale = 8;

		for (int y = 0; y < renderHeight; y++) {
			for (int x = 0; x < renderWidth; x++) {
				// int py = renderHeight - 1 - y;

				double lx = (x - (renderWidth / 2.0)) / scale;
				double ly = (y - (renderHeight / 2.0)) / scale;
				Vector3d v = new Vector3d(lx, ly, z);

				double distance = sdf.getDistance(v, time);

				int r = 255 - (int) Math.max(0, Math.min((int) Math.abs(distance) * 10.0, 255));
				int g = (int) Math.max(0, Math.min((int) 0, 255));
				int b = (int) Math.max(0, Math.min((int) distance > 0 ? 0 : 255, 255));

				colorBuffer.put((byte) r);
				colorBuffer.put((byte) g);
				colorBuffer.put((byte) b);
				colorBuffer.put((byte) 255);

				// Color color = new Color(r, g, b);
			}
		}

		colorBuffer.flip();
		// renderFinalize();
	}


	private void setViewRenderPreview() {
		this.previewWindow.changeType(ViewType.TOP, true);
		this.previewWindow.renderGrid = false;

		double scaleFactor = Math.min(0.5 * previewWindow.getWidth() / renderWidth, 0.5 * previewWindow.getHeight() / renderHeight);
		this.previewWindow.setScaleFactor(scaleFactor);
		this.previewWindow.setOrthoTarget(new Vector3d(-renderWidth * scaleFactor, -renderHeight * scaleFactor, 0));

		this.previewWindow.geometry = geometryRenderPreview;
	}


	private void setViewScenePreview() {
		this.previewWindow.changeType(ViewType.PERSP, false);
		this.previewWindow.fov = scene.camera.getFOV();

		this.previewWindow.geometry = geometryScenePreview;
	}


	private void copyCameraToView(double time) {
		previewWindow.setVectorTarget(scene.camera.getTarget(time));
		previewWindow.setVectorEye(scene.camera.getPosition(time));
	}


	/**
	 * Update the UIE labels to match the current camera position and target
	 */
	private void updateCameraLabels(double time) {
		Vector3d cameraPosition = scene.camera.getPosition(time);
		Vector3d cameraTarget = scene.camera.getTarget(time);

		cameraPositionX.setValueSilent(cameraPosition.x + "");
		cameraPositionY.setValueSilent(cameraPosition.y + "");
		cameraPositionZ.setValueSilent(cameraPosition.z + "");

		cameraTargetX.setValueSilent(cameraTarget.x + "");
		cameraTargetY.setValueSilent(cameraTarget.y + "");
		cameraTargetZ.setValueSilent(cameraTarget.z + "");
	}


	private class RenderJob {
		public SDF sdf;

		public double timestamp;
		public String name;

		public boolean useShadows = true;
		public boolean useNormalShading = true;
		public boolean useReflectivity = true;

		/**
		 * Intermediate render data, outer array is each level of detail Direct colors
		 * for each pixel as returned by the raymarcher, later converted to the
		 * ByteBuffer for display or read directly for saving
		 */
		public volatile Color[][] colors;

		/**
		 * Total levels of detail for render process
		 */
		public int renderLevels = -1;

		/**
		 * Dimensions of 'image' at each level of detail
		 */
		public int levelWidth[];
		public int levelHeight[];


		public RenderJob(SDF sdf, double timestamp, String name) {
			this.timestamp = timestamp;
			this.name = name;
			this.sdf = sdf;
		}


		public RenderJob(SDF sdf, double timestamp, String name, boolean useShadows, boolean useNormalShading, boolean useReflectivity) {
			this.timestamp = timestamp;
			this.name = name;
			this.sdf = sdf;

			this.useNormalShading = useNormalShading;
			this.useShadows = useShadows;
			this.useReflectivity = useReflectivity;
		}

	}


	private class RenderThread extends Thread {
		private int start;
		private int stop;
		private boolean updateBar = false;
		private int lod;

		private RenderJob job;


		public RenderThread(RenderJob job, int start, int stop, boolean updateBar, int lod) {
			this.start = start;
			this.stop = stop;
			this.updateBar = updateBar;
			this.lod = lod;
			this.job = job;
		}


		@Override
		public void run() {
			for (int i = start; i < stop; i++) {
				if (cancelFlag) {
					break;
				}

				int x = xListUnique[lod].get(i);
				int y = yListUnique[lod].get(i);

				Vector3d rayPosition = scene.camera.getPosition(job.timestamp);
				Vector3d rayVector = scene.camera.getRayVector(x, y);

				Color c = getSDFRayColor(job, rayPosition, rayVector, 0);

				for (int j = 0; j < job.renderLevels; j++) {
					int lx = x / (1 << j);
					int ly = y / (1 << j);
					int li = ly * job.levelWidth[j] + lx;
					job.colors[j][li] = c;
				}

				if (updateBar) {
					progressBar.update(1.0f * (i - start) / (stop - start));
				}

				finishCounter += 1;
				finishCounterLabel.setText("Finish Counter : " + finishCounter + "");
			}
		}
	}


	private class RenderEndThread extends Thread {
		private RenderThread[] rts;
		private int lod;
		private RenderJob job;


		public RenderEndThread(RenderJob job, RenderThread[] rts, int lod) {
			this.rts = rts;
			this.lod = lod;
			this.job = job;
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
			ByteBuffer colorBufferTemp = ByteBuffer.allocateDirect(job.levelWidth[lod] * job.levelHeight[lod] * 4);
			colorBufferTemp.order(ByteOrder.nativeOrder());

			for (int y = 0; y < job.levelHeight[lod]; y++) {
				for (int x = 0; x < job.levelWidth[lod]; x++) {
					int ly = job.levelHeight[lod] - 1 - y;
					colorBufferTemp.put((byte) UtilMath.clip(job.colors[lod][ly * job.levelWidth[lod] + x].r, 0, 255));
					colorBufferTemp.put((byte) UtilMath.clip(job.colors[lod][ly * job.levelWidth[lod] + x].g, 0, 255));
					colorBufferTemp.put((byte) UtilMath.clip(job.colors[lod][ly * job.levelWidth[lod] + x].b, 0, 255));
					colorBufferTemp.put((byte) 255);
				}
			}
			colorBufferTemp.flip();
			colorBuffer = colorBufferTemp;

			finishedJobs.add(job);

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
		controllerManager.mouseWheel((int) amt);
	}


	@Override
	protected void mousePressed(int button, int mouseX, int mouseY) {
		if (button == 0) {
			controllerManager.poll(mouseX, mouseY);
		}
	}


	@Override
	protected void mouseDragged(int button, int dx, int dy) {
		controllerManager.mouseDragged(button, dx, dy);
	}


	@Override
	protected void mouseReleased(int button) {
		controllerManager.mouseReleased();
	}


	private void setupControl() {
		controllerManager = new UIEControlManager(getWidth(), getHeight(), 10, 30, 10, 10);

		controllerManager.add(new UIEToggle(null, "autoupdate", "Auto-Update", 0, 0, 20, 20).setCallback((toggle) -> {
			autoUpdate = toggle.state;
			// TODO: Implement autoupdate
		}));

		controllerManager.add(new UIEButton(null, "update_manual", "Update", 0, 0, 20, 20).setCallback((button) -> {
			updateSDFFromScript(sdfFilename);
		}));

		controllerManager.newLine();

		fileChooser = new UIEFileChooser(null, "fileChooser", "File Chooser", 0, 0, -1, 20, controllerManager, true, false).setCallback((fc) -> {
			String filename = fc.getCurrentString();
			// TODO: Tie this into rest of scheme system
		});
		controllerManager.add(fileChooser);

		controllerManager.newLine();

		textfieldSDFObjectList = new UIETextField(null, "sdf_object_list", "SDF Objects", 0, 0, -1, 200).setClearOnExecute(false).setCallback((tf) -> {
			int selectedLine = tf.getSelectedLine();
			if (selectedLine < sdfArray.size()) {
				animationWindow.setAnimated(sdfArray.get(selectedLine).getAnimated());
			}

		});
		textfieldSDFObjectList.setValueSilent("abcdefghijklmnopqrs\ntuvwxyz0123456789.,/_-()");
		textfieldSDFObjectList.editable = false;
		controllerManager.add(textfieldSDFObjectList);

		controllerManager.newLine();

		{
			UIEVerticalStack stackPosition = new UIEVerticalStack(null, "stack_position", "", 0, 0, 120, 0);
			stackPosition.add(new UIELabel(null, "camera_position_label", "Camera Position", 0, 0, 100, 20));
			cameraPositionX = new UIETextField(null, "camera_position_x", "X", 0, 0, 100, 20).setClearOnExecute(false).setCallback((tf) -> {
				Vector3d pos = scene.camera.getPosition(0);
				try {
					pos.x = Double.parseDouble(tf.getValue());
				} catch (Exception e) {
					tf.setValueSilent(pos.x + "");
				}
				scene.camera.setPositionKeyframe(0, pos);
			});
			stackPosition.add(cameraPositionX);

			cameraPositionY = new UIETextField(null, "camera_position_y", "Y", 0, 0, 100, 20).setClearOnExecute(false).setCallback((tf) -> {
				Vector3d pos = scene.camera.getPosition(0);
				try {
					pos.y = Double.parseDouble(tf.getValue());
				} catch (Exception e) {
					tf.setValueSilent(pos.y + "");
				}
				scene.camera.setPositionKeyframe(0,pos);
			});
			stackPosition.add(cameraPositionY);

			cameraPositionZ = new UIETextField(null, "camera_position_y", "Z", 0, 0, 100, 20).setClearOnExecute(false).setCallback((tf) -> {
				Vector3d pos = scene.camera.getPosition(0);
				try {
					pos.z = Float.parseFloat(tf.getValue());
				} catch (Exception e) {
					tf.setValueSilent(pos.z + "");
				}
				scene.camera.setPositionKeyframe(0,pos);
			});
			stackPosition.add(cameraPositionZ);
			stackPosition.close();
			controllerManager.add(stackPosition);
		}

		{
			UIEVerticalStack stackTarget = new UIEVerticalStack(null, "stack_target", "", 0, 0, 120, 0);
			stackTarget.add(new UIELabel(null, "camera_target_label", "Camera Target", 0, 0, 100, 20));
			cameraTargetX = new UIETextField(null, "camera_target_x", "X", 0, 0, 100, 20).setClearOnExecute(false);
			stackTarget.add(cameraTargetX);
			cameraTargetY = new UIETextField(null, "camera_target_y", "Y", 0, 0, 100, 20).setClearOnExecute(false);
			stackTarget.add(cameraTargetY);
			cameraTargetZ = new UIETextField(null, "camera_target_y", "Z", 0, 0, 100, 20).setClearOnExecute(false);
			stackTarget.add(cameraTargetZ);
			stackTarget.close();
			controllerManager.add(stackTarget);
		}

		{
			UIEVerticalStack stackLock = new UIEVerticalStack(null, "stack_lock", "", 0, 0, 120, 0);
			stackLock.add(new UIELabel(null, "camera_lock_label", "Camera Sync", 0, 0, 100, 20));
			stackLock.add(new UIEButton(null, "button_preview_to_cam", "Copy Preview to Camera", 0, 0, 20, 20).setCallback((button) -> {
				FluxCadd.backend.forceRedraw = true;
			}));
			stackLock.add(new UIEButton(null, "button_cam_to_preview", "Copy Camera to Preview", 0, 0, 20, 20).setCallback((button) -> {
				copyCameraToView(0);
				FluxCadd.backend.forceRedraw = true;
			}));
			stackLock.add(new UIEToggle(null, "toggle_lock_cam", "Lock Camera Preview", 0, 0, 20, 20).setCallback((toggle) -> {
				cameraLockedToPreview = toggle.state;

				scene.camera.getGeometryFirstPerson().visible = cameraLockedToPreview;
				scene.camera.getGeometryThirdPerson().visible = !cameraLockedToPreview;

				FluxCadd.backend.forceRedraw = true;
			}));
			stackLock.close();
			controllerManager.add(stackLock);
		}

		controllerManager.newLine();

		UIEToggle toggleReflectivity = new UIEToggle(null, "t_reflectivity", "Reflectivity", 0, 0, 20, 20);
		UIEToggle toggleShadow = new UIEToggle(null, "t_shadow", "Shadow", 0, 0, 20, 20);
		UIEToggle toggleShading = new UIEToggle(null, "t_shading", "Shading", 0, 0, 20, 20);

		UIEButton buttonRender = new UIEButton(null, "button_render", "Render", 0, 0, 20, 20).setCallback((button) -> {
			renderJobs.add(new RenderJob(sdfScene, animationWindow.getTime(), "s" + UtilString.leftPad((int) animationWindow.getTime() + "", 5), 
					toggleShadow.state, toggleShading.state, toggleReflectivity.state));
			renderScene(renderJobs.getFirst());
		});
		controllerManager.add(buttonRender);

		UIEButton buttonCancel = new UIEButton(null, "button_cancel", "Cancel", 0, 0, 20, 20).setCallback((button) -> {
			cancelFlag = true;
			flagRendering = false;
			progressBar.update(0);
			setViewScenePreview();
			renderJobs.clear();
			
		});
		controllerManager.add(buttonCancel);

		UIEButton buttonResult = new UIEButton(null, "button_result", "Result", 0, 0, 20, 20);
		controllerManager.add(buttonResult);

		UIEButton buttonRender2D = new UIEButton(null, "button_render_2d", "Render 2D", 0, 0, 20, 20).setCallback((button) -> {
			render2DSlice(sdfScene, 15.99, 0);
		});
		controllerManager.add(buttonRender2D);

		controllerManager.newLine();

		UIETextField frameStart = new UIETextField(null, "animation_frame_start", "Frame Start", 0, 0, 100, 20,1, new Domain(0, 1000), 1);
		UIETextField frameEnd = new UIETextField(null, "animation_frame_end", "Frame End", 0, 0, 100, 20, 480, new Domain(0, 1000), 1);

		UIEButton buttonRenderAnimation = new UIEButton(null, "button_render_animation", "Render Animation", 0, 0, 20, 20).setCallback((button) -> {
			int start = (int) frameStart.getBackingDouble();
			int end = (int) frameEnd.getBackingDouble();

			for (int i = start; i < end; i++) {
				renderJobs.add(new RenderJob(sdfScene, i, UtilString.leftPad(i + "", 5)));
			}
			renderScene(renderJobs.getFirst());
		});
		controllerManager.add(buttonRenderAnimation);
		controllerManager.add(frameStart);
		controllerManager.add(frameEnd);

		controllerManager.newLine();

		finishCounterLabel = new UIELabel(null, "finish_counter", "Finish Counter : ", 0, 0, 250, 20);
		controllerManager.add(finishCounterLabel);

		renderJobLabel = new UIELabel(null, "render_job_counter", "Render Jobs : ", 0, 0, 100, 20);
		controllerManager.add(renderJobLabel);

		controllerManager.newLine();

		progressBar = new UIEProgressBar(null, "progress_bar", "Render Progress", 0, 0, -1, 20, 1.0f);
		controllerManager.add(progressBar);
		controllerManager.newLine();

		controllerManager.newLine();

		{
			UIEVerticalStack stackFOV = new UIEVerticalStack(null, "stack_fov", "", 0, 0, 120, 0);
			stackFOV.add(new UIELabel(null, "fov_label", "FOV", 0, 0, 100, 20));
			stackFOV.add(new UIETextField(null, "camera_fov", "Camera FOV", 0, 0, 100, 20, 45, new Domain(0, 180), 1).setClearOnExecute(false).setCallback((tf) -> {
				scene.camera.setFOV(Math.toRadians(tf.getBackingDouble()));
				scene.camera.updateGeometry(animationWindow.getTime());
				previewWindow.fov = scene.camera.getFOV();
			}));
			stackFOV.add(new UIETextField(null, "scene_fov", "Preview FOV Offset", 0, 0, 100, 20, 0.18, new Domain(0, 1), 0.01).setClearOnExecute(false).setCallback((tf) -> {
				previewWindow.fovDiff = tf.getBackingDouble();
			}));

			stackFOV.close();
			controllerManager.add(stackFOV);
		}

		controllerManager.add(toggleReflectivity);
		controllerManager.add(toggleShadow);
		controllerManager.add(toggleShading);

		controllerManager.finalizeLayer();
	}

}
