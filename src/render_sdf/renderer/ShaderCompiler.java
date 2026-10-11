package render_sdf.renderer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import graphics.Shader;
import render_sdf.sdf.SDF;

public class ShaderCompiler {
	
	private static void loadLines(ArrayList<String> source, String path) {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(path));
			String line = br.readLine();
			while (line != null) {
				source.add(line);
				line = br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	
	public static Shader compileShader(SDF sdf, boolean testMode, boolean save, String outputName) {
		ArrayList<String> source = new ArrayList<String>();
		
		loadLines(source, "shaders_compute/render_sdf_functions.glsl");
		
		sdf.generateCompileNames();
		LinkedHashSet<String> linesSDFDistance = new LinkedHashSet<String>();
		sdf.getGLSLDistanceRepresentation("position", linesSDFDistance);
		source.add("float programDistance(vec3 position)");
		source.add("{");
		for (String line : linesSDFDistance) {
			source.add(line);
		}
		source.add("  return dist" + sdf.getCompileName() + ";");
		source.add("}\n\n\n");
		
		LinkedHashSet<String> linesSDFMaterial = new LinkedHashSet<String>();
		LinkedHashSet<String> linesSDFMaterialPredefinitions = new LinkedHashSet<String>();
		sdf.getGLSLMaterialRepresentation("position", linesSDFMaterial, linesSDFMaterialPredefinitions);
		for (String line : linesSDFMaterialPredefinitions) {
			source.add(line);
		}
		source.add("\n\nMaterial programMaterial(vec3 position)");
		source.add("{");
		for (String line : linesSDFMaterial) {
			source.add(line);
		}
		source.add(" return mat" + sdf.getCompileName() + ";");
		source.add("}");
		
		
		String pathMain = testMode ? "shaders_compute/render_sdf_test.glsl" : "shaders_compute/render_sdf_main.glsl";
		loadLines(source, pathMain);
	
		if (save) {
			FileWriter writer;
			try {
				String outputFilename = outputName == null ? "sdf_compute.glsl" : outputName; 
				writer = new FileWriter("shaders_generated/" + outputFilename);
				for(String str: source) {
				  writer.write(str + System.lineSeparator());
				}
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
		Shader output = new Shader(source);
		
		return output;
	}
}
