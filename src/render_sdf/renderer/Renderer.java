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

import render_sdf.material.Material;
import render_sdf.sdf.SDF;
import utility.Color3i;
import utility.Util;
import utility.math.UtilMath;
import utility.math.UtilVector;

import org.lwjgl.opengl.GL11;

import geometry.Geometry;
import geometry.GeometryDatabase;
import geometry.Rect;
import main.FluxCadd;


/**
 * Contains functionality for performing SDF rendering
 *
 */
public class Renderer {

	private LinkedList<RenderJob> renderJobs;

	/**
	 * Contains all of the render jobs that may have finished since the last main
	 * thread tick
	 */
	private LinkedList<RenderJob> finishedJobs;

	private RenderThread[] renderThreads;

	/**
	 * Most recently completed level of detail
	 */
	private int lastLevel = -1;

	
	private volatile boolean cancelFlag = false;

	private boolean flagRendering = false;

	/**
	 * Bytebuffer representation of the rendered colors, converted in a new thread
	 * after all render threads have joined, then not touched until the UI thread
	 * gets back
	 */
	private volatile ByteBuffer colorBuffer;

	/**
	 * Maximum number of reflections before a ray stops
	 */
	private int maxDepth = 100;

	
	private GeometryDatabase previewWindowGeometry;
	
	
	private int threadCount = -1;
	
	
	private int currentLOD = -1;


	public Renderer(GeometryDatabase previewWindowGeometry) {
		renderJobs = new LinkedList<RenderJob>();
		finishedJobs = new LinkedList<RenderJob>();
		
		this.previewWindowGeometry = previewWindowGeometry;
	}


	public void addJob(SDF sdf, Scene scene, double timestamp, String name, RenderSettings settings, boolean inAnimation) {
		RenderJob job = new RenderJob(sdf, scene, timestamp, name, settings, inAnimation);
		renderJobs.add(job);
	}


	public void startRenderingJobs() {
		renderJob(renderJobs.getFirst());
	}


	public void cancelRendering() {
		cancelFlag = true;
		flagRendering = false;
		renderJobs.clear();
		FluxCadd.animating = false;
	}


	public void finalizeLevels() {
		while (finishedJobs.size() > 0) {
			renderLevelFinalize(finishedJobs.pop());
		}
	}


	public int getJobCount() {
		return renderJobs.size();
	}


	public int getFinishCount() {
		if (renderJobs.size() > 0) {
			return renderJobs.getFirst().finishCounter;
		}
		else {
			return 0;
		}
		
	}


	public double getCurrentJobTime() {
		if (renderJobs.size() > 0) {
			return renderJobs.getFirst().timestamp;
		}
		else {
			return Double.NaN;
		}
	}
	
	
	public int getCurrentJobPixelCount() {
		if (renderJobs.size() == 0) {
			return 0;
		}
		RenderJob job = renderJobs.get(0);
		return job.getWidth() * job.getHeight();
	}
	
	
	public int getCurrentJobResolutionWidth() {
		if (renderJobs.size() == 0) {
			return 0;
		}
		return renderJobs.get(0).getWidth();
	}
	
	
	public int getCurrentJobResolutionHeight() {
		if (renderJobs.size() == 0) {
			return 0;
		}
		return renderJobs.get(0).getHeight();
	}

	
	public int getCurrentThreadCount() {
		return threadCount;
	}
	
	
	public int getCurrentLOD() {
		return currentLOD;
	}
	
	
	public boolean isRendering() {
		return flagRendering;
	}


