package render_sdf.sdf;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.StringBuilder;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;

import javax.tools.*;

import org.joml.Vector3d;

import geometry.GeometryDatabase;
import render_sdf.animation.Animated;
import render_sdf.material.Material;

import render_sdf.renderer.MemoryClassLoader;


/**
 * Represents a complete SDF tree as a locally class
 */
public class SDFCompiled extends SDF {
	
	private SDF instance;
	private SDF tree;
	
	public void compileTree(String name, SDF tree, double time, boolean inMemory) {
		
		name = name.replace('-', '_');
		
		this.tree = tree;
		
		long compileTimeStart = System.currentTimeMillis();
		
		HashSet<String> usedNames = new HashSet<String>();
		tree.setCompileNames(usedNames);
		
		ArrayList<String> definitions = new ArrayList<String>();
		ArrayList<String> functions = new ArrayList<String>();
		ArrayList<String> transforms = new ArrayList<String>();
		
		String sourceSDFTree = tree.getSourceRepresentation(definitions, functions, transforms, "v", time);
		
		StringBuilder sourceTotal = new StringBuilder();
		
		sourceTotal.append("package sdf_compiled;\n");
		sourceTotal.append("\n");
		sourceTotal.append("import org.joml.Matrix4d;\n");
		sourceTotal.append("import org.joml.Matrix3x2d;\n");
		sourceTotal.append("import org.joml.SimplexNoise;\n");
		sourceTotal.append("import org.joml.Vector3d;\n");
		sourceTotal.append("import render_sdf.sdf.*;\n");
		sourceTotal.append("import java.util.function.BiFunction;\n");
		sourceTotal.append("import render_sdf.animation.Animated;\n");
		sourceTotal.append("import geometry.GeometryDatabase;\n");
		sourceTotal.append("\n");
		sourceTotal.append("public class CompiledSDF_" + name + " extends SDF {\n");
		
		for (int i = 0; i < definitions.size(); i++) {
			sourceTotal.append(" " + definitions.get(i) + "\n");
			sourceTotal.append("\n");
		}
		
		sourceTotal.append("\n");
		sourceTotal.append("\n");
		
		if (functions.size() > 0) {
			for (int i = 0; i < functions.size(); i++) {
				sourceTotal.append(functions.get(i) + "\n");
				sourceTotal.append("\n");
			}
			
			sourceTotal.append("\n");	
		}
		
		sourceTotal.append("public double getDistance(Vector3d v, double time) {\n");
		
		if (transforms.size() > 0) {
			for (int i = 0; i < transforms.size(); i++) {
				sourceTotal.append(transforms.get(i) + "\n");
				sourceTotal.append("\n");
			}
			
			sourceTotal.append("\n");	
		}
		
		sourceTotal.append("  return " + sourceSDFTree + ";\n");
		sourceTotal.append(" }\n");
		sourceTotal.append("\n");
		sourceTotal.append("\n");
		sourceTotal.append(" @Override\n");
		sourceTotal.append(" public void extractSceneGeometry(GeometryDatabase geometryDatabase, boolean solid, boolean materialPreview, double time) {\n");
		sourceTotal.append(" }\n");
		sourceTotal.append("\n");
		sourceTotal.append("\n");
		sourceTotal.append(" @Override\n");
		sourceTotal.append(" public Animated[] getAnimated() {\n");
		sourceTotal.append("  return null;\n");
		sourceTotal.append(" }\n");
		sourceTotal.append("}");
		
		String sourceFinal = sourceTotal.toString();
		
		long assembleTimeEnd = System.currentTimeMillis();
		System.out.println("Assemble Time : " + ((assembleTimeEnd - compileTimeStart) / 1000.0) + " Seconds");
		
		if (inMemory) {
			instance = compileInMemory(name, sourceFinal);	
		}
		else {
			instance = compileWithFile(name, sourceFinal);
		}
		
		
		long compileTimeEnd = System.currentTimeMillis();
		
		System.out.println("Compile Time : " + ((compileTimeEnd - assembleTimeEnd) / 1000.0) + " Seconds");
	}
	
	/**
	 * Compiles the SDF file using an intermediary file in the temp directory, for debugging.
	 * @param input
	 * @return
	 */
	private static SDF compileWithFile(String name, String input) {
		
		File root = null;
		File sourceFile = null;
		try {
			root = Files.createTempDirectory("fluxcadd").toFile();
			sourceFile = new File(root, "sdf_compiled/CompiledSDF_" + name + ".java");
			sourceFile.getParentFile().mkdirs();
			Files.write(sourceFile.toPath(), input.getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Compile source file.
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		compiler.run(null, null, null, sourceFile.getPath());


		// Load and instantiate compiled class.
		URLClassLoader classLoader = null;
		try {
			classLoader = URLClassLoader.newInstance(new URL[] { root.toURI().toURL() });
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		try {
			System.out.println(sourceFile.toURI().toURL());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Class<?> cls = null;
		try {
			cls = Class.forName("sdf_compiled.CompiledSDF_" + name, true, classLoader);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		SDF instance = null;
		try {
			instance = (SDF) cls.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}

		return instance;
	}
	
	
	/**
	 * Compiles the SDF file in memory. Mildly faster and doesn't create extra files. 
	 * @param input
	 * @return
	 */
	private static SDF compileInMemory(String name, String input) {
		MemoryClassLoader memClassLoader = new MemoryClassLoader();
		Class<?> cls = null;
		try {
			cls = memClassLoader.compileAndLoad("sdf_compiled.CompiledSDF_" + name, input);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		SDF instance = null;
		try {
			instance = (SDF) cls.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		
		return instance;
	}
	

	@Override
	public double getDistance(Vector3d vector, double time) {
		return instance.getDistance(vector, time);
	}
	
	
	@Override
	public Material getMaterial(Vector3d vector, double time) {
		return tree.getMaterial(vector, time);
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase geometryDatabase, boolean solid, boolean materialPreview, double time) {
	}


	@Override
	public Animated[] getAnimated() {
		return null;
	}
}
