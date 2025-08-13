package fluxcadd;

import org.joml.Vector3d;
import org.junit.jupiter.api.*;

import render_sdf.renderer.Scene;
import render_sdf.renderer.VectorContext;
import render_sdf.sdf.SDF;
import render_sdf.sdf.SDFCompiled;
import scheme.SchemeEnvironment;
import scheme.SourceFile;

/** 
 * Tests simple instantiation and basic distance call for all SDF objects. 
 * Only fails on exceptions.
 */
@SuppressWarnings("static-method")
class TestSDFObjects {
	static SchemeEnvironment schemeEnvironment;
	static Scene scene;
	
	@BeforeAll
	static void setupEnvironment() {
		schemeEnvironment = new SchemeEnvironment();
		try {
			SourceFile systemSDFFile = new SourceFile("scheme/system-sdf.scm");
			schemeEnvironment.evalMultiple(systemSDFFile.fullFile);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	
	@BeforeEach
	void resetScene() {
		scene = new Scene(1080, 1080);
		schemeEnvironment.call("set-scene-render", scene);
	}
	
	
	@Test
	void testSDFBoolDifference() {
		loadAndQuerySDF("test_scripts/testSDFBoolDifference.scm");
	}
	
	
	@Test
	void testSDFBoolIntersection() {
		loadAndQuerySDF("test_scripts/testSDFBoolIntersection.scm");
	}
	
	
	@Test
	void testSDFBoolUnion() {
		loadAndQuerySDF("test_scripts/testSDFBoolUnion.scm");
	}
	
	
	@Test
	void testSDFOpAdd() {
		loadAndQuerySDF("test_scripts/testSDFOpAdd.scm");
	}
	
	
	@Test
	void testSDFOpAddConstant() {
		loadAndQuerySDF("test_scripts/testSDFOpAddConstant.scm");
	}
	
	
	@Test
	void testSDFOpAverage() {
		loadAndQuerySDF("test_scripts/testSDFOpAverage.scm");
	}
	
	
	@Test
	void testSDFOpChamfer() {
		loadAndQuerySDF("test_scripts/testSDFOpChamfer.scm");
	}
	
	
	@Test
	void testSDFOpFillet() {
		loadAndQuerySDF("test_scripts/testSDFOpFillet.scm");
	}
	
	
	@Test
	void testSDFOpLerp() {
		loadAndQuerySDF("test_scripts/testSDFOpLerp.scm");
	}
	
	
	@Test
	void testSDFOpModulo() {
		loadAndQuerySDF("test_scripts/testSDFOpModulo.scm");
	}
	
	
	@Test
	void testSDFOpSmooth() {
		loadAndQuerySDF("test_scripts/testSDFOpSmooth.scm");
	}
	
	
	@Test
	void testSDFOpSubtract() {
		loadAndQuerySDF("test_scripts/testSDFOpSubtract.scm");
	}
	
	
	@Test
	void testSDFOpSubtractConstant() {
		loadAndQuerySDF("test_scripts/testSDFOpSubtractConstant.scm");
	}
	
	
	@Test
	void testSDFOpTransform() {
		loadAndQuerySDF("test_scripts/testSDFOpTransform.scm");
	}
	

	@Test
	void testSDFPrimitiveCross() {
		loadAndQuerySDF("test_scripts/testSDFPrimitiveCross.scm");
	}
	
	
	@Test
	void testSDFPrimitiveCube() {
		loadAndQuerySDF("test_scripts/testSDFPrimitiveCube.scm");
	}
	
	
	@Test
	void testSDFPrimitiveCylinder() {
		loadAndQuerySDF("test_scripts/testSDFPrimitiveCylinder.scm");
	}
	
	
	@Test
	void testSDFPrimitiveDiamond() {
		loadAndQuerySDF("test_scripts/testSDFPrimitiveDiamond.scm");
	}
	
	
	@Test
	void testSDFPrimitiveGroundPlane() {
		loadAndQuerySDF("test_scripts/testSDFPrimitiveGroundPlane.scm");
	}
	
	
	@Test
	void testSDFPrimitiveSimplex() {
		loadAndQuerySDF("test_scripts/testSDFPrimitiveSimplex.scm");
	}
	
	
	@Test
	void testSDFPrimitiveSphere() {
		loadAndQuerySDF("test_scripts/testSDFPrimitiveSphere.scm");
	}
	
	
	@Test
	void testSDFPrimitiveStar() {
		loadAndQuerySDF("test_scripts/testSDFPrimitiveStar.scm");
	}
	
	
	@Test
	void testSDFPrimitiveStarError0() {
		loadAndQuerySDF("test_scripts/testSDFPrimitiveStarError0.scm");
	}
	
	
	@Test
	void testSDFPrimitiveStarError1() {
		loadAndQuerySDF("test_scripts/testSDFPrimitiveStarError1.scm");
	}
	
	
	@Test
	void testSDFPrimitiveTorus() {
		loadAndQuerySDF("test_scripts/testSDFPrimitiveTorus.scm");
	}
	
	
	@Test
	void testSDFAnimationCompilation() {
		loadAndQuerySDFWithAnimation("test_scripts/testSDFAnimation.scm");
	}
	
	
	static void loadAndQuerySDF(String filepath) {
		SourceFile sdfFile = new SourceFile(filepath);
		schemeEnvironment.evalMultiple(sdfFile.fullFile);
		SDF sdf =  (SDF) schemeEnvironment.eval("scene-sdf");
		
		Vector3d start = new Vector3d(20, 20, 30);
		int time = 0;
		
		VectorContext context = new VectorContext();
		double distance = sdf.getDistance(start, time, context);
		System.out.println("Tested : " + filepath + " : " + distance);
		
		SDFCompiled compiledFile = new SDFCompiled();
		compiledFile.compileTree("testFile", sdf, 0, false);
		double distanceFile = compiledFile.getDistance(start, time, context);
		System.out.println("File : " + distanceFile);
		
		SDFCompiled compiledMemory = new SDFCompiled();
		compiledMemory.compileTree("testMemory", sdf, 0, true);
		double distanceMemory = compiledMemory.getDistance(start, time, context);
		System.out.println("Memory : " + distanceMemory);
		
		assert distance == distanceFile;
		assert distance == distanceMemory;
	}
	
	
	static void loadAndQuerySDFWithAnimation(String filepath) {
		SourceFile sdfFile = new SourceFile(filepath);
		schemeEnvironment.evalMultiple(sdfFile.fullFile);
		SDF sdf =  (SDF) schemeEnvironment.eval("scene-sdf");
		
		Vector3d start = new Vector3d(20, 20, 30);
		int time1 = 0;
		int time2 = 50;
		
		VectorContext context = new VectorContext();
		double distance = sdf.getDistance(start, time1, context);
		double distance2 = sdf.getDistance(start, time2, context);
		System.out.println("Tested : " + filepath + " : " + distance);
		
		SDFCompiled compiledFile = new SDFCompiled();
		compiledFile.compileTree("testFile", sdf, 0, false);
		double distanceFile = compiledFile.getDistance(start, time1, context);
		double distanceFile2 = compiledFile.getDistance(start, time2, context);
		System.out.println("File : " + distanceFile);
		
		SDFCompiled compiledMemory = new SDFCompiled();
		compiledMemory.compileTree("testMemory", sdf, 0, true);
		double distanceMemory = compiledMemory.getDistance(start, time1, context);
		double distanceMemory2 = compiledMemory.getDistance(start, time2, context);
		System.out.println("Memory : " + distanceMemory);
		
		assert distance == distanceFile;
		assert distance == distanceMemory;
		
		assert distance2 == distanceFile2;
		assert distance2 == distanceMemory2;
		
		assert distance != distance2;
	}
}