	/**
	 * Starts renderering a single job (frame). 
	 * @param job
	 */
	@SuppressWarnings("unchecked")
	private void renderJob(RenderJob job) {
		job.renderStartTime = System.currentTimeMillis();
		cancelFlag = false;
		flagRendering = true;
		FluxCadd.animating = true;

		job.renderLevels = (int) UtilMath.log2(Math.max(job.getWidth(), job.getHeight())) + 1;

		job.xListUnique = (ArrayList<Integer>[]) new ArrayList[job.renderLevels];
		job.yListUnique = (ArrayList<Integer>[]) new ArrayList[job.renderLevels];

		job.levelWidth = new int[job.renderLevels];
		job.levelHeight = new int[job.renderLevels];

		int levelCount[] = new int[job.renderLevels];

		for (int i = 0; i < job.renderLevels; i++) {
			job.levelWidth[i] = 0;
			job.levelHeight[i] = 0;
			levelCount[i] = 0;
			job.xListUnique[i] = new ArrayList<Integer>();
			job.yListUnique[i] = new ArrayList<Integer>();
		}

		for (int y = 0; y < job.getHeight(); y++) {
			for (int x = 0; x < job.getWidth(); x++) {
				boolean flag = true;
				for (int i = job.renderLevels - 1; i >= 0; i--) {
					int n = 1 << i;
					if (x % n == 0 && y % n == 0) {
						levelCount[i] += 1;

						if (y == 0) {
							job.levelWidth[i] += 1;
						}

						if (flag) {
							job.xListUnique[i].add(x);
							job.yListUnique[i].add(y);
						}

						flag = false;
					}
				}
			}
		}

		job.colors = new Color3i[job.renderLevels][];

		for (int i = 0; i < job.renderLevels; i++) {
			job.colors[i] = new Color3i[levelCount[i]];
			job.levelHeight[i] = levelCount[i] / job.levelWidth[i];
		}

		System.out.println("Start Render : " + job.getWidth() + "x" + job.getHeight() + " : " + job.renderLevels + " lod");
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
		
		currentLOD = lod;

		// By default, assign threadcount by number of processors
		// In early levels of detail, use single threading, the *128 is arbitrary for
		// now
		threadCount = Runtime.getRuntime().availableProcessors();
		if (job.xListUnique[lod].size() < threadCount * 128) {
			threadCount = 1;
		}

		int threadDiv = job.colors.length / threadCount;
		renderThreads = new RenderThread[threadCount];

		for (int i = 0; i < threadCount; i++) {
			int start = threadDiv * i;
			int end = (threadDiv * (i + 1));

			if (i == threadCount - 1) {
				end = job.xListUnique[lod].size();
			}
			renderThreads[i] = new RenderThread(job, start, end, lod);
			renderThreads[i].start();
		}

		RenderEndThread renderEndThread = new RenderEndThread(job, renderThreads, lod);
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

		previewWindowGeometry.clear();
		previewWindowGeometry.add((Geometry) new Rect(job.getWidth(), job.getHeight(), job.getWidth(), job.getHeight(), textureId));

		// Either render next level, next frame, or finish
		if (lastLevel > 0) {
			renderLevel(job, lastLevel - 1);
		}
		else {
			long renderEndTime = System.currentTimeMillis();
			System.out.println("Render Time : " + (renderEndTime - job.renderStartTime) / 1000.0 + " Seconds");
			flagRendering = false;
			FluxCadd.animating = false;
			saveRenderToFile(job);
			// Go to the next frame if necessary

			renderJobs.pop();
			if (renderJobs.size() > 0) {
				renderJob(renderJobs.getFirst());
			}
		}
	}


