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
import event.EventListener;
import event.EventMessage;
import geometry.Geometry;
import geometry.GeometryDatabase;
import geometry.Rect;
import render_sdf.material.Material;
import render_sdf.sdf.*;
import ui.*;
import utility.Color;
import utility.Util;
import utility.math.Domain;
import utility.math.UtilMath;
import utility.math.UtilVector;

public class Content_Renderer extends Content implements EventListener {

	private UIEControlManager controllerManager;
	private UIEFileChooser fileChooser;
	private UIETextField textfieldSDFObjectList;
	private UIETextField cameraPositionX;
	private UIETextField cameraPositionY;
	private UIETextField cameraPositionZ;
	private UIETextField cameraTargetX;
	private UIETextField cameraTargetY;
	private UIETextField cameraTargetZ;
	private UIEProgressBar progressBar;

	private Content_View previewWindow;

	private Scene scene;

	private SDF sdfScene;

	private int renderWidth = 1080;
	private int renderHeight = 1080;

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

	private boolean cameraLockedToPreview = true;

	private boolean flagRendering = false;

	private int maxDepth = 100;

	private boolean materialPreview = true;


	public Content_Renderer(Panel parent, Content_View previewWindow) {
		super(parent);

		geometryScenePreview = new GeometryDatabase();
		geometryRenderPreview = new GeometryDatabase();

		setupControl();

		// setupSDFDemoMain();
		// setupSDFDemoCross();
		// setupSDFDemoMollusk();
		// setupSDFDemoAquaduct();
		setupSDFDemoTorus();
		//setupSDFDemoCube();
		// setup2DDemo();

		this.previewWindow = previewWindow;
		this.previewWindow.renderGrid = false;
		this.previewWindow.register(this);
		this.previewWindow.fovDiff = 0.18;
		setViewScenePreview();

		updateCameraLabels();

		geometryScenePreview.add(scene.camera.getGeometryFirstPerson());

		setParentWindowTitle("SDF Render");

		copyViewToCamera();
	}


	@Override
	public void render() {
		controllerManager.render();

		if (performFinalize) {
			renderLevelFinalize();
		}
	}


	@Override
	public void resizeRespond() {
		controllerManager.setWidth(parent.getWidth());
		controllerManager.reflow();
	}


	@Override
	public void message(EventMessage em) {
		if (em instanceof ViewEvent) {
			ViewEvent ve = (ViewEvent) em;
			if (ve.type == ViewEvent.ViewEventType.MOUSE_DRAGGED || ve.type == ViewEvent.ViewEventType.MOUSE_WHEEL) {
				if (cameraLockedToPreview && !flagRendering) {
					copyViewToCamera();
				}
			}
		}
	}


