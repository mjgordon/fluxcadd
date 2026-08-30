package render_sdf.renderer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

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
		
		sdf.setCompileNames(new HashSet<String>());
		ArrayList<String> linesSDF = new ArrayList<String>();
		sdf.getGLSLRepresentation("position", linesSDF);
		source.add("float programDistance(vec3 position)");
		source.add("{");
		for (String line : linesSDF) {
			source.add(line);
		}
		source.add("  return dist" + sdf.getCompileName() + ";");
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
