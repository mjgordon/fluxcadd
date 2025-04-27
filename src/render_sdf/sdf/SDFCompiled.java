package render_sdf.sdf;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;

import javax.tools.*;

import org.joml.Matrix4d;
import org.joml.Vector3d;

import geometry.GeometryDatabase;
import render_sdf.animation.Animated;
import render_sdf.material.Material;


/**
 * Represents a complete SDF tree as a locally class
 */
public class SDFCompiled extends SDF {
	
	private SDF instance;
	private SDF tree;
	
	public void compileTree(SDF tree, double time) {
		
		this.tree = tree;
		
		long compileTimeStart = System.currentTimeMillis();
		
		HashSet<String> usedNames = new HashSet<String>();
		tree.setCompileNames(usedNames);
		
		ArrayList<String> definitions = new ArrayList<String>();
		ArrayList<String> prelines = new ArrayList<String>();
		
		String source = tree.getSourceRepresentation(definitions, prelines, "v", time);
		
		String sourceTotal = ""
				+ "package sdf_compiled;\n"
				+ "\n"
				+ "import org.joml.Matrix4d;\n"
				+ "import org.joml.Vector3d;\n"
				+ "import render_sdf.sdf.SDF;\n"
				+ "import render_sdf.animation.Animated;\n"
				+ "import geometry.GeometryDatabase;\n"
				+ "\n"
				+ "public class CompiledSDFObject extends SDF {\n";
		
		for (int i = 0; i < definitions.size(); i++) {
			sourceTotal += " " + definitions.get(i) + "\n";
		}
		
		sourceTotal += ""
				+ "\n"
				+ "\n"
				+ " public double getDistance(Vector3d v, double time) {\n"
				+ "  return " + source + ";\n"
				+ " }\n"
				+ "\n"
				+ "\n"
				+ " @Override\n"
				+ " public void extractSceneGeometry(GeometryDatabase geometryDatabase, boolean solid, boolean materialPreview, double time) {\n"
				+ " }\n"
				+ "\n"
				+ "\n"
				+ " @Override\n"
				+ " public Animated[] getAnimated() {\n"
				+ "  return null;\n"
				+ " }\n"
				+ "}";
		
		long assembleTimeEnd = System.currentTimeMillis();
		System.out.println("Assemble Time : " + ((assembleTimeEnd - compileTimeStart) / 1000.0) + " Seconds");
		
		instance = compile(sourceTotal);
		
		long compileTimeEnd = System.currentTimeMillis();
		
		System.out.println("Compile Time : " + ((compileTimeEnd - assembleTimeEnd) / 1000.0) + " Seconds");
	}
	

	private static SDF compile(String input) {
		File root = null;
		File sourceFile = null;
		try {
			root = Files.createTempDirectory("fluxcadd").toFile();
			sourceFile = new File(root, "sdf_compiled/CompiledSDFObject.java");
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
		
		Class<?> cls = null;
		try {
			cls = Class.forName("sdf_compiled.CompiledSDFObject", true, classLoader);
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