	@SuppressWarnings("unused")
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
		sdfScene.extractSceneGeometry(geometryScenePreview, true, materialPreview);
		geometryScenePreview.add(scene.camera.getGeometryThirdPerson());
	}


	@SuppressWarnings("unused")
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
		sdfScene.extractSceneGeometry(geometryScenePreview, true, materialPreview);
	}


	@SuppressWarnings("unused")
	private void setupSDFDemoMollusk() {
		scene = new Scene(renderWidth, renderHeight);
		scene.camera.setPosition(new Vector3d(100, -100, 30));
		scene.camera.setTarget(new Vector3d(0, 0, -10));

		Material materialGround = new Material(new Color(0xDDBEA8), 0);
		// Material materialSphere = new Material(new Color(0x246A73), 0);
		Material materialSphere = new Material(new Color(0xf21395), 0);

		sdfScene = new SDFPrimitiveGroundPlane(0, materialGround);

		sdfScene = new SDFOpSubtract(sdfScene, new SDFPrimitiveSimplex(materialGround, 0.05), 10);

		SDF sdfMollusk = null;

		for (int i = 0; i < 10; i++) {
			Vector3d vSphere = new Vector3d(0, i * (5 - (i * 0.2)), 5 + (i * 0.7));

			double size = 10 - i;

			if (i % 2 == 0) {
				if (sdfMollusk == null) {
					sdfMollusk = new SDFPrimitiveSphere(vSphere, size, materialSphere);
				}
				else {
					sdfMollusk = new SDFOpSmooth(sdfMollusk, new SDFPrimitiveSphere(vSphere, size, materialSphere), 2);
				}

			}
			else {
				sdfMollusk = new SDFBoolDifference(sdfMollusk, new SDFPrimitiveSphere(vSphere, size, materialSphere));
			}
		}
		// sdfMollusk = new SDFOpModulo(sdfMollusk, 50);

		sdfScene = new SDFOpSmooth(sdfScene, sdfMollusk, 2);

		geometryScenePreview.clear();
		sdfScene.extractSceneGeometry(geometryScenePreview, true, materialPreview);
	}


	@SuppressWarnings("unused")
	private void setupSDFDemoAquaduct() {
		Material materialGround = new Material(new Color(0x444455), 0);
		Material materialColumns = new Material(new Color(0xEEEEDD), 0);

		scene = new Scene(renderWidth, renderHeight);
		scene.camera.setPosition(new Vector3d(100, -100, 30));
		scene.camera.setTarget(new Vector3d(0, 0, -10));
		scene.sunPosition = new Vector3d(25, 25, 25);

		sdfScene = new SDFPrimitiveGroundPlane(0, materialGround);

		SDF sdfColumns = new SDFPrimitiveCross(new Vector3d(0, 0, 30), 1, materialColumns);
		sdfColumns = new SDFOpModulo(sdfColumns, 50);

		sdfColumns = new SDFOpSubtract(sdfColumns, new SDFPrimitiveSimplex(materialGround, 0.3), 0.5);

		// sdfScene = new SDFBoolUnion(sdfScene, sdfColumns);
		sdfScene = new SDFOpSmooth(sdfScene, sdfColumns, 10);

		geometryScenePreview.clear();
		sdfScene.extractSceneGeometry(geometryScenePreview, true, materialPreview);
	}


	@SuppressWarnings("unused")
	private void setupSDFDemoTorus() {
		Material materialGround = new Material(new Color(0x444455), 0);
		Material materialTorus = new Material(new Color(0xEEEEDD), 0);
		Material materialSphere = new Material(new Color(0xFF0000), 0);

		scene = new Scene(renderWidth, renderHeight);
		scene.camera.setPosition(new Vector3d(100, -100, 30));
		scene.camera.setTarget(new Vector3d(0, 0, -10));

		sdfScene = new SDFPrimitiveGroundPlane(0, materialGround);

		Matrix4d torusFrame = new Matrix4d();
		torusFrame.m32(20);
		// torusFrame.m32(20).m00(2);

		sdfScene = new SDFBoolUnion(sdfScene, new SDFPrimitiveTorus(torusFrame, 50, 10, materialTorus));
		sdfScene = new SDFBoolUnion(sdfScene, new SDFPrimitiveCross(new Vector3d(0, 0, 0), 1, materialTorus));

		sdfScene = new SDFBoolUnion(sdfScene, new SDFPrimitiveCross(new Vector3d(0, 0, 10), 2, materialSphere));
		sdfScene = new SDFBoolUnion(sdfScene, new SDFPrimitiveCross(new Vector3d(10, 0, 10), 2, materialSphere));
		sdfScene = new SDFBoolUnion(sdfScene, new SDFPrimitiveCross(new Vector3d(20, 0, 10), 2, materialSphere));
		sdfScene = new SDFBoolUnion(sdfScene, new SDFPrimitiveCross(new Vector3d(30, 0, 10), 2, materialSphere));
		sdfScene = new SDFBoolUnion(sdfScene, new SDFPrimitiveCross(new Vector3d(40, 0, 10), 2, materialSphere));
		sdfScene = new SDFBoolUnion(sdfScene, new SDFPrimitiveCross(new Vector3d(50, 0, 10), 2, materialSphere));

		geometryScenePreview.clear();
		sdfScene.extractSceneGeometry(geometryScenePreview, true, materialPreview);
	}


	private void setupSDFDemoCube() {
		Material materialGround = new Material(new Color(0x444455), 0);
		Material materialCube = new Material(new Color(0xFF0000), 0);

		scene = new Scene(renderWidth, renderHeight);
		scene.camera.setPosition(new Vector3d(100, -100, 30));
		scene.camera.setTarget(new Vector3d(0, 0, -10));

		sdfScene = new SDFPrimitiveGroundPlane(0, materialGround);

		sdfScene = new SDFBoolUnion(sdfScene, new SDFPrimitiveCube(new Vector3d(0,0,20), 20, materialCube));

		geometryScenePreview.clear();
		sdfScene.extractSceneGeometry(geometryScenePreview, true, materialPreview);
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
		setViewScenePreview();

		if (cameraLockedToPreview) {
			copyViewToCamera();
		}
		setViewRenderPreview();

		renderStartTime = System.currentTimeMillis();
		cancelFlag = false;
		flagRendering = true;

		renderLevels = (int) UtilMath.log2(Math.max(renderWidth, renderHeight)) + 1;

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
			flagRendering = false;
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


	private Color getSDFRayColor(SDF sdf, Vector3d pos, Vector3d vec, int depth) {
		Color output = new Color(0, 0, 0);

		Material material = new Material(null, 0);

		Vector3d hit = rayMarch(sdf, pos, vec, material, null);

		if (hit == null) {
			return (scene.skyColor);
		}

		Vector3d normal = sdfScene.getNormal(hit);

		if (material.reflectivity > 0 && depth < maxDepth) {
			Vector3d newStart = new Vector3d(normal).mul(0.1).add(hit);
			Color reflectedColor = getSDFRayColor(sdf, newStart, new Vector3d(normal), depth + 1);
			material.diffuseColor.set(Color.lerpColor(material.diffuseColor, reflectedColor, material.reflectivity));
		}

		Vector3d shadowVector = new Vector3d(scene.sunPosition).sub(hit).normalize();
		double angle = 1 - (normal.angle(shadowVector) / Math.PI);

		int shadowCount = 0;
		int dirCount = 8;

		Vector3d[] shadowStarts = new Vector3d[dirCount + 1];
		shadowStarts[0] = new Vector3d(normal).mul(0.01).add(hit);
		double shadowRadius = 0.05;

		Matrix4d shadowTransform = UtilVector.getTransformVecVec(new Vector3d(0, 0, 1), normal);
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
			Vector3d shadowCollision = rayMarch(sdf, shadowStarts[i], shadowVector, material.copy(), scene.sunPosition);
			if (shadowCollision != null) {
				shadowCount += 1;
			}
		}

		double multFactor = UtilMath.lerp(angle, scene.ambientLight, 1.0 * shadowCount / (dirCount + 1));
		output.set(material.diffuseColor);
		output.mult(multFactor);

		return (output);
	}


	private Vector3d rayMarch(SDF sdf, Vector3d pos, Vector3d vec, Material material, Vector3d goalPoint) {
		double farClip = 5000;
		double distanceDelta = 0;

		while (true) {
			DistanceData distanceData = sdf.getDistance(pos);
			material.set(distanceData.material);

			if (distanceData.distance <= SDF.epsilon) {
				return (pos);
			}

			double marchDistance = distanceData.distance * SDF.distanceFactor;
			vec.normalize(marchDistance);
			pos.add(vec);
			distanceDelta += marchDistance;

			if (goalPoint != null) {
				Vector3d gpDiff = new Vector3d(goalPoint).sub(pos);
				if (gpDiff.dot(vec) < 0) {
					return (null);
				}
			}

			if (distanceDelta > farClip) {
				return (null);
			}
		}
	}


	/**
	 * Renders a non-marching 2d slice of the scene. Not using threading for now
	 * Currently not used as hasn't been updated for multithreading
	 */
	@Deprecated
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


	private void copyViewToCamera() {
		scene.camera.setPosition(previewWindow.getVectorEye());
		scene.camera.setTarget(previewWindow.getVectorTarget());
		updateCameraLabels();
	}


	private void copyCameraToView() {
		previewWindow.setVectorEye(scene.camera.getPosition());
		previewWindow.setVectorTarget(scene.camera.getTarget());
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

				Color c = getSDFRayColor(sdfScene, rayPosition, rayVector, 0);

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
					colorBufferTemp.put((byte) UtilMath.clip(colors[lod][ly * levelWidth[lod] + x].r, 0, 255));
					colorBufferTemp.put((byte) UtilMath.clip(colors[lod][ly * levelWidth[lod] + x].g, 0, 255));
					colorBufferTemp.put((byte) UtilMath.clip(colors[lod][ly * levelWidth[lod] + x].b, 0, 255));
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
	}


	@Override
	protected void mousePressed(int button, int mouseX, int mouseY) {
		if (button == 0) {
			controllerManager.poll(mouseX, mouseY);
		}
	}


	@Override
	protected void mouseDragged(int dx, int dy) {
		controllerManager.mouseDragged(dx, dy);
	}


	@Override
	protected void mouseReleased(int button) {
		controllerManager.mouseReleased();
	}


	private void setupControl() {
		controllerManager = new UIEControlManager(getWidth(), getHeight(), 10, 30, 10, 10);

		fileChooser = new UIEFileChooser(null, "fileChooser", "File Chooser", 0, 0, -1, 20, controllerManager, true, false).setCallback((fc) -> {
			String filename = fc.getCurrentString();
			loadFile(filename);
		});
		controllerManager.add(fileChooser);

		controllerManager.newLine();

		textfieldSDFObjectList = new UIETextField(null, "sdf_object_list", "SDF Objects", 0, 0, -1, 200);
		textfieldSDFObjectList.currentString = "abcdefghijklmnopqrs\ntuvwxyz0123456789.,/_-()";
		controllerManager.add(textfieldSDFObjectList);

		controllerManager.newLine();

		{
			UIEVerticalStack stackPosition = new UIEVerticalStack(null, "stack_position", "", 0, 0, 120, 0);
			stackPosition.add(new UIELabel(null, "camera_position_label", "Camera Position", 0, 0, 100, 20));
			cameraPositionX = new UIETextField(null, "camera_position_x", "X", 0, 0, 100, 20).setClearOnExecute(false).setCallback((tf) -> {
				Vector3d pos = scene.camera.getPosition();
				try {
					pos.x = Double.parseDouble(tf.getValue());
				} catch (Exception e) {
					tf.setValueSilent(pos.x + "");
				}
				scene.camera.setPosition(pos);
			});
			stackPosition.add(cameraPositionX);
			cameraPositionY = new UIETextField(null, "camera_position_y", "Y", 0, 0, 100, 20).setClearOnExecute(false).setCallback((tf) -> {
				Vector3d pos = scene.camera.getPosition();
				try {
					pos.y = Double.parseDouble(tf.getValue());
				} catch (Exception e) {
					tf.setValueSilent(pos.y + "");
				}
				scene.camera.setPosition(pos);
			});
			stackPosition.add(cameraPositionY);
			cameraPositionZ = new UIETextField(null, "camera_position_y", "Z", 0, 0, 100, 20).setClearOnExecute(false).setCallback((tf) -> {
				Vector3d pos = scene.camera.getPosition();
				try {
					pos.z = Float.parseFloat(tf.getValue());
				} catch (Exception e) {
					tf.setValueSilent(pos.z + "");
				}
				scene.camera.setPosition(pos);
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
			stackLock.add(new UIEButton(null, "button_preview_to_cam", "Preview to Camera", 0, 0, 20, 20).setCallback((button) -> {
				copyViewToCamera();
			}));
			stackLock.add(new UIEButton(null, "button_cam_to_preview", "Camera to Preview", 0, 0, 20, 20).setCallback((button) -> {
				copyCameraToView();
			}));
			stackLock.add(new UIEToggle(null, "toggle_lock_cam", "Lock Camera Preview", 0, 0, 20, 20).setCallback((toggle) -> {
				cameraLockedToPreview = toggle.state;

				if (cameraLockedToPreview) {
					geometryScenePreview.add(scene.camera.getGeometryThirdPerson());
				}
				else {

				}
			}));
			stackLock.close();
			controllerManager.add(stackLock);
		}

		controllerManager.newLine();

		UIEButton buttonRender = new UIEButton(null, "button_render", "Render", 0, 0, 20, 20).setCallback((button) -> {
			renderScene();
		});
		controllerManager.add(buttonRender);

		UIEButton buttonCancel = new UIEButton(null, "button_cancel", "Cancel", 0, 0, 20, 20).setCallback((button) -> {
			cancelFlag = true;
			flagRendering = false;
			progressBar.update(0);
			setViewScenePreview();
		});
		controllerManager.add(buttonCancel);

		UIEButton buttonResult = new UIEButton(null, "button_result", "Result", 0, 0, 20, 20);
		controllerManager.add(buttonResult);

		controllerManager.newLine();

		progressBar = new UIEProgressBar(null, "progress_bar", "Render Progress", 0, 0, -1, 20, 1.0f);
		controllerManager.add(progressBar);
		controllerManager.newLine();

		UIEButton buttonRender2D = new UIEButton(null, "button_render_2d", "Render 2D", 0, 0, 20, 20).setCallback((button) -> {
			render2DSlice(sdfScene, 15.99);
		});
		controllerManager.add(buttonRender2D);

		controllerManager.newLine();

		{
			UIEVerticalStack stackFOV = new UIEVerticalStack(null, "stack_fov", "", 0, 0, 120, 0);
			stackFOV.add(new UIELabel(null, "fov_label", "FOV", 0, 0, 100, 20));
			stackFOV.add(new UIETextField(null, "camera_fov", "Camera FOV", 0, 0, 100, 20, 45, new Domain(0, 180), 1).setClearOnExecute(false).setCallback((tf) -> {
				scene.camera.setFOV(Math.toRadians(tf.getBackingDouble()));
				scene.camera.updateGeometry();
				previewWindow.fov = scene.camera.getFOV();
			}));
			stackFOV.add(new UIETextField(null, "scene_fov", "Preview FOV Offset", 0, 0, 100, 20, 0.18, new Domain(0, 1), 0.01).setClearOnExecute(false).setCallback((tf) -> {
				previewWindow.fovDiff = tf.getBackingDouble();
			}));

			stackFOV.close();
			controllerManager.add(stackFOV);
		}

		controllerManager.finalizeLayer();
	}

}
