package render_sdf.renderer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JFileChooser;

import org.joml.Vector3d;

import console.Console;
import controller.*;
import geometry.GeometryDatabase;
import geometry.Group;
import geometry.Line;
import main.FluxCadd;
import render_sdf.animation.Content_Animation;
import render_sdf.material.Material;
import render_sdf.material.MaterialDiffuse;
import render_sdf.sdf.*;
import scheme.SchemeEnvironment;
import scheme.SourceFile;
import ui.*;
import utility.Color3i;
import utility.UtilString;
import utility.math.Domain;

/**
 * Contains user interface and controls for SDF rendering
 *
 */
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
	private UIETextField textFieldFrameStart;
	private UIETextField textFieldFrameEnd;

	private Content_View previewWindow;

	private Content_Animation animationWindow;

	private Scene scene;

	private SDF sdfScene;

	private ArrayList<SDF> sdfArray;

	private GeometryDatabase geometryScenePreview;
	private GeometryDatabase geometryRenderPreview;

	private boolean cameraLockedToPreview = true;

	private boolean materialPreview = true;

	private boolean autoUpdate = false;

	private SchemeEnvironment schemeEnvironment;

	// private String sdfFilename = "scripts_sdf/animation_test.scm";
	private String sdfFilename = "test_scripts/testSDFPrimitiveTorus.scm";

	private Renderer renderer;

	private int defaultRenderWidth = 1080;
	private int defaultRenderHeight = 1080;


	public Content_Renderer(Panel parent, Content_View previewWindow, Content_Animation animationWindow) {
		super(parent);

		geometryScenePreview = new GeometryDatabase();
		geometryRenderPreview = new GeometryDatabase();

		setupControl();

		scene = new Scene(defaultRenderWidth, defaultRenderHeight);

		this.previewWindow = previewWindow;
		this.previewWindow.renderGrid = false;
		this.previewWindow.fovDiff = 0.18;

		this.animationWindow = animationWindow;

		resetPreviewGeometry();

		setupSDFSchemeEnvironment();
		loadSDFFromScheme(sdfFilename);

		setViewScenePreview();

		updateCameraLabels(0);

		setParentWindowTitle("SDF Render");

		this.renderer = new Renderer(geometryRenderPreview);
	}


	@Override
	public void render() {
		double time = renderer.getCurrentJobTime();
		if (Double.isNaN(time)) {
			time = animationWindow.getTime();
		}
		previewWindow.time = time;

		controllerManager.render();

		progressBar.update(1.0f * renderer.getFinishCount() / renderer.getCurrentJobPixelCount());
		finishCounterLabel.setText("Finish Counter : " + renderer.getFinishCount() + "");

		progressBar.setDisplayName("Render Progress | Level : " + renderer.getCurrentLOD() + " | Threadcount : " + renderer.getCurrentThreadCount());

		renderer.finalizeLevels();
	}


	@Override
	public void resizeRespond(int newWidth, int newHeight) {
		controllerManager.setWidth(newWidth);
		controllerManager.setHeight(newHeight - this.parent.barHeight);
		controllerManager.reflow();
	}


	/**
	 * Create scheme environment and load system scheme scripts
	 */
	private void setupSDFSchemeEnvironment() {
		schemeEnvironment = new SchemeEnvironment();
		try {
			SourceFile systemSDFFile = new SourceFile("scheme/system-sdf.scm");
			schemeEnvironment.evalMultiple(systemSDFFile.fullFile);
		} catch (Exception e) {
			System.out.println(e);
		}
	}


	/**
	 * Load an SDF scene by evaluating the .scm file at filename.
	 * 
	 * @param filepath
	 */
	private void loadSDFFromScheme(String filepath) {
		scene = new Scene(defaultRenderWidth, defaultRenderHeight);
		schemeEnvironment.call("set-scene-render", scene);
		try {
			SourceFile sdfFile = new SourceFile(filepath);
			schemeEnvironment.evalMultiple(sdfFile.fullFile);
			sdfScene = (SDF) schemeEnvironment.eval("scene-sdf");
		} catch (Exception e) {
			Console.log("Scheme SDF Exception: " + e);
		}
		
		copyCameraToView(0);

		resetPreviewGeometry();
		sdfScene.extractSceneGeometry(geometryScenePreview, true, materialPreview, animationWindow.getTime());

		// Create sun preview geometry
		{
			Group g = new Group();
			double hp = 10.0;
			Color3i c = new Color3i(255, 255, 0);
			g.add(new Line(new Vector3d(-hp, 0, 0), new Vector3d(hp, 0, 0)).setFillColor(c));
			g.add(new Line(new Vector3d(0, -hp, 0), new Vector3d(0, hp, 0)).setFillColor(c));
			g.add(new Line(new Vector3d(0, 0, -hp), new Vector3d(0, 0, hp)).setFillColor(c));
			g.setMatrix(scene.sunPosition);
			geometryScenePreview.add(g);	
		}
		
		// Reset UI elements
		this.textfieldSDFObjectList.setValue(sdfScene.describeTree("", 0, "", true), true);
		this.textFieldFrameStart.setValue(scene.frameStart + "", true);
		this.textFieldFrameEnd.setValue(scene.frameEnd + "", true);

		sdfArray = sdfScene.getArray();

		scene.camera.updateMatrix(0);
		
		// Set file chooser
		Path pathCWD = Paths.get("");
		String cwd = pathCWD.toAbsolutePath().toString();
		Path pathFilepath = Paths.get(filepath);
		String displayString = filepath;
		if (pathFilepath.isAbsolute()) {
			if (filepath.contains(cwd)) {
				displayString = "[FLUX]/" + displayString.substring(cwd.length());
			}
		}
		else {
			displayString = "[FLUX]/" + filepath;
		}
		fileChooser.setValue(displayString, true);
	}


	/**
	 * TODO: Move this to an external script or delete
	 */
	@SuppressWarnings("unused")
	private void setup2DDemo() {
		Material materialMain = new MaterialDiffuse(new Color3i(0xFF0000), 0);

		sdfScene = new SDFPrimitiveCross(new Vector3d(0, 0, 0), 75, materialMain);
		sdfScene = new SDFOpChamfer(sdfScene, new SDFPrimitiveCube(new Vector3d(200, 0, 0), 200, materialMain), 50);
		sdfScene = new SDFOpFillet(sdfScene, new SDFPrimitiveCube(new Vector3d(-200, 0, 0), 200, materialMain), 100);
	}


	private void resetPreviewGeometry() {
		geometryScenePreview.clear();
		geometryScenePreview.add(scene.camera.getGeometryFirstPerson());
		geometryScenePreview.add(scene.camera.getGeometryThirdPerson());

		scene.camera.getGeometryFirstPerson().visible = true;
		scene.camera.getGeometryThirdPerson().visible = false;
	}


	/**
	 * Updates the associated preview window to show the most recent complete frame
	 * or frame preview
	 */
	private void setViewRenderPreview() {
		this.previewWindow.changeType(ViewType.TOP, true);
		this.previewWindow.renderGrid = false;
		double scaleFactor = Math.min(0.5 * previewWindow.getWidth() / renderer.getCurrentJobResolutionWidth(),
				0.5 * previewWindow.getHeight() / renderer.getCurrentJobResolutionHeight());
		this.previewWindow.setScaleFactor(scaleFactor);
		this.previewWindow.setOrthoTarget(new Vector3d(-1080 * scaleFactor, -1080 * scaleFactor, 0));

		this.previewWindow.geometry = geometryRenderPreview;
	}


	/**
	 * Updates the associated preview window to show the SDF preview geometry
	 */
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

		cameraPositionX.setValue(cameraPosition.x + "", true);
		cameraPositionY.setValue(cameraPosition.y + "", true);
		cameraPositionZ.setValue(cameraPosition.z + "", true);

		cameraTargetX.setValue(cameraTarget.x + "", true);
		cameraTargetY.setValue(cameraTarget.y + "", true);
		cameraTargetZ.setValue(cameraTarget.z + "", true);
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
	protected void mouseWheel(int mouseX, int mouseY, int dy) {
		controllerManager.mouseWheel(mouseX, mouseY, dy);
	}


	@Override
	protected void mousePressed(int button, int mouseX, int mouseY) {
		if (button == 0) {
			controllerManager.mousePressed(mouseX, mouseY);
		}
	}


	@Override
	protected void mouseDragged(int button, int x, int y, int dx, int dy) {
		controllerManager.mouseDragged(button, x, y, dx, dy);
	}


	@Override
	protected void mouseReleased(int button) {
		controllerManager.mouseReleased();
	}


	private void setupControl() {
		controllerManager = new UIEControlManager(0, parent.barHeight, getWidth(), getHeight() - parent.barHeight, 10, 10, 10, 10, true);

		// === Toggle Autoupdate ===
		controllerManager.add(new UIEToggle("autoupdate", "Auto-Update", 0, 0, 20, 20).setCallback((toggle) -> {
			autoUpdate = toggle.state;
			// TODO: Implement autoupdate
		}));

		controllerManager.add(new UIEButton("update_manual", "Update", 0, 0, 20, 20).setCallback((button) -> {
			loadSDFFromScheme(sdfFilename);
		}));

		controllerManager.newLine();

		// === File Chooser ===
		fileChooser = new UIEFileChooser("fileChooser", "File Chooser", 0, 0, -1, 20, controllerManager, true, false).setCallback((fc) -> {
			String filename = fc.getCurrentString();
			sdfFilename = filename;
			loadSDFFromScheme(sdfFilename);
		});
		controllerManager.add(fileChooser);

		controllerManager.newLine();

		// === SDF Object List ===
		textfieldSDFObjectList = new UIETextField("sdf_object_list", "SDF Objects", 0, 0, -1, 200).setClearOnExecute(false).setCallback((tf) -> {
			int selectedLine = tf.getSelectedLine();
			if (selectedLine < sdfArray.size()) {
				animationWindow.setAnimated(sdfArray.get(selectedLine).getAnimated());
			}

		});
		textfieldSDFObjectList.setValue("abcdefghijklmnopqrs\ntuvwxyz0123456789.,/_-()", true);
		textfieldSDFObjectList.editable = false;
		controllerManager.add(textfieldSDFObjectList);

		controllerManager.newLine();

		// === Camera Position ===
		{
			UIEVerticalStack stackPosition = new UIEVerticalStack("stack_position", "", 0, 0, 120, 0);
			stackPosition.add(new UIELabel("camera_position_label", "Camera Position", 0, 0, 100, 20));
			cameraPositionX = new UIETextField("camera_position_x", "X", 0, 0, 100, 20).setClearOnExecute(false).setCallback((tf) -> {
				Vector3d pos = scene.camera.getPosition(0);
				try {
					pos.x = Double.parseDouble(tf.getValue());
				} catch (Exception e) {
					tf.setValue(pos.x + "", true);
				}
				scene.camera.setPositionKeyframe(0, pos);
			});
			stackPosition.add(cameraPositionX);

			cameraPositionY = new UIETextField("camera_position_y", "Y", 0, 0, 100, 20).setClearOnExecute(false).setCallback((tf) -> {
				Vector3d pos = scene.camera.getPosition(0);
				try {
					pos.y = Double.parseDouble(tf.getValue());
				} catch (Exception e) {
					tf.setValue(pos.y + "", true);
				}
				scene.camera.setPositionKeyframe(0, pos);
			});
			stackPosition.add(cameraPositionY);

			cameraPositionZ = new UIETextField("camera_position_y", "Z", 0, 0, 100, 20).setClearOnExecute(false).setCallback((tf) -> {
				Vector3d pos = scene.camera.getPosition(0);
				try {
					pos.z = Float.parseFloat(tf.getValue());
				} catch (Exception e) {
					tf.setValue(pos.z + "", true);
				}
				scene.camera.setPositionKeyframe(0, pos);
			});
			stackPosition.add(cameraPositionZ);
			stackPosition.close();
			controllerManager.add(stackPosition);
		}

		// === Camera Target ===
		{
			UIEVerticalStack stackTarget = new UIEVerticalStack("stack_target", "", 0, 0, 120, 0);
			stackTarget.add(new UIELabel("camera_target_label", "Camera Target", 0, 0, 100, 20));
			cameraTargetX = new UIETextField("camera_target_x", "X", 0, 0, 100, 20).setClearOnExecute(false);
			stackTarget.add(cameraTargetX);
			cameraTargetY = new UIETextField("camera_target_y", "Y", 0, 0, 100, 20).setClearOnExecute(false);
			stackTarget.add(cameraTargetY);
			cameraTargetZ = new UIETextField("camera_target_y", "Z", 0, 0, 100, 20).setClearOnExecute(false);
			stackTarget.add(cameraTargetZ);
			stackTarget.close();
			controllerManager.add(stackTarget);
		}

		// === Camera Controls ===
		{
			UIEVerticalStack stackLock = new UIEVerticalStack("stack_lock", "", 0, 0, 120, 0);
			stackLock.add(new UIELabel("camera_lock_label", "Camera Sync", 0, 0, 100, 20));
			stackLock.add(new UIEButton("button_preview_to_cam", "Set Camera to Viewer", 0, 0, 20, 20).setCallback((button) -> {
				FluxCadd.forceRedraw = true;
			}));
			stackLock.add(new UIEButton("button_cam_to_preview", "Set Viewer to Camera", 0, 0, 20, 20).setCallback((button) -> {
				copyCameraToView(0);
				FluxCadd.forceRedraw = true;
			}));
			stackLock.add(new UIEToggle("toggle_lock_cam", "Lock Camera Preview", 0, 0, 20, 20).setCallback((toggle) -> {
				cameraLockedToPreview = toggle.state;

				scene.camera.getGeometryFirstPerson().visible = cameraLockedToPreview;
				scene.camera.getGeometryThirdPerson().visible = !cameraLockedToPreview;

				FluxCadd.forceRedraw = true;
			}));
			stackLock.close();
			controllerManager.add(stackLock);
		}

		controllerManager.newLine();

		// === Render Settings ===
		UIEToggle toggleReflectivity = new UIEToggle("t_reflectivity", "Reflectivity", 0, 0, 20, 20);
		UIEToggle toggleShadow = new UIEToggle("t_shadow", "Shadow", 0, 0, 20, 20);
		UIEToggle toggleShading = new UIEToggle("t_shading", "Shading", 0, 0, 20, 20);

		// === Button Render ===
		UIEButton buttonRender = new UIEButton("button_render", "Render", 0, 0, 20, 20).setCallback((button) -> {

			SDFCompiled sdfCompiled = new SDFCompiled();
			sdfCompiled.compileTree(scene.name, sdfScene , animationWindow.getTime(), true);

			RenderSettings renderSettings = new RenderSettings(toggleShading.state, toggleReflectivity.state, toggleShadow.state);
			renderer.addJob(sdfCompiled, scene, animationWindow.getTime(), "s" + UtilString.leftPad((int) animationWindow.getTime() + "", 5), renderSettings, false);
			renderer.startRenderingJobs();
			renderJobLabel.setText("Render Jobs: " + renderer.getJobCount());
			setViewRenderPreview();
		});
		controllerManager.add(buttonRender);

		// === Button Cancel ===
		UIEButton buttonCancel = new UIEButton("button_cancel", "Cancel", 0, 0, 20, 20).setCallback((button) -> {
			renderer.cancelRendering();
			progressBar.update(0);
			setViewScenePreview();
		});
		controllerManager.add(buttonCancel);

		// === Button Result ===
		UIEButton buttonResult = new UIEButton("button_result", "Result", 0, 0, 20, 20);
		controllerManager.add(buttonResult);

		// === Button Render 2D ===
		UIEButton buttonRender2D = new UIEButton("button_render_2d", "Render 2D", 0, 0, 20, 20).setCallback((button) -> {
			/*
			 * Renderer.RenderJob job = renderer.new RenderJob(sdfScene, scene,
			 * animationWindow.getTime(), "s" + UtilString.leftPad((int)
			 * animationWindow.getTime() + "", 5), toggleShadow.state, toggleShading.state,
			 * toggleReflectivity.state); renderer.render2DSlice(job, 15.99, 0);
			 * setViewRenderPreview();
			 */
		});
		controllerManager.add(buttonRender2D);

		// === Button Render Directory ===
		UIEButton buttonRenderDir = new UIEButton("button_render_dir", "Render Dir", 0, 0, 20, 20).setCallback((button) -> {

			RenderSettings renderSettings = new RenderSettings(toggleShading.state, toggleReflectivity.state, toggleShadow.state);

			Path pathCWD = Paths.get("");
			String cwd = pathCWD.toAbsolutePath().toString();
			JFileChooser chooser = new JFileChooser(cwd);

			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = chooser.showOpenDialog(null);
			System.out.println(returnVal);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File directory = chooser.getSelectedFile();

				for (final File fileEntry : directory.listFiles()) {
					if (!fileEntry.isDirectory()) {
						loadSDFFromScheme(fileEntry.getAbsolutePath());
						
						SDFCompiled sdfCompiled = new SDFCompiled();
						sdfCompiled.compileTree(scene.name,  sdfScene, 0, true);
						
						renderer.addJob(sdfCompiled, scene, 0, UtilString.leftPad(0 + "", 5), renderSettings, false);
					}
				}
			}

			renderer.startRenderingJobs();
			renderJobLabel.setText("Render Jobs: " + renderer.getJobCount());
			setViewRenderPreview();
		});
		controllerManager.add(buttonRenderDir);

		controllerManager.newLine();

		// === Button Render Animation
		UIEButton buttonRenderAnimation = new UIEButton("button_render_animation", "Render Animation", 0, 0, 20, 20).setCallback((button) -> {
			RenderSettings renderSettings = new RenderSettings(toggleShading.state, toggleReflectivity.state, toggleShadow.state);
			for (int i = scene.frameStart; i < scene.frameEnd; i++) {
				renderer.addJob(sdfScene, scene, i, UtilString.leftPad(i + "", 5), renderSettings, true);
			}
			renderer.startRenderingJobs();
			renderJobLabel.setText("Render Jobs: " + renderer.getJobCount());
			setViewRenderPreview();
		});
		controllerManager.add(buttonRenderAnimation);

		// === Text Field Frame Start ===
		textFieldFrameStart = new UIETextField("animation_frame_start", "Frame Start", 0, 0, 100, 20, 1, new Domain(0, 1000), 1).setCallback((textfield) -> {
			scene.frameStart = (int) textfield.getBackingDouble();
		});
		controllerManager.add(textFieldFrameStart);

		// === Text Field Frame End ===
		textFieldFrameEnd = new UIETextField("animation_frame_end", "Frame End", 0, 0, 100, 20, 480, new Domain(0, 1000), 1).setCallback((textfield) -> {
			scene.frameEnd = (int) textfield.getBackingDouble();
		});
		controllerManager.add(textFieldFrameEnd);

		controllerManager.newLine();

		// === Label Finish Counter ===
		finishCounterLabel = new UIELabel("finish_counter", "Finish Counter : ", 0, 0, 250, 20);
		controllerManager.add(finishCounterLabel);

		// === Label Job Counter === 
		renderJobLabel = new UIELabel("render_job_counter", "Render Jobs : ", 0, 0, 100, 20);
		controllerManager.add(renderJobLabel);

		controllerManager.newLine();

		// === Progress Bar ===
		progressBar = new UIEProgressBar("progress_bar", "Render Progress", 0, 0, -1, 20, 1.0f);
		controllerManager.add(progressBar);
		controllerManager.newLine();

		controllerManager.newLine();

		{
			UIEVerticalStack stackFOV = new UIEVerticalStack("stack_fov", "", 0, 0, 120, 0);
			stackFOV.add(new UIELabel("fov_label", "FOV", 0, 0, 100, 20));
			stackFOV.add(new UIETextField("camera_fov", "Camera FOV", 0, 0, 100, 20, 45, new Domain(0, 180), 1).setClearOnExecute(false).setCallback((tf) -> {
				scene.camera.setFOV(Math.toRadians(tf.getBackingDouble()));
				scene.camera.updateGeometry(animationWindow.getTime());
				previewWindow.fov = scene.camera.getFOV();
			}));
			stackFOV.add(new UIETextField("scene_fov", "Preview FOV Offset", 0, 0, 100, 20, 0.18, new Domain(0, 1), 0.01).setClearOnExecute(false).setCallback((tf) -> {
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