	private static void saveRenderToFile(RenderJob job) {
		BufferedImage bi = new BufferedImage(job.getWidth(), job.getHeight(), 3);
		for (int y = 0; y < job.getHeight(); y++) {
			for (int x = 0; x < job.getWidth(); x++) {
				Color3i c = job.colors[0][y * job.getWidth() + x];
				int alpha = 0xFF << 24;
				bi.setRGB(x, y, c.toInt() + alpha);
			}
		}

		try {
			String appPath = new File(".").getCanonicalPath();
			File outFile;

			if (job.scene.name == null) {
				outFile = new File(appPath + "\\output\\renders\\" + Util.getTimestamp() + ".png");
			}
			else if (job.inAnimation) {
				outFile = new File(appPath + "\\output\\renders_named\\" + job.scene.name + "\\frames\\" + job.name + ".png");
			}
			else {
				outFile = new File(appPath + "\\output\\renders_named\\" + job.scene.name + ".png");
			}
			new File(outFile.getParent()).mkdirs();

			System.out.println(outFile);
			ImageIO.write(bi, "png", outFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private Color3i getSDFRayColor(RenderJob job, Vector3d pos, Vector3d vec, int depth) {
		Vector3d hit = rayMarch(job.sdf, pos, vec, null, job.timestamp);

		if (hit == null) {
			return (job.scene.skyColor);
		}

		Material material = job.sdf.getMaterial(hit, job.timestamp);

		double multFactor = 1;

		if (job.renderSettings.useNormalShading || job.renderSettings.useShadows || job.renderSettings.useReflectivity) {
			Vector3d normal = job.sdf.getNormal(hit, job.timestamp);
			Vector3d shadowVector = job.scene.sunPosition.get(job.timestamp).getColumn(3, new Vector3d()).sub(hit).normalize();

			double sunNormalAngle = 1;

			if (job.renderSettings.useReflectivity && material.getReflectivity() > 0 && depth < maxDepth) {
				Vector3d newStart = new Vector3d(normal).mul(0.1).add(hit);
				Color3i reflectedColor = getSDFRayColor(job, newStart, new Vector3d(normal), depth + 1);
				material.lerpTowards(reflectedColor, material.getReflectivity());
			}

			if (job.renderSettings.useNormalShading) {
				sunNormalAngle = 1 - (normal.angle(shadowVector) / Math.PI);
				multFactor = sunNormalAngle;
			}

			if (job.renderSettings.useShadows) {

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
					Vector3d shadowCollision = rayMarch(job.sdf, shadowStarts[i], shadowVector, job.scene.sunPosition.get(job.timestamp).getColumn(3, new Vector3d()),
							job.timestamp);
					if (shadowCollision != null) {
						shadowCount += 1;
					}
				}

				multFactor = UtilMath.lerp(sunNormalAngle, job.scene.ambientLight, 1.0 * shadowCount / (dirCount + 1));
			}
		}

		
		Color3i output = material.getColor().copy();
		output.mult(multFactor);

		return output;
	}


	private static Vector3d rayMarch(SDF sdf, Vector3d pos, Vector3d vec, Vector3d goalPoint, double time) {
		double distanceDelta = 0;

		while (true) {
			double distance = sdf.getDistance(pos, time);

			if (distance <= SDF.epsilon) {
				return pos;
			}

			double marchDistance = distance * SDF.distanceFactor;
			vec.normalize(marchDistance);
			pos.add(vec);
			distanceDelta += marchDistance;

			// Once ray passes the goalpoint, report no obstacles found
			if (goalPoint != null) {
				if (new Vector3d(goalPoint).sub(pos).dot(vec) < 0) {
					return null;
				}
			}

			if (distanceDelta > SDF.farClip) {
				return null;
			}
		}
	}


	/**
	 * Renders a non-marching 2d slice of the scene. Not using threading for now
	 * Currently not used as hasn't been updated for multithreading
	 */
	@Deprecated
	public void render2DSlice(RenderJob job, double z, double time) {
		colorBuffer = ByteBuffer.allocateDirect(job.getWidth() * job.getHeight() * 4);
		colorBuffer.order(ByteOrder.nativeOrder());

		float scale = 8;

		for (int y = 0; y < job.getHeight(); y++) {
			for (int x = 0; x < job.getWidth(); x++) {

				double lx = (x - (job.getWidth() / 2.0)) / scale;
				double ly = (y - (job.getHeight() / 2.0)) / scale;
				Vector3d v = new Vector3d(lx, ly, z);

				double distance = job.sdf.getDistance(v, time);

				int r = 255 - (int) Math.max(0, Math.min((int) Math.abs(distance) * 10.0, 255));
				int g = (int) Math.max(0, Math.min((int) 0, 255));
				int b = (int) Math.max(0, Math.min((int) distance > 0 ? 0 : 255, 255));

				colorBuffer.put((byte) r);
				colorBuffer.put((byte) g);
				colorBuffer.put((byte) b);
				colorBuffer.put((byte) 255);
			}
		}

		colorBuffer.flip();
	}


	/**
	 * Render container for a single frame
	 *
	 */
	private class RenderJob {
		public Scene scene;
		public SDF sdf;

		public double timestamp;
		public String name;

		private int renderWidth;
		private int renderHeight;

		private RenderSettings renderSettings;

		/**
		 * Intermediate render data, outer array is each level of detail Direct colors
		 * for each pixel as returned by the raymarcher, later converted to the
		 * ByteBuffer for display or read directly for saving
		 */
		public volatile Color3i[][] colors;

		/**
		 * Total levels of detail for render process
		 */
		public int renderLevels = -1;

		/**
		 * Dimensions of 'image' at each level of detail
		 */
		public int levelWidth[];
		public int levelHeight[];

		/**
		 * Timestamp for the start of the render
		 */
		private long renderStartTime;
		
		
		/**
		 * Records the number of pixels that have been finished in the job
		 */
		private volatile int finishCounter = 0;
		
		
		/**
		 * X coordinates of new pixels to be rendered at each level of detail
		 */
		private ArrayList<Integer>[] xListUnique;

		/**
		 * Y Coordinates of new pixels to be rendered at each level of detail
		 */
		private ArrayList<Integer>[] yListUnique;
		
		public boolean inAnimation = false;


		public RenderJob(SDF sdf, Scene scene, double timestamp, String name, RenderSettings renderSettings, boolean inAnimation) {
			this.timestamp = timestamp;
			this.name = name;
			this.sdf = sdf;
			this.scene = scene;
			
			this.renderSettings = renderSettings;
			
			this.renderWidth = scene.camera.getPixelWidth();
			this.renderHeight = scene.camera.getPixelHeight();
			
			this.inAnimation = inAnimation;
		}


		public int getWidth() {
			return renderWidth;
		}


		public int getHeight() {
			return renderHeight;
		}

	}


	private class RenderThread extends Thread {
		private int start;
		private int stop;
		private int lod;

		private RenderJob job;


		public RenderThread(RenderJob job, int start, int stop, int lod) {
			this.start = start;
			this.stop = stop;
			this.lod = lod;
			this.job = job;
		}


		@Override
		public void run() {
			for (int i = start; i < stop; i++) {
				if (cancelFlag) {
					break;
				}

				int x = job.xListUnique[lod].get(i);
				int y = job.yListUnique[lod].get(i);

				Vector3d rayPosition = job.scene.camera.getPosition(job.timestamp);
				Vector3d rayVector = job.scene.camera.getRayVector(x, y);

				Color3i c = getSDFRayColor(job, rayPosition, rayVector, 0);

				for (int j = 0; j < job.renderLevels; j++) {
					int lx = x / (1 << j);
					int ly = y / (1 << j);
					int li = ly * job.levelWidth[j] + lx;
					job.colors[j][li] = c;
				}

				job.finishCounter += 1;
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
}
